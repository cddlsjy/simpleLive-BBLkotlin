package com.xycz.bilibili_live.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xycz.bilibili_live.data.local.entity.FollowUser
import com.xycz.bilibili_live.data.local.entity.History
import com.xycz.bilibili_live.ui.screens.MainScreen
import com.xycz.bilibili_live.ui.screens.live.LiveRoomScreen
import com.xycz.bilibili_live.ui.screens.login.LoginScreen
import com.xycz.bilibili_live.ui.theme.BiliBiliLiveTheme
import com.xycz.bilibili_live.ui.viewmodel.CategoryUiState
import com.xycz.bilibili_live.ui.viewmodel.FollowUiState
import com.xycz.bilibili_live.ui.viewmodel.HistoryUiState
import com.xycz.bilibili_live.ui.viewmodel.HomeViewModel
import com.xycz.bilibili_live.ui.viewmodel.LiveRoomViewModel
import com.xycz.bilibili_live.ui.viewmodel.LoginViewModel
import com.xycz.bilibili_live.ui.viewmodel.SettingsUiState
import com.xycz.bilibili_live.ui.viewmodel.SubCategoryItem
import com.xycz.bilibili_live.util.NetworkModule

/**
 * 主Activity
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BiliBiliLiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BiliBiliLiveApp()
                }
            }
        }
    }
}

/**
 * 应用主界面
 */
@Composable
fun BiliBiliLiveApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var currentRoomId by remember { mutableStateOf<String?>(null) }

    // 自动播放上次观看
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        val settings = com.xycz.bilibili_live.util.SettingsManager(context)
        if (settings.autoResumeLast) {
            try {
                val historyDao = com.xycz.bilibili_live.data.local.AppDatabase.getInstance(context).historyDao()
                // 由于getAll()返回Flow，我们需要收集它
                historyDao.getAll().collect {
                    val lastHistory = it.firstOrNull()
                    when (lastHistory?.type) {
                        "live" -> {
                            lastHistory.roomId?.let {
                                currentRoomId = it
                                currentScreen = Screen.LiveRoom(it)
                            }
                        }
                        "vod" -> {
                            lastHistory.bvid?.let {
                                currentScreen = Screen.VodPlayer(it)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // 忽略异常，保持默认行为
            }
        }
    }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            val viewModel: HomeViewModel = viewModel {
                HomeViewModel(NetworkModule.bilibiliApi)
            }
            val uiState by viewModel.uiState.collectAsState()

            MainScreen(
                homeUiState = uiState,
                categoryUiState = CategoryUiState(),
                followUiState = FollowUiState(),
                followedList = emptyList<FollowUser>(),
                historyUiState = HistoryUiState(),
                settingsUiState = SettingsUiState(),
                onSearch = { viewModel.search(it) },
                onRoomClick = { roomId ->
                    currentRoomId = roomId
                    currentScreen = Screen.LiveRoom(roomId)
                },
                onLoadMore = { viewModel.loadMore() },
                onSubCategoryClick = { _, _ -> },
                onBackToCategories = { },
                onCategoryLoadMore = { },
                onCategoryRefresh = { },
                onFollowRoomClick = { roomId ->
                    currentRoomId = roomId
                    currentScreen = Screen.LiveRoom(roomId)
                },
                onUnfollow = { },
                onHistoryClick = { history ->
                    history.roomId?.let {
                        currentRoomId = it
                        currentScreen = Screen.LiveRoom(it)
                    }
                },
                onClearHistory = { },
                onDeleteHistory = { },
                onLoginClick = { currentScreen = Screen.Login },
                onLogout = { },
                onSetAutoResumeLast = { }
            )
        }

        is Screen.LiveRoom -> {
            val roomId = screen.roomId
            val context = androidx.compose.ui.platform.LocalContext.current
            val viewModel: LiveRoomViewModel = viewModel {
                LiveRoomViewModel(
                    application = context.applicationContext as android.app.Application,
                    roomId = roomId
                )
            }
            val uiState by viewModel.uiState.collectAsState()

            LiveRoomScreen(
                uiState = uiState,
                followList = emptyList(), // 这里应该从followViewModel获取
                recommendList = emptyList(), // 这里应该从homeViewModel获取
                onBack = { currentScreen = Screen.Home },
                onToggleDanmaku = { viewModel.toggleDanmaku() },
                onToggleFollow = { viewModel.toggleFollow() },
                onShowQuality = { viewModel.showQualitySheet() },
                onShare = { viewModel.shareRoom() },
                onSendDanmaku = { viewModel.sendDanmaku(it) },
                onQualitySelect = { },
                onSwitchRoom = { roomId ->
                    currentRoomId = roomId
                    currentScreen = Screen.LiveRoom(roomId)
                }
            )
        }

        is Screen.Login -> {
            val viewModel: LoginViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            LoginScreen(
                qrStatus = uiState.qrStatus,
                qrCodeUrl = uiState.qrCodeUrl,
                error = uiState.error,
                loginSuccess = uiState.loginSuccess,
                onBack = { currentScreen = Screen.Home },
                onRefresh = { viewModel.refreshQRCode() },
                onStartPolling = { viewModel.startPolling() }
            )
        }

        is Screen.VodPlayer -> {
            val bvid = screen.bvid
            val context = androidx.compose.ui.platform.LocalContext.current
            val viewModel: com.xycz.bilibili_live.ui.viewmodel.VodViewModel = viewModel(
                factory = com.xycz.bilibili_live.ui.viewmodel.VodViewModelFactory(
                    application = context.applicationContext as android.app.Application,
                    bvid = bvid
                )
            )
            val uiState by viewModel.uiState.collectAsState()

            com.xycz.bilibili_live.ui.screens.vod.VodPlayerScreen(
                uiState = uiState,
                onBack = { currentScreen = Screen.Home },
                onSwitchEpisode = { delta -> viewModel.switchEpisode(delta) },
                onSwitchRecommend = { newBvid -> currentScreen = Screen.VodPlayer(newBvid) }
            )
        }
    }
}

/**
 * 屏幕状态
 */
sealed class Screen {
    object Home : Screen()
    data class LiveRoom(val roomId: String) : Screen()
    data class VodPlayer(val bvid: String) : Screen()
    object Login : Screen()
}
