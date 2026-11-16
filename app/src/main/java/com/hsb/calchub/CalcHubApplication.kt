package com.hsb.calchub

import android.app.Application
import com.hsb.calchub.di.AppContainer

class CalcHubApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
