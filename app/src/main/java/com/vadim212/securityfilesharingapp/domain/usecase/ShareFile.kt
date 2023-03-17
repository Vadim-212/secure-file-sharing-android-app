package com.vadim212.securityfilesharingapp.domain.usecase

import com.vadim212.securityfilesharingapp.data.UserPublicKey
import com.vadim212.securityfilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securityfilesharingapp.domain.base.BaseNetworkUseCase
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class ShareFile(var fileSharingRepository: FileSharingRepository): BaseNetworkUseCase<ResponseBody, ShareFile.Companion.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<ResponseBody> {
        return this.fileSharingRepository.initiateShareFile(params.senderUserId, params.recipientUserId, params.encryptedFile, params.encryptedFileKey)
    }

    companion object {
        class Params(val senderUserId: String,
                     val recipientUserId: String,
                     val encryptedFile: MultipartBody.Part,
                     val encryptedFileKey: String) {
            companion object {
                fun forShareFile(senderUserId: String,
                                 recipientUserId: String,
                                 encryptedFile: MultipartBody.Part,
                                 encryptedFileKey: String) = Params(senderUserId, recipientUserId, encryptedFile, encryptedFileKey)
            }
        }
    }
}