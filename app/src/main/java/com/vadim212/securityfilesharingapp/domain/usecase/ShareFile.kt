package com.vadim212.securityfilesharingapp.domain.usecase

import com.vadim212.securityfilesharingapp.data.repository.FileSharingRepository
import com.vadim212.securityfilesharingapp.domain.base.BaseNetworkUseCase
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

class ShareFile @Inject constructor(private var fileSharingRepository: FileSharingRepository): BaseNetworkUseCase<ResponseBody, ShareFile.Companion.Params>() {

    override fun buildUseCaseObservable(params: Params): Observable<ResponseBody> {
        return this.fileSharingRepository.initiateShareFile(params.senderUserId, params.recipientUserId, params.encryptedFile, params.encryptedFileKey)
    }

    fun fileToMultipartBodyPart(encryptedFile: File): MultipartBody.Part {
        val part = MultipartBody.Part.createFormData("encrypted_file", encryptedFile.name,
            encryptedFile.asRequestBody("text/plain".toMediaType())
        )
        return part
    }

    fun strToRequestBody(string: String): RequestBody {
        return string.toRequestBody("text/plain".toMediaType())
    }

    companion object {
        class Params(val senderUserId: RequestBody,
                     val recipientUserId: RequestBody,
                     val encryptedFile: MultipartBody.Part,
                     val encryptedFileKey: RequestBody) {
            companion object {
                fun forShareFile(senderUserId: RequestBody,
                                 recipientUserId: RequestBody,
                                 encryptedFile: MultipartBody.Part,
                                 encryptedFileKey: RequestBody) = Params(senderUserId, recipientUserId, encryptedFile, encryptedFileKey)
            }
        }
    }
}