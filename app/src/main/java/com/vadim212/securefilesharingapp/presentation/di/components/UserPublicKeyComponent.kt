package com.vadim212.securefilesharingapp.presentation.di.components

import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.di.modules.ActivityModule
import com.vadim212.securefilesharingapp.presentation.di.modules.UserPublicKeyModule
import com.vadim212.securefilesharingapp.presentation.view.fragment.HomeFragment
import com.vadim212.securefilesharingapp.presentation.view.fragment.UserEntryFragment
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class, UserPublicKeyModule::class])
interface UserPublicKeyComponent: ActivityComponent {
    fun inject(homeFragment: HomeFragment)

    fun inject(userEntryFragment: UserEntryFragment)
}