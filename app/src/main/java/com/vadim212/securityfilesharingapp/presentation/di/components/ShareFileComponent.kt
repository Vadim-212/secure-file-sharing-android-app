package com.vadim212.securityfilesharingapp.presentation.di.components

import android.app.Activity
import com.vadim212.securityfilesharingapp.presentation.di.PerActivity
import com.vadim212.securityfilesharingapp.presentation.di.modules.ActivityModule
import com.vadim212.securityfilesharingapp.presentation.di.modules.ShareFileModule
import com.vadim212.securityfilesharingapp.presentation.view.fragment.SendingFilesFragment
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class, ShareFileModule::class])
interface ShareFileComponent: ActivityComponent {
    fun inject(sendingFilesFragment: SendingFilesFragment)
}