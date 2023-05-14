package com.vadim212.securefilesharingapp.domain.usecase

import com.vadim212.securefilesharingapp.domain.DownloadedFile
import com.vadim212.securefilesharingapp.domain.base.BaseNetworkUseCase
import com.vadim212.securefilesharingapp.domain.repository.FileSharingDomainRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DownloadFile @Inject constructor(private var fileSharingRepository: FileSharingDomainRepository): BaseNetworkUseCase<DownloadedFile, DownloadFile.Companion.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<DownloadedFile> { // TODO: changed return type to Response so headers can be retrieved
        return this.fileSharingRepository.initiateDownloadFile(params.senderUserId, params.recipientUserId)
    }

    companion object {
        class Params(val senderUserId: String,
                     val recipientUserId: String) {
            companion object {
                fun forDownloadFile(senderUserId: String,
                                 recipientUserId: String) = Params(senderUserId, recipientUserId)
            }
        }
    }
}