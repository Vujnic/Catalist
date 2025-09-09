package com.example.catalist

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CatalistApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        Log.d("Test", "App:onCreate()")
    }
}