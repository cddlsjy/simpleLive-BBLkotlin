package com.xycz.bilibili_live.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 视频详情响应
 */
data class VideoDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: VideoDetailData?
)

/**
 * 视频详情数据
 */
data class VideoDetailData(
    @SerializedName("bvid") val bvid: String,
    @SerializedName("aid") val aid: Long,
    @SerializedName("title") val title: String,
    @SerializedName("pubdate") val pubdate: Long,
    @SerializedName("pic") val pic: String,
    @SerializedName("videos") val videos: Int,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("stat") val stat: VideoStat,
    @SerializedName("pages") val pages: List<VideoPage>,
    @SerializedName("desc") val desc: String
)

/**
 * 视频UP主信息
 */
data class Owner(
    @SerializedName("mid") val mid: Long,
    @SerializedName("name") val name: String,
    @SerializedName("face") val face: String
)

/**
 * 视频统计信息
 */
data class VideoStat(
    @SerializedName("view") val view: Int,
    @SerializedName("danmaku") val danmaku: Int,
    @SerializedName("reply") val reply: Int,
    @SerializedName("favorite") val favorite: Int,
    @SerializedName("coin") val coin: Int,
    @SerializedName("share") val share: Int
)

/**
 * 视频分P信息
 */
data class VideoPage(
    @SerializedName("cid") val cid: Long,
    @SerializedName("page") val page: Int,
    @SerializedName("part") val part: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("first_frame") val firstFrame: String?
)

/**
 * 视频播放地址响应
 */
data class VideoPlayUrlResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: VideoPlayUrlData?
)

/**
 * 视频播放地址数据
 */
data class VideoPlayUrlData(
    @SerializedName("durl") val durl: List<Durl>,
    @SerializedName("quality") val quality: Int,
    @SerializedName("format") val format: String
)

/**
 * 播放地址信息
 */
data class Durl(
    @SerializedName("url") val url: String,
    @SerializedName("length") val length: Int,
    @SerializedName("size") val size: Long,
    @SerializedName("aes_key") val aesKey: String?
)

/**
 * 推荐视频响应
 */
data class RelatedVideoResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<RelatedVideoItem>
)

/**
 * 推荐视频项
 */
data class RelatedVideoItem(
    @SerializedName("bvid") val bvid: String,
    @SerializedName("aid") val aid: Long,
    @SerializedName("title") val title: String,
    @SerializedName("pic") val pic: String,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("stat") val stat: VideoStat,
    @SerializedName("duration") val duration: Int
)
