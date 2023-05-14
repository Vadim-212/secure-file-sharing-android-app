package com.vadim212.securefilesharingapp.presentation.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vadim212.securefilesharingapp.AndroidApplication
import com.vadim212.securefilesharingapp.presentation.di.components.ApplicationComponent
import com.vadim212.securefilesharingapp.presentation.di.modules.ActivityModule

abstract class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getApplicationComponent().inject(this)
    }

    /*protected*/ fun getApplicationComponent(): ApplicationComponent {
        return (application as AndroidApplication).getApplicationComponent()
    }

    /*protected*/ fun getActivityModule(): ActivityModule = ActivityModule(this)
}