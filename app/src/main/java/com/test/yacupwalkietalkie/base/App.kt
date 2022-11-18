package com.test.yacupwalkietalkie.base

import android.app.Application
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME

class App : Application() {
    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        System.setProperty(IO_PARALLELISM_PROPERTY_NAME, Int.MAX_VALUE.toString())
        app = this
    }
}
