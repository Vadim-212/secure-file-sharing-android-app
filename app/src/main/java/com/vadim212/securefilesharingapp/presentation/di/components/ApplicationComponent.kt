package com.vadim212.securefilesharingapp.presentation.di.components

import android.content.Context
import com.vadim212.securefilesharingapp.domain.repository.FileSharingDomainRepository
import com.vadim212.securefilesharingapp.domain.repository.UserPublicKeyDomainRepository
import com.vadim212.securefilesharingapp.presentation.di.modules.ApplicationModule
import com.vadim212.securefilesharingapp.presentation.view.activity.BaseActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(baseActivity: BaseActivity)

    fun context(): Context
    //fun threadExecutor(): ThreadExecutor
    //fun postExecutionThread(): PostExecutionThread
    fun fileSharingRepository(): FileSharingDomainRepository
    fun userPublicKeyRepository(): UserPublicKeyDomainRepository
}