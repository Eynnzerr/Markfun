package com.eynnzerr.memorymarkdown.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CPApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    //since this is an application context, which will always exist till app finishes, it won't cause memory leak literally.
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}