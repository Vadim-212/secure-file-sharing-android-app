package com.vadim212.securityfilesharingapp.domain.usecase

import com.vadim212.securityfilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securityfilesharingapp.domain.DownloadedFile
import com.vadim212.securityfilesharingapp.domain.base.BaseNetworkUseCase
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class DownloadFile @Inject constructor(var fileSharingRepository: FileSharingRepository): BaseNetworkUseCase<DownloadedFile, DownloadFile.Companion.Params>() {

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