package com.vadim212.securefilesharingapp.presentation.di.components

import android.app.Activity
import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.di.modules.ActivityModule
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun activity(): Activity
}