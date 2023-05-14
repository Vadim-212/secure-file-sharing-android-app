package com.vadim212.securefilesharingapp

import android.app.Application
import com.vadim212.securefilesharingapp.presentation.di.components.ApplicationComponent
import com.vadim212.securefilesharingapp.presentation.di.components.DaggerApplicationComponent
import com.vadim212.securefilesharingapp.presentation.di.modules.ApplicationModule

class AndroidApplication: Application() {
    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        initializeInjector()
        //initializeLeakDetection()
    }

    private fun initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()

    }

    fun getApplicationComponent() = this.applicationComponent

//    private fun initializeLeakDetection() {
//        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this)
//        }
//    }
}