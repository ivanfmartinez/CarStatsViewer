package com.ixam97.carStatsViewer.aaos

import android.content.pm.ApplicationInfo
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator


class CarStatsViewerService : CarAppService() {
    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return CarStatsViewerSession()
    }

    override fun createHostValidator(): HostValidator {
        return if (getApplicationInfo().flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator.Builder(getApplicationContext())
                .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                .build()
        }
    }
}
