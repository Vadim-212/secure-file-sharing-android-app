package com.vadim212.securefilesharingapp.domain.usecase

import com.vadim212.securefilesharingapp.domain.UserPublicKey
import com.vadim212.securefilesharingapp.domain.base.BaseNetworkUseCase
import com.vadim212.securefilesharingapp.domain.repository.UserPublicKeyDomainRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetUserPublicKey @Inject constructor(private var userPublicKeyRepository: UserPublicKeyDomainRepository) :
    BaseNetworkUseCase<UserPublicKey, GetUserPublicKey.Companion.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<UserPublicKey> {
        return this.userPublicKeyRepository.initiateGetUserPbKey(params.userId)
    }

    companion object {
        class Params(val userId: String) {
            companion object {
                fun forUserPublicKey(userId: String) = Params(userId)
            }
        }
    }
}