package com.xycz.bilibili_live.ui.screens.vod

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.xycz.bilibili_live.service.player.PlayerManager
import com.xycz.bilibili_live.ui.viewmodel.VodUiState
import com.xycz.bilibili_live.util.PlaylistManager

/**
 * 点播播放器页面
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun VodPlayerScreen(
    uiState: VodUiState,
    onBack: () -> Unit,
    onSwitchEpisode: (Int) -> Unit,
    onSwitchRecommend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerManager = remember { PlayerManager(context) }
    val playlistManager = remember { PlaylistManager() }
    val focusRequester = remember { FocusRequester() }
    var isPlaying by remember { mutableStateOf(false) }

    // 初始化播放列表管理器
    LaunchedEffect(uiState.video, uiState.recommendList) {
        uiState.video?.let {
            playlistManager.setVodMode(it.episodes, uiState.recommendList)
        }
    }

    LaunchedEffect(uiState.playUrl) {
        uiState.playUrl?.let {url ->
            playerManager.initialize()
            playerManager.playQuality(url)
            isPlaying = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            playerManager.stop()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.video?.title ?: "视频播放",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.nativeKeyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                    when (event.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onSwitchEpisode(-1)
                            true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onSwitchEpisode(1)
                            true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_UP -> {
                            val prevRecommend = playlistManager.previousRecommend()
                            prevRecommend?.let {
                                onSwitchRecommend(it.bvid)
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_DOWN -> {
                            val nextRecommend = playlistManager.nextRecommend()
                            nextRecommend?.let {
                                onSwitchRecommend(it.bvid)
                            }
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            // 视频播放区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                // 这里应该集成播放器视图
                // 暂时显示封面图
                uiState.video?.cover?.let {coverUrl ->
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = "视频封面",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // 播放/暂停按钮
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { 
                            if (isPlaying) {
                                playerManager.pause()
                            } else {
                                playerManager.resume()
                            }
                            isPlaying = !isPlaying
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                playerManager.pause()
                            } else {
                                playerManager.resume()
                            }
                            isPlaying = !isPlaying
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "暂停" else "播放",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            // 视频信息
            uiState.video?.let {video ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = video.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = video.ownerFace,
                                contentDescription = "UP主头像",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = video.ownerName)
                        }

                        Row {
                            Text(text = "${video.viewCount} 观看")
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "${video.danmakuCount} 弹幕")
                        }
                    }
                }
            }

            // 分P列表
            uiState.video?.episodes?.let {episodes ->
                if (episodes.size > 1) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "分P列表",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow {
                            items(episodes) {episode ->
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .padding(4.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(8.dp)
                                        .clickable { 
                                            // 切换到该分P
                                        }
                                ) {
                                    Text(
                                        text = episode.part,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 推荐视频列表
            if (uiState.recommendList.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "推荐视频",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow {
                        items(uiState.recommendList) {recommend ->
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .padding(4.dp)
                                    .clickable { 
                                        onSwitchRecommend(recommend.bvid)
                                    }
                            ) {
                                AsyncImage(
                                    model = recommend.cover,
                                    contentDescription = recommend.title,
                                    modifier = Modifier
                                        .width(150.dp)
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Text(
                                    text = recommend.title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = recommend.ownerName,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 自动获取焦点以支持遥控器操作
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
