package com.vadim212.securityfilesharingapp.domain.usecase

import com.vadim212.securityfilesharingapp.data.UserPublicKey
import com.vadim212.securityfilesharingapp.data.repository.UserPublicKeyRepository
import com.vadim212.securityfilesharingapp.domain.base.BaseNetworkUseCase
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

class PostUserPublicKey(var userPublicKeyRepository: UserPublicKeyRepository):
    BaseNetworkUseCase<ResponseBody, PostUserPublicKey.Companion.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<ResponseBody> {
        return this.userPublicKeyRepository.initiatePostUserPbKey(params.userPbKey)
    }

    companion object {
        class Params(val userPbKey: UserPublicKey) {
            companion object {
                fun forUserPublicKey(userPbKey: UserPublicKey) = Params(userPbKey)
            }
        }
    }
}