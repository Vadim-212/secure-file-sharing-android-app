package com.vadim212.securefilesharingapp.presentation.di.components

import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.di.modules.ActivityModule
import com.vadim212.securefilesharingapp.presentation.di.modules.ShareFileModule
import com.vadim212.securefilesharingapp.presentation.view.fragment.SendingFilesFragment
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class, ShareFileModule::class])
interface ShareFileComponent: ActivityComponent {
    fun inject(sendingFilesFragment: SendingFilesFragment)
}