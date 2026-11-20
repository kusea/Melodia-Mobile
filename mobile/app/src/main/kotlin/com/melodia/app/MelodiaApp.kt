package com.melodia.app

import android.app.Application
import com.melodia.app.di.AppContainer
import com.melodia.app.di.DefaultAppContainer

class MelodiaApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
