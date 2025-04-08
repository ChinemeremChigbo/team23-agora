package com.chinemerem.agora.screens.settings.appearance

import AppearanceViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppearanceViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppearanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppearanceViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
