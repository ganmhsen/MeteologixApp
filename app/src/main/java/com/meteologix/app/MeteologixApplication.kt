package com.meteologix.app

import android.app.Application
import android.webkit.WebView

class MeteologixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}