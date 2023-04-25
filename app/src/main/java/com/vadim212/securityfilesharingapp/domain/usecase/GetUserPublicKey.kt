package com.vadim212.securityfilesharingapp.domain.usecase

import com.vadim212.securityfilesharingapp.data.entity.UserPublicKeyEntity
import com.vadim212.securityfilesharingapp.data.repository.UserPublicKeyRepository
import com.vadim212.securityfilesharingapp.domain.UserPublicKey
import com.vadim212.securityfilesharingapp.domain.base.BaseNetworkUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetUserPublicKey @Inject constructor(var userPublicKeyRepository: UserPublicKeyRepository) :
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