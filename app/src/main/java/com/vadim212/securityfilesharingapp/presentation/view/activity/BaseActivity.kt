package com.vadim212.securityfilesharingapp.presentation.view.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vadim212.securityfilesharingapp.AndroidApplication
import com.vadim212.securityfilesharingapp.presentation.di.components.ApplicationComponent
import com.vadim212.securityfilesharingapp.presentation.di.modules.ActivityModule

abstract class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getApplicationComponent().inject(this)
    }

    protected fun getApplicationComponent(): ApplicationComponent {
        return (application as AndroidApplication).getApplicationComponent()
    }

    protected fun getActivityModule(): ActivityModule = ActivityModule(this)
}