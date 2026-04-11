package com.xycz.bilibili_live.data.local.dao

import androidx.room.*
import com.xycz.bilibili_live.data.local.entity.History
import kotlinx.coroutines.flow.Flow

/**
 * 历史记录DAO
 */
@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY updateTime DESC")
    fun getAll(): Flow<List<History>>

    @Query("SELECT * FROM history WHERE roomId = :roomId")
    suspend fun getByRoomId(roomId: String): History?

    @Query("SELECT * FROM history WHERE bvid = :bvid")
    suspend fun getByBvid(bvid: String): History?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Delete
    suspend fun delete(history: History)

    @Query("DELETE FROM history")
    suspend fun deleteAll()

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM history WHERE roomId = :roomId")
    suspend fun deleteByRoomId(roomId: String)

    @Query("DELETE FROM history WHERE bvid = :bvid")
    suspend fun deleteByBvid(bvid: String)
}
