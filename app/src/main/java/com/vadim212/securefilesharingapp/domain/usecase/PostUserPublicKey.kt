package com.vadim212.securefilesharingapp.domain.usecase

import com.vadim212.securefilesharingapp.domain.UserPublicKey
import com.vadim212.securefilesharingapp.domain.base.BaseNetworkUseCase
import com.vadim212.securefilesharingapp.domain.repository.UserPublicKeyDomainRepository
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import javax.inject.Inject

class PostUserPublicKey @Inject constructor(private var userPublicKeyRepository: UserPublicKeyDomainRepository):
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