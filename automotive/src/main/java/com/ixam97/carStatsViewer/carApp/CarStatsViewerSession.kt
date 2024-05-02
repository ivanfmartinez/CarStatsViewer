package com.ixam97.carStatsViewer.carApp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.lifecycleScope
import com.ixam97.carStatsViewer.BuildConfig
import com.ixam97.carStatsViewer.CarStatsViewer
import com.ixam97.carStatsViewer.carApp.renderer.CarDataSurfaceCallback
import com.ixam97.carStatsViewer.dataCollector.DataCollector
import com.ixam97.carStatsViewer.ui.activities.PermissionsActivity
import com.ixam97.carStatsViewer.utils.throttle
import kotlinx.coroutines.launch

@OptIn(ExperimentalCarApi::class)
class CarStatsViewerSession : Session(), DefaultLifecycleObserver {

    val permissions = PermissionsActivity.PERMISSIONS.toList()

    lateinit var carDataSurfaceCallback: CarDataSurfaceCallback

    override fun onCreateScreen(intent: Intent): Screen {
        carDataSurfaceCallback = CarDataSurfaceCallback(carContext)
        lifecycleScope.launch {
            CarStatsViewer.dataProcessor.realTimeDataFlow.throttle(1000).collect {
                if (carContext.carAppApiLevel >= 7 && carDataSurfaceCallback.isEnabled()) {
                    carDataSurfaceCallback.requestRenderFrame()
                }
            }
        }

        val screens = mutableListOf<Screen>(CarStatsViewerScreen(carContext, this))

        lifecycle.addObserver(this)

        if (CarStatsViewer.appPreferences.versionString != BuildConfig.VERSION_NAME) {
            screens.add(ChangesScreen(carContext))
        }

        var neededPermissions = permissions.filter { carContext.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (neededPermissions.isNotEmpty()) {
            screens.add(PermissionScreen(carContext, this))
            // carContext.requestPermissions(permissions) {granted,_ ->
            //     if (granted.containsAll(permissions)) {
            //         startService()
            //     }
            // }
        } else {
            startService()
        }

        if (screens.size > 1) {
            val screenManager = carContext.getCarService(ScreenManager::class.java)
            for (i in 0 until screens.size - 1) {
                screenManager.push(screens[i])
            }
        }

        return screens.last()// CarStatsViewerScreen(carContext, this)
    }

    fun startService() {
        carContext.startForegroundService(Intent(carContext, DataCollector::class.java))
    }
}