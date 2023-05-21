package com.vadim212.securefilesharingapp.presentation.di.components

import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.di.modules.ActivityModule
import com.vadim212.securefilesharingapp.presentation.di.modules.DownloadFileModule
import com.vadim212.securefilesharingapp.presentation.view.fragment.ReceivingFilesFragment
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class, DownloadFileModule::class])
interface DownloadFileComponent {
    fun inject(receivingFilesFragment: ReceivingFilesFragment)
}