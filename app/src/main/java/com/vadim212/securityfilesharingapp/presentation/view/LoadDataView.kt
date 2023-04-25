package com.vadim212.securityfilesharingapp.presentation.view

import android.content.Context

interface LoadDataView: BaseView {
    fun showLoading()

    fun hideLoading()

    fun showRetry()

    fun hideRetry()

    fun showError(message: String)

    fun context(): Context
}