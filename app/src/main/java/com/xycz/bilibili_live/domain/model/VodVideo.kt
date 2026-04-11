package com.xycz.bilibili_live.domain.model

/**
 * 视频信息模型
 */
data class VodVideo(
    val bvid: String,
    val aid: Long,
    val title: String,
    val cover: String,
    val ownerName: String,
    val ownerFace: String,
    val viewCount: Int,
    val danmakuCount: Int,
    val duration: Int,
    val desc: String,
    val episodes: List<VodEpisode>
)
