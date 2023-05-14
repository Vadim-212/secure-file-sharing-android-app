package com.vadim212.securefilesharingapp.domain.usecase

import com.vadim212.securefilesharingapp.domain.FileKey
import com.vadim212.securefilesharingapp.domain.base.BaseNetworkUseCase
import com.vadim212.securefilesharingapp.domain.repository.FileSharingDomainRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetFileKey @Inject constructor(private var fileSharingRepository: FileSharingDomainRepository) :
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