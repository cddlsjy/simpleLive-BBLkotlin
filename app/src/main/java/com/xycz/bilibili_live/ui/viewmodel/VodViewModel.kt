package com.xycz.bilibili_live.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xycz.bilibili_live.data.remote.api.BilibiliApi
import com.xycz.bilibili_live.data.remote.dto.VideoDetailResponse
import com.xycz.bilibili_live.data.remote.dto.VideoPlayUrlResponse
import com.xycz.bilibili_live.data.remote.dto.RelatedVideoResponse
import com.xycz.bilibili_live.domain.model.VodVideo
import com.xycz.bilibili_live.domain.model.VodEpisode
import com.xycz.bilibili_live.domain.model.VodRecommend
import com.xycz.bilibili_live.util.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 点播ViewModel
 */
class VodViewModel(application: Application, private val bvid: String) : AndroidViewModel(application) {

    private val api: BilibiliApi = NetworkModule.bilibiliApi

    private val _uiState = MutableStateFlow(VodUiState())
    val uiState: StateFlow<VodUiState> = _uiState.asStateFlow()

    private var currentPageIndex = 0
    private var currentRecommendIndex = 0
    private var pagesList = listOf<VodEpisode>()
    private var recommendList = listOf<VodRecommend>()

    init {
        loadVideoDetail()
    }

    /**
     * 加载视频详情
     */
    fun loadVideoDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                // 获取视频详情
                val detailResponse = api.getVideoDetail(bvid)
                if (detailResponse.isSuccessful) {
                    val detailData = detailResponse.body()?.data
                    if (detailData != null) {
                        // 转换为领域模型
                        pagesList = detailData.pages.map {
                            VodEpisode(
                                cid = it.cid,
                                page = it.page,
                                part = it.part,
                                duration = it.duration,
                                firstFrame = it.firstFrame
                            )
                        }

                        val video = VodVideo(
                            bvid = detailData.bvid,
                            aid = detailData.aid,
                            title = detailData.title,
                            cover = detailData.pic,
                            ownerName = detailData.owner.name,
                            ownerFace = detailData.owner.face,
                            viewCount = detailData.stat.view,
                            danmakuCount = detailData.stat.danmaku,
                            duration = detailData.pages.sumOf { it.duration },
                            desc = detailData.desc,
                            episodes = pagesList
                        )

                        _uiState.value = _uiState.value.copy(
                            video = video,
                            currentEpisode = pagesList.getOrElse(0) { pagesList.first() }
                        )

                        // 加载播放地址
                        loadPlayUrl(pagesList[0].cid)

                        // 加载推荐视频
                        loadRecommendVideos()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "获取视频详情失败",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "网络错误: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    /**
     * 加载播放地址
     */
    private fun loadPlayUrl(cid: Long) {
        viewModelScope.launch {
            try {
                val response = api.getVideoPlayUrl(bvid, cid)
                if (response.isSuccessful) {
                    val playUrl = response.body()?.data?.durl?.firstOrNull()?.url
                    if (playUrl != null) {
                        _uiState.value = _uiState.value.copy(
                            playUrl = playUrl,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "获取播放地址失败",
                    isLoading = false
                )
            }
        }
    }

    /**
     * 加载推荐视频
     */
    private fun loadRecommendVideos() {
        viewModelScope.launch {
            try {
                val response = api.getRelatedVideos(bvid)
                if (response.isSuccessful) {
                    val relatedVideos = response.body()?.data
                    if (relatedVideos != null) {
                        recommendList = relatedVideos.map {
                            VodRecommend(
                                bvid = it.bvid,
                                title = it.title,
                                cover = it.pic,
                                ownerName = it.owner.name,
                                viewCount = it.stat.view,
                                duration = it.duration
                            )
                        }
                        _uiState.value = _uiState.value.copy(
                            recommendList = recommendList
                        )
                    }
                }
            } catch (e: Exception) {
                // 推荐视频加载失败不影响主播放
            }
        }
    }

    /**
     * 切换分P
     */
    fun switchEpisode(delta: Int) {
        val newIndex = currentPageIndex + delta
        if (newIndex in pagesList.indices) {
            currentPageIndex = newIndex
            val newEpisode = pagesList[newIndex]
            _uiState.value = _uiState.value.copy(
                currentEpisode = newEpisode
            )
            // 加载新分P的播放地址
            loadPlayUrl(newEpisode.cid)
        }
    }

    /**
     * 切换推荐视频
     */
    fun switchRecommend(delta: Int) {
        if (recommendList.isNotEmpty()) {
            val newIndex = (currentRecommendIndex + delta + recommendList.size) % recommendList.size
            currentRecommendIndex = newIndex
            val newRecommend = recommendList[newIndex]
            // 重新加载新视频的详情
            // 这里需要更新bvid并重新调用loadVideoDetail
            // 但由于ViewModel是按bvid创建的，实际应用中可能需要通过回调通知上层切换页面
        }
    }

    /**
     * 保存播放进度
     */
    fun savePlayProgress(progress: Long) {
        // 这里可以实现保存播放进度到数据库
    }
}

/**
 * 点播UI状态
 */
data class VodUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val video: VodVideo? = null,
    val currentEpisode: VodEpisode? = null,
    val playUrl: String? = null,
    val recommendList: List<VodRecommend> = emptyList()
)
