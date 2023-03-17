package com.vadim212.securityfilesharingapp.domain.usecase

import com.vadim212.securityfilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securityfilesharingapp.domain.base.BaseNetworkUseCase
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class DownloadFile(var fileSharingRepository: FileSharingRepository): BaseNetworkUseCase<ResponseBody, DownloadFile.Companion.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<ResponseBody> {
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