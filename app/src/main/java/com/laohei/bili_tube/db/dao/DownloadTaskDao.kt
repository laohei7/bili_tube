package com.laohei.bili_tube.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.laohei.bili_tube.model.DownloadTask

@Dao
interface DownloadTaskDao{

    @Query("""
        SELECT * FROM tb_downloads
    """)
    fun getAllTask():List<DownloadTask>

    @Query("""
        SELECT * FROM tb_downloads WHERE id = :id
    """)
    suspend fun getTaskById(id:String): DownloadTask

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTask(task: DownloadTask)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTask(task: DownloadTask)

    @Delete
    fun deleteTasks(tasks:List<DownloadTask>)
}