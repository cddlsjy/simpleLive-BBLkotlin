package com.xycz.bilibili_live.domain.model

/**
 * 推荐视频模型
 */
data class VodRecommend(
    val bvid: String,
    val title: String,
    val cover: String,
    val ownerName: String,
    val viewCount: Int,
    val duration: Int
)
