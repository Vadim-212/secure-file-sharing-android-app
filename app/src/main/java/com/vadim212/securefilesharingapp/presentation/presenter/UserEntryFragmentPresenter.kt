package com.vadim212.securefilesharingapp.presentation.presenter

import com.vadim212.securefilesharingapp.domain.UserPublicKey
import com.vadim212.securefilesharingapp.domain.usecase.GetUserPublicKey
import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.view.UserEntryView
import com.vadim212.securefilesharingapp.domain.base.DefaultObserver
import com.vadim212.securefilesharingapp.domain.utils.RecipientUserPublicKeyHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@PerActivity
class UserEntryFragmentPresenter @Inject constructor(private val getUserPublicKeyUseCase: GetUserPublicKey) : Presenter {
    private var userEntryView: UserEntryView? = null

    fun setView(view: UserEntryView) {
        this.userEntryView = view
    }

    override fun resume() { }

    override fun pause() { }

    override fun destroy() {
        this.getUserPublicKeyUseCase.dispose()
        this.userEntryView = null
    }

    fun initialize(userId: String) {
        this.hideViewRetry()
        this.showViewLoading()
        this.getUserPublicKey(userId)
    }

    private fun getUserPublicKey(userId: String) {
        this.getUserPublicKeyUseCase.execute(GetUserPublicKeyObserver(), GetUserPublicKey.Companion.Params.forUserPublicKey(userId))
    }

    private fun showViewLoading() {
        this.userEntryView?.showLoading() // TODO: add null check - ?: throw Exception("")
    }

    private fun hideViewLoading() {
        this.userEntryView?.hideLoading()
    }

    private fun showViewRetry() {
        this.userEntryView?.showRetry()
    }

    private fun hideViewRetry() {
        this.userEntryView?.hideRetry()
    }

    private fun showErrorMessage(e: Exception) {
        this.userEntryView?.showError(e.message ?: e.javaClass.simpleName)
    }

    private fun savePublicKey(userPublicKey: UserPublicKey) {
        CoroutineScope(Dispatchers.IO).launch {
            RecipientUserPublicKeyHelper.saveRecipientPublicKeyToDataStore(userPublicKey, this@UserEntryFragmentPresenter.userEntryView?.context()!!)
        }
    }

    private fun onPublicKeySaved() {
        this.userEntryView?.onRecipientPublicKeySaved()
    }

    private inner class GetUserPublicKeyObserver: DefaultObserver<UserPublicKey>() {
        override fun onNext(t: UserPublicKey) {
            this@UserEntryFragmentPresenter.savePublicKey(t)
        }

        override fun onError(e: Throwable) {
            this@UserEntryFragmentPresenter.hideViewLoading()
            this@UserEntryFragmentPresenter.showErrorMessage(e as Exception)
            this@UserEntryFragmentPresenter.showViewRetry()
        }

        override fun onComplete() {
            this@UserEntryFragmentPresenter.hideViewLoading()
            this@UserEntryFragmentPresenter.onPublicKeySaved()
        }

    }
}