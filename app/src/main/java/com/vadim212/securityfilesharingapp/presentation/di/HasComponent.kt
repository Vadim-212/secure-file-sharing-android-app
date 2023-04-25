package com.vadim212.securityfilesharingapp.presentation.di

interface HasComponent<C> {
    fun getComponent(): C
}