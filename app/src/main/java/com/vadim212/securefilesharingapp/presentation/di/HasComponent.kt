package com.vadim212.securefilesharingapp.presentation.di

interface HasComponent<C> {
    fun getComponent(): C
}