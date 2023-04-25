package com.vadim212.securityfilesharingapp.presentation.di.modules

import android.app.Activity
import com.vadim212.securityfilesharingapp.presentation.di.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    @PerActivity
    fun activity(): Activity = this.activity
}