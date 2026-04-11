package com.xycz.bilibili_live.domain.model

/**
 * 分P/剧集模型
 */
data class VodEpisode(
    val cid: Long,
    val page: Int,
    val part: String,
    val duration: Int,
    val firstFrame: String?
)
