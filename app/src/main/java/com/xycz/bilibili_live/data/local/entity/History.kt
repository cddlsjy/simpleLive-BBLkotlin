package com.xycz.bilibili_live.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 历史记录实体
 * 支持直播和点播历史记录
 */
@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomId: String? = null, // 直播房间ID
    val bvid: String? = null, // 点播视频ID
    val cid: Long? = null, // 点播视频分P ID
    val type: String, // 类型：live 或 vod
    val siteId: String = "bilibili",
    val userName: String,
    val face: String,
    val progress: Long = 0, // 播放进度（毫秒）
    val updateTime: Long
)
