package com.bikerental.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BikeApplication: Application() {
    companion object {
        const val TAG = "BikeApplication"
    }

    override fun onCreate() {
        super.onCreate()

    }
}