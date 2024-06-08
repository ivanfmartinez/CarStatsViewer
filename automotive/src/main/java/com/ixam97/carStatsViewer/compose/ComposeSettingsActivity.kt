package com.ixam97.carStatsViewer.compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.SwitchDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixam97.carStatsViewer.R
import com.ixam97.carStatsViewer.compose.screens.SettingsScreen
import com.ixam97.carStatsViewer.compose.theme.CarTheme

class ComposeSettingsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()

            settingsViewModel.finishActivityLiveData.observe(this) {
                if (it.consume() == true) finish()
            }

            CarTheme(Build.BRAND) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}