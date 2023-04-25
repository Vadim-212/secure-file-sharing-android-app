package com.vadim212.securityfilesharingapp.presentation.di.components

import android.content.Context
import com.vadim212.securityfilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securityfilesharingapp.data.repository.UserPublicKeyRepository
import com.vadim212.securityfilesharingapp.presentation.di.modules.ApplicationModule
import com.vadim212.securityfilesharingapp.presentation.view.activity.BaseActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(baseActivity: BaseActivity)

    fun context(): Context
    //fun threadExecutor(): ThreadExecutor
    //fun postExecutionThread(): PostExecutionThread
    fun fileSharingRepository(): FileSharingRepository
    fun userPublicKeyRepository(): UserPublicKeyRepository
}