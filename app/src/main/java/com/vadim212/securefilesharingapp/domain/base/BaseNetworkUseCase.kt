package com.vadim212.securefilesharingapp.domain.base

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class BaseNetworkUseCase<T : Any, Params> { //params
    private val compositeDisposable = CompositeDisposable()

    fun execute(disposableObserver: DisposableObserver<T>, params: Params) { //params
        val observable = this.buildUseCaseObservable(params)//params
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        val observer = observable.subscribeWith(disposableObserver)
        compositeDisposable.add(observer)
    }

    abstract fun buildUseCaseObservable(params: Params): Observable<T> //params

    fun dispose() {
        if(!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    fun clear() {
        compositeDisposable.clear()
    }
}