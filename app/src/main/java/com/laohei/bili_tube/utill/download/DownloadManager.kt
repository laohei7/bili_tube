package com.laohei.bili_tube.utill.download

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.MERGE_SOURCE_KEY
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.util.PreferencesUtil
import com.laohei.bili_tube.db.BiliTubeDB
import com.laohei.bili_tube.model.DownloadStatus
import com.laohei.bili_tube.model.DownloadTask
import io.ktor.client.HttpClient
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.contentLength
import io.ktor.util.collections.ConcurrentMap
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset

class DownloadManager(
    private val context: Context,
    client: HttpClient,
    private val biliTubeDB: BiliTubeDB,
    private val preferencesUtil: PreferencesUtil
) {
    companion object {
        private val TAG = DownloadManager::class.simpleName
        private const val DBG = true
    }

    private val mCoroutineScope = CoroutineScope(Dispatchers.IO)


    private val mutex = Mutex()
    private val _downloadQueue = MutableStateFlow<List<DownloadTask>>(emptyList())
    val downloadQueue = _downloadQueue.asStateFlow()
    private val semaphore = Semaphore(3)

    private var notificationHelper = DownloadNotificationHelper(context)

    private var downloader = VideoAudioDownloader(client)

    private val mJobMap = ConcurrentMap<String, Job>()

    init {
        mCoroutineScope.launch {
            val tasks = biliTubeDB.downloadTaskDao().getAllTask()
            val deletedTasks = tasks.fastFilter {
                val isCompleted = it.status == DownloadStatus.COMPLETED
                val hasVideoAndAudio =
                    it.videoFile.isNullOrBlank().not() && File(it.videoFile!!).exists()
                            && it.audioFile.isNullOrBlank().not() && File(it.audioFile!!).exists()
                val hasMerged =
                    it.mergedFile.isNullOrBlank().not() && File(it.mergedFile!!).exists()

                isCompleted && hasVideoAndAudio.not() && hasMerged.not()
            }
            _downloadQueue.update {
                (tasks - deletedTasks.toSet()).map {
                    if (it.status == DownloadStatus.DOWNLOADING) {
                        it.copy(status = DownloadStatus.PAUSE)
                    } else {
                        it
                    }
                }
            }
            biliTubeDB.downloadTaskDao().deleteTasks(deletedTasks)
            startNextDownload()
        }
    }

    fun addTask(
        id: String,
        aid: Long,
        cid: Long,
        name: String? = null,
        cover: String,
        quality: String,
        videoUrls: List<String>,
        audioUrls: List<String>?,
        archive: String? = null
    ) {
        val task = DownloadTask(
            id = id,
            aid = aid,
            cid = cid,
            name = name ?: id,
            archive = archive,
            cover = cover,
            quality = quality,
            videoUrls = videoUrls,
            audioUrls = audioUrls
        )
        mCoroutineScope.launch {
            mutex.withLock {
                val message = if (_downloadQueue.value.any { it.id == task.id }) {
                    context.getString(R.string.str_download_exit)
                } else {
                    _downloadQueue.value += task
                    biliTubeDB.downloadTaskDao().addTask(task)
                    context.getString(R.string.str_download_add)
                }
                EventBus.send(
                    Event.PlayerEvent.SnackbarEvent(message)
                )
            }
            startNextDownload()
        }
    }

    suspend fun deleteTask(task: DownloadTask) = withContext(Dispatchers.IO) {
        pauseTask(task)
        val deletedTasks: List<DownloadTask>
        mutex.withLock {
            deletedTasks = _downloadQueue.value.filter { it.id == task.id }
            _downloadQueue.update { it - deletedTasks.toSet() }
        }
        biliTubeDB.downloadTaskDao().deleteTasks(deletedTasks)
        deletedTasks.fastForEach { item ->
            item.mergedFile?.let { File(it).delete() }
            item.videoFile?.let { File(it).delete() }
            item.audioFile?.let { File(it).delete() }
        }
    }

    suspend fun pauseTask(task: DownloadTask) = withContext(Dispatchers.IO) {
        val selectedJob = mJobMap[task.id]
        if (selectedJob == null || selectedJob.isCancelled) {
            return@withContext
        }
        if (DBG) {
            Log.d(TAG, "pauseTask: $selectedJob")
        }
        selectedJob.cancel()
        updateTaskStatus(task = task, progress = task.progress, status = DownloadStatus.PAUSE)
    }

    suspend fun startTask(task: DownloadTask) = withContext(Dispatchers.IO) {
        updateTaskStatus(task = task, progress = task.progress, status = DownloadStatus.PENDING)
        val pendingTask: DownloadTask
        mutex.withLock {
            pendingTask =
                _downloadQueue.value.find { it.id == task.id }
                    ?: return@withContext
        }
        if (DBG) {
            Log.d(TAG, "startTask: $pendingTask")
        }
        val currentJob = mJobMap[pendingTask.id]
        if (currentJob?.isActive == true) {
            return@withContext
        }
        downloadTaskAsync(pendingTask)
    }

    private suspend fun startNextDownload() {
        val pendingTask: DownloadTask
        mutex.withLock {
            pendingTask =
                _downloadQueue.value.firstOrNull { it.status == DownloadStatus.PENDING }
                    ?: return
        }
        if (DBG) {
            Log.d(TAG, "startNextDownload: $pendingTask")
        }
        downloadTaskAsync(pendingTask)
    }

    private fun downloadTaskAsync(task: DownloadTask) {
        val job = mCoroutineScope.launch { downloadTask(task) }
        Log.d(TAG, "downloadTaskAsync: $job")
        mJobMap[task.id] = job
    }

    private suspend fun downloadTask(task: DownloadTask) {
        semaphore.withPermit {
            updateTaskStatus(
                task = task,
                progress = task.progress,
                status = DownloadStatus.DOWNLOADING
            )

            val isSingleFile = task.audioUrls.isNullOrEmpty()
            suspend fun adjustAndPostProgress(progress: Int, base: Int = 0) {
                val newProgress = if (isSingleFile) {
                    progress
                } else {
                    (progress / 2).coerceIn(0..50) + base
                }
                updateTaskStatus(
                    task = task,
                    progress = newProgress,
                    status = DownloadStatus.DOWNLOADING
                )
            }

            var videoFile: File? = null
            var audioFile: File? = null
            if (DBG) {
                Log.d(TAG, "downloadTask: video [ ${task.name} ] start download...")
            }
            for ((index, url) in task.videoUrls.withIndex()) {
                try {
                    if (DBG) {
                        Log.d(TAG, "downloadTask: video try source $index; url [ $url ]")
                    }
                    videoFile =
                        downloader.download(
                            url = url,
                            fileName = "video_${task.id}",
//                            parentDir = context.cacheDir
                        ) { progress ->
                            adjustAndPostProgress(progress)
                        }

                    if (videoFile != null) {
                        if (DBG) {
                            Log.d(TAG, "downloadTask: video download success ${videoFile.length()}")
                        }
                        break
                    }
                } catch (e: Exception) {
                    if (DBG) {
                        Log.d(TAG, "downloadTask: video try source error $index; url [ $url ]")
                        Log.d(TAG, "downloadTask: video error msg [ ${e.message} ]")
                    }
                    continue
                }
            }

            task.audioUrls?.run {
                if (DBG) {
                    Log.d(TAG, "downloadTask: audio [ ${task.name} ] start download...")
                }
                for ((index, url) in this.withIndex()) {
                    try {
                        if (DBG) {
                            Log.d(TAG, "downloadTask: audio try source $index; url [ $url ]")
                        }
                        audioFile = downloader.download(
                            url = url,
                            fileName = "audio_${task.id}",
//                            parentDir = context.cacheDir
                        ) { progress ->
                            adjustAndPostProgress(progress, 50)
                        }

                        if (audioFile != null) {
                            if (DBG) {
                                Log.d(
                                    TAG,
                                    "downloadTask: audio download success ${audioFile!!.length()}"
                                )
                            }
                            break
                        }
                    } catch (e: Exception) {
                        if (DBG) {
                            Log.d(TAG, "downloadTask: audio try source error $index; url [ $url ]")
                            Log.d(TAG, "downloadTask: audio error msg [ ${e.message} ]")
                        }
                        continue
                    }
                }
            }
            when {
                videoFile != null && audioFile != null -> {
                    val mergeSource = preferencesUtil.getValue(MERGE_SOURCE_KEY, false)
                    if (mergeSource) {
                        updateTaskStatus(
                            task = task,
                            progress = task.progress,
                            status = task.status
                        )
                        mergeFiles(task, videoFile, audioFile!!)
                    } else {
                        renameFiles(task, videoFile, audioFile!!)
                    }
                }

                videoFile != null -> {

                }

                else -> {
                    val currentTask =
                        mutex.withLock { _downloadQueue.value.find { task.id == it.id } }
                    if (currentTask == null || currentTask.status == DownloadStatus.PAUSE) {
                        return
                    }
                    markTaskAsFailed(task)
                }
            }

        }
    }

    private suspend fun renameFiles(
        task: DownloadTask,
        videoFile: File,
        audioFile: File,
        parentDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    ) {
        updateTaskStatus(task, progress = 100, status = DownloadStatus.PROCESSING)
        val targetVideoName = String("v_${task.name}.m4s".toByteArray(), Charset.forName("UTF-8"))
        val targetAudioName = String("a_${task.name}.m4s".toByteArray(), Charset.forName("UTF-8"))
        val downloadDir = File(parentDir, "BiliTube").apply {
            if (exists().not()) {
                mkdirs()
            }
        }
        val targetVideo = File(downloadDir, targetVideoName).run {
            val success = videoFile.renameTo(this)
            if (success) {
                videoFile.delete()
                this
            } else {
                videoFile
            }
        }
        val targetAudio = File(downloadDir, targetAudioName).run {
            val success = audioFile.renameTo(this)
            if (success) {
                audioFile.delete()
                this
            } else {
                audioFile
            }
        }
        markTaskAsSuccess(
            task = task,
            videoFile = targetVideo.absolutePath,
            audioFile = targetAudio.absolutePath
        )
    }

    private suspend fun mergeFiles(
        task: DownloadTask,
        videoFile: File,
        audioFile: File,
        parentDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    ) {
        updateTaskStatus(task, progress = 100, status = DownloadStatus.PROCESSING)
        val outputName = String("${task.id}.mp4".toByteArray(), Charset.forName("UTF-8"))
        val targetName = String("${task.name}.mp4".toByteArray(), Charset.forName("UTF-8"))
        val downloadDir = File(parentDir, "BiliTube").apply {
            if (exists().not()) {
                mkdirs()
            }
        }
        val outputFile = File(downloadDir, outputName)
        if (DBG) {
            Log.d(TAG, "mergeFiles: output file $outputFile")
        }
        mergeVideoAudio(
            videoFile.absolutePath,
            audioFile.absolutePath,
            outputFile.absolutePath
        ) { success ->
            mCoroutineScope.launch {
                if (success) {
                    videoFile.delete()
                    audioFile.delete()
                }
                if (success) {
                    val targetFile = task.archive?.run {
                        val archiveFile = File(downloadDir, this).apply {
                            if (exists().not()) {
                                mkdirs()
                            }
                        }
                        File(archiveFile, targetName)
                    } ?: run {
                        File(downloadDir, targetName)
                    }
                    outputFile.renameTo(targetFile)
                    task.archive?.run { outputFile.delete() }
                    markTaskAsSuccess(task = task, mergeFile = targetFile.absolutePath)
                } else {
                    markTaskAsFailed(task = task)
                }
            }
        }
    }

    private suspend fun markTaskAsSuccess(
        task: DownloadTask,
        mergeFile: String? = null,
        videoFile: String? = null,
        audioFile: String? = null,
    ) {
        updateTaskStatus(
            task = task,
            progress = 100,
            mergeFile = mergeFile,
            videoFile = videoFile,
            audioFile = audioFile,
            status = DownloadStatus.COMPLETED,
        )
        notificationHelper.showCompletedNotification(
            task.id.hashCode(),
            "下载完成: ${task.name}"
        )
        startNextDownload()
    }

    private suspend fun markTaskAsFailed(task: DownloadTask) {
        updateTaskStatus(
            task = task,
            progress = -1,
            status = DownloadStatus.FAILED
        )
        notificationHelper.showCompletedNotification(
            task.id.hashCode(),
            "下载失败: ${task.name}"
        )
        startNextDownload()
    }

    private suspend fun updateTaskStatus(
        task: DownloadTask,
        progress: Int,
        mergeFile: String? = null,
        videoFile: String? = null,
        audioFile: String? = null,
        status: DownloadStatus
    ) {
        mutex.withLock {
            _downloadQueue.update { queue ->
                queue.map {
                    if (it.id == task.id) {
                        it.copy(
                            mergedFile = mergeFile,
                            status = status,
                            progress = progress,
                            videoFile = videoFile,
                            audioFile = audioFile
                        )
                    } else {
                        it
                    }
                }
            }
        }
        updateTask(task)
    }

    private suspend fun updateTask(task: DownloadTask) {
        mutex.withLock { _downloadQueue.value.find { it.id == task.id } }?.let {
            biliTubeDB.downloadTaskDao().updateTask(it)
        }
    }


    private fun mergeVideoAudio(
        videoPath: String,
        audioPath: String,
        outputPath: String,
        onComplete: (Boolean) -> Unit
    ) {
        val cmd =
            "-y -i \"$videoPath\" -i \"$audioPath\" -c:v copy -c:a aac -strict experimental \"$outputPath\""
        FFmpegKit.executeAsync(cmd) { session ->
            when {
                ReturnCode.isSuccess(session.returnCode) -> {
                    onComplete.invoke(true)
                }

                ReturnCode.isCancel(session.returnCode) -> {
                }

                else -> {
                    if (DBG) {
                        Log.d(TAG, "mergeVideoAudio: ${session.logsAsString}")
                    }
                    onComplete.invoke(false)
                }
            }
        }
    }
}


class VideoAudioDownloader(
    private val client: HttpClient,
) {

    companion object {
        private val TAG = VideoAudioDownloader::class.simpleName
        private const val DBG = true
    }

    suspend fun download(
        url: String,
        fileName: String,
        parentDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        onProgressChanged: (suspend (Int) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        val remoteSize = client.head(url).headers[HttpHeaders.ContentLength]?.toLongOrNull() ?: -1
        if (DBG) {
            Log.d(TAG, "download: remoteSize $remoteSize")
        }
        val cacheDir = File(parentDir, "BiliTube").apply {
            if (exists().not()) {
                mkdirs()
            }
        }
        val file = File(
            cacheDir,
            fileName
        )
        var downloadedSize = if (file.exists()) file.length() else 0L
        if (DBG) {
            Log.d(TAG, "download: has size $downloadedSize")
        }
        // TODO 后续增加文件校验
        if (remoteSize == downloadedSize) {
            return@withContext file
        }
        var retry = 0
        while (retry < 3) {
            val newFile = client.prepareGet(url) {
                if (downloadedSize > 0) {
                    header(HttpHeaders.Range, "bytes=$downloadedSize-")
                }
            }.execute { response ->
                val contentLength = response.contentLength() ?: -1L
                if (response.status.value !in 200..299) {
                    retry++
                    downloadedSize = 0
                    file.delete()
                    return@execute null
                }
                val channel = response.bodyAsChannel()
                var bytesRead = downloadedSize
                RandomAccessFile(file, "rw").use { raf ->
                    raf.seek(downloadedSize)
                    val packet = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        ensureActive()
                        val byteArray = channel.readAvailable(packet)
                        if (byteArray == -1) break
                        raf.write(packet, 0, byteArray)
                        bytesRead += byteArray
                        if (contentLength > 0) {
                            val progress = ((bytesRead * 100) / contentLength).toInt()
                            onProgressChanged?.invoke(progress.coerceIn(0, 100))
                        }
                    }
                }
                return@execute file
            }
            if (newFile != null) {
                return@withContext newFile
            }
        }
        null // 下载失败
    }
}

