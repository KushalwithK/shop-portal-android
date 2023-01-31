package com.nexusinfinity.electronicsportal

import android.app.Application
import com.google.android.material.color.DynamicColors

class ElectronicsPortalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}