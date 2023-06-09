package com.vadim212.securefilesharingapp.presentation.di.modules

import android.content.Context
import com.vadim212.securefilesharingapp.AndroidApplication
import com.vadim212.securefilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securefilesharingapp.data.repository.UserPublicKeyRepository
import com.vadim212.securefilesharingapp.domain.repository.FileSharingDomainRepository
import com.vadim212.securefilesharingapp.domain.repository.UserPublicKeyDomainRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: AndroidApplication) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = this.application

//    @Provides
//    @Singleton
//    fun provideThreadExecutor(jobExecutor: JobExecutor) = jobExecutor
//
//    @Provides
//    @Singleton
//    fun providePostExecutionThread(uiThread: UIThread) = uiThread

    @Provides
    @Singleton
    fun provideFileSharingRepository(fileSharingRepository: FileSharingRepository): FileSharingDomainRepository = fileSharingRepository

    @Provides
    @Singleton
    fun provideUserPublicKeyRepository(userPublicKeyRepository: UserPublicKeyRepository): UserPublicKeyDomainRepository = userPublicKeyRepository
}