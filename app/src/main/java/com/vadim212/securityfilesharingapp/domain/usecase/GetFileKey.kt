package com.vadim212.securityfilesharingapp.domain.usecase

import com.vadim212.securityfilesharingapp.data.entity.FileKeyEntity
import com.vadim212.securityfilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securityfilesharingapp.domain.FileKey
import com.vadim212.securityfilesharingapp.domain.base.BaseNetworkUseCase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetFileKey @Inject constructor(var fileSharingRepository: FileSharingRepository) :
    BaseNetworkUseCase<FileKey, GetFileKey.Companion.Params>() {
    override fun buildUseCaseObservable(params: Params): Observable<FileKey> {
        return this.fileSharingRepository.initiateGetFileKey(params.senderUserId, params.recipientUserId)
    }

    companion object {
        class Params(val senderUserId: String,
                     val recipientUserId: String) {
            companion object {
                fun forFileKey(senderUserId: String,
                                    recipientUserId: String) = Params(senderUserId, recipientUserId)
            }
        }
    }
}