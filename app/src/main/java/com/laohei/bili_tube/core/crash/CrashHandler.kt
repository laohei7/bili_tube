package com.laohei.bili_tube.core.crash

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.laohei.bili_tube.BuildConfig
import com.laohei.bili_tube.core.SAVE_CRASH_DOCS_KEY
import com.laohei.bili_tube.utill.PreferencesUtil
import java.io.File

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private lateinit var appContext: Context
    private lateinit var preferencesUtil: PreferencesUtil

    companion object {
        val instance: CrashHandler by lazy { CrashHandler() }
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        preferencesUtil = PreferencesUtil(context)
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (preferencesUtil.getValue(SAVE_CRASH_DOCS_KEY, false)) {
            saveCrashInfoToDocuments(e)
        }
        defaultHandler?.uncaughtException(t, e)
    }

    private fun saveCrashInfoToDocuments(e: Throwable) {
        val docDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (docDir?.exists() == false) docDir.mkdirs()
        val crashDir = File(docDir, "BiliTube/crash").apply {
            if (!exists()) mkdirs()
        }


        val crashFile = File(crashDir, "crash_${System.currentTimeMillis()}.log")
        val crashContent = buildString {
            append(getDeviceInfo())
            append("\n\n")
            append(Log.getStackTraceString(e))
        }

        crashFile.writeText(crashContent)
    }

    private fun uploadCrash(e: Throwable) {
        // TODO: upload crash doc
    }

    private fun showToast(message: String) {
        // TODO: show toast
    }

    private fun getDeviceInfo(): String {
        return """
            Brand: ${Build.BRAND}
            Model: ${Build.MODEL}
            SDK: ${Build.VERSION.SDK_INT}
            Version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})
        """.trimIndent()
    }
}
