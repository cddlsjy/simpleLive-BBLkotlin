package com.xycz.bilibili_live.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * VodViewModel工厂类
 */
class VodViewModelFactory(
    private val application: Application,
    private val bvid: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VodViewModel(application, bvid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
