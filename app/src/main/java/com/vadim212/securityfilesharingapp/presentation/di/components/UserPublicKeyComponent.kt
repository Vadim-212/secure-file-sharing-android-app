package com.vadim212.securityfilesharingapp.presentation.di.components

import com.vadim212.securityfilesharingapp.presentation.di.PerActivity
import com.vadim212.securityfilesharingapp.presentation.di.modules.ActivityModule
import com.vadim212.securityfilesharingapp.presentation.di.modules.UserPublicKeyModule
import com.vadim212.securityfilesharingapp.presentation.view.fragment.UserEntryFragment
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class, UserPublicKeyModule::class])
interface UserPublicKeyComponent: ActivityComponent {
    fun inject(userEntryFragment: UserEntryFragment)
}