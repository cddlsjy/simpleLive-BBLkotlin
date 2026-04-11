package com.xycz.bilibili_live.util

import com.xycz.bilibili_live.domain.model.VodEpisode
import com.xycz.bilibili_live.domain.model.VodRecommend

/**
 * 播放列表管理器
 * 管理直播和点播的播放逻辑
 */
class PlaylistManager {

    enum class PlayMode {
        LIVE,
        VOD
    }

    private var currentMode: PlayMode = PlayMode.LIVE
    private var currentEpisodeIndex = 0
    private var currentRecommendIndex = 0
    private var episodes = listOf<VodEpisode>()
    private var recommends = listOf<VodRecommend>()

    /**
     * 设置为直播模式
     */
    fun setLiveMode() {
        currentMode = PlayMode.LIVE
    }

    /**
     * 设置为点播模式
     */
    fun setVodMode(episodes: List<VodEpisode>, recommends: List<VodRecommend>) {
        currentMode = PlayMode.VOD
        this.episodes = episodes
        this.recommends = recommends
        currentEpisodeIndex = 0
        currentRecommendIndex = 0
    }

    /**
     * 获取当前播放模式
     */
    fun getCurrentMode(): PlayMode = currentMode

    /**
     * 切换到上一个分P
     */
    fun previousEpisode(): VodEpisode? {
        if (currentMode != PlayMode.VOD || episodes.isEmpty()) return null
        currentEpisodeIndex = (currentEpisodeIndex - 1 + episodes.size) % episodes.size
        return episodes[currentEpisodeIndex]
    }

    /**
     * 切换到下一个分P
     */
    fun nextEpisode(): VodEpisode? {
        if (currentMode != PlayMode.VOD || episodes.isEmpty()) return null
        currentEpisodeIndex = (currentEpisodeIndex + 1) % episodes.size
        return episodes[currentEpisodeIndex]
    }

    /**
     * 切换到上一个推荐视频
     */
    fun previousRecommend(): VodRecommend? {
        if (currentMode != PlayMode.VOD || recommends.isEmpty()) return null
        currentRecommendIndex = (currentRecommendIndex - 1 + recommends.size) % recommends.size
        return recommends[currentRecommendIndex]
    }

    /**
     * 切换到下一个推荐视频
     */
    fun nextRecommend(): VodRecommend? {
        if (currentMode != PlayMode.VOD || recommends.isEmpty()) return null
        currentRecommendIndex = (currentRecommendIndex + 1) % recommends.size
        return recommends[currentRecommendIndex]
    }

    /**
     * 获取当前分P
     */
    fun getCurrentEpisode(): VodEpisode? {
        if (currentMode != PlayMode.VOD || episodes.isEmpty()) return null
        return episodes.getOrNull(currentEpisodeIndex)
    }

    /**
     * 获取当前推荐视频
     */
    fun getCurrentRecommend(): VodRecommend? {
        if (currentMode != PlayMode.VOD || recommends.isEmpty()) return null
        return recommends.getOrNull(currentRecommendIndex)
    }

    /**
     * 获取分P列表
     */
    fun getEpisodes(): List<VodEpisode> = episodes

    /**
     * 获取推荐视频列表
     */
    fun getRecommends(): List<VodRecommend> = recommends
}
