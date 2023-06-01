package com.vadim212.securefilesharingapp.presentation.presenter

import com.vadim212.securefilesharingapp.domain.UserPublicKey
import com.vadim212.securefilesharingapp.domain.base.DefaultObserver
import com.vadim212.securefilesharingapp.domain.usecase.PostUserPublicKey
import com.vadim212.securefilesharingapp.presentation.di.PerActivity
import com.vadim212.securefilesharingapp.presentation.model.UserPublicKeyModel
import com.vadim212.securefilesharingapp.presentation.view.HomeView
import okhttp3.ResponseBody
import javax.inject.Inject

@PerActivity
class HomeFragmentPresenter @Inject constructor(private val postUserPublicKeyUseCase: PostUserPublicKey): Presenter {
    private var homeView: HomeView? = null

    fun setView(homeView: HomeView) {
        this.homeView = homeView
    }

    override fun resume() { }

    override fun pause() { }

    override fun destroy() {
        this.postUserPublicKeyUseCase.dispose()
        this.homeView = null
    }

    fun initialize(userPublicKeyModel: UserPublicKeyModel) {
        this.hideViewRetry()
        this.showViewLoading()
        this.postUserPublicKey(UserPublicKey(userPublicKeyModel.userId, userPublicKeyModel.pbKey))
    }

    private fun postUserPublicKey(userPublicKey: UserPublicKey) {
        this.postUserPublicKeyUseCase.execute(PostUserPublicKeyObserver(), PostUserPublicKey.Companion.Params.forUserPublicKey(userPublicKey))
    }

    private fun showViewLoading() {
        this.homeView?.showLoading() // TODO: add null check - ?: throw Exception("")
    }

    private fun hideViewLoading() {
        this.homeView?.hideLoading()
    }

    private fun showViewRetry() {
        this.homeView?.showRetry()
    }

    private fun hideViewRetry() {
        this.homeView?.hideRetry()
    }

    private fun showErrorMessage(e: Exception) {
        this.homeView?.showError(e.message ?: e.javaClass.simpleName)
    }

    private inner class PostUserPublicKeyObserver: DefaultObserver<ResponseBody>() {

        override fun onError(e: Throwable) {
            this@HomeFragmentPresenter.hideViewLoading()
            this@HomeFragmentPresenter.showErrorMessage(e as Exception)
            this@HomeFragmentPresenter.showViewRetry()
        }

        override fun onComplete() {
            this@HomeFragmentPresenter.hideViewLoading()
        }
    }
}