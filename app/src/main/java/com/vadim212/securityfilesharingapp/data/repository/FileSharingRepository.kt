package com.vadim212.securityfilesharingapp.data.repository

import android.util.Log
import com.vadim212.securityfilesharingapp.data.FileKey
import com.vadim212.securityfilesharingapp.data.api.ApiImplementation
import com.vadim212.securityfilesharingapp.domain.repository.FileSharingDomainRepository
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class FileSharingRepository: FileSharingDomainRepository {
    var apiImplementation: ApiImplementation

    init {
        apiImplementation = ApiImplementation()
    }

    override fun initiateShareFile(
        senderUserId: String,
        recipientUserId: String,
        encryptedFile: MultipartBody.Part,
        encryptedFileKey: String
    ): Observable<ResponseBody> {
        return apiImplementation.shareFile(senderUserId, recipientUserId, encryptedFile, encryptedFileKey)
            .map { response ->
                if (response.isSuccessful) {
                    response.body()
                } else {
                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
    }

    override fun initiateDownloadFile(
        senderUserId: String,
        recipientUserId: String
    ): Observable<ResponseBody> {
        return apiImplementation.downloadFile(senderUserId, recipientUserId)
            .map { response ->
                if (response.isSuccessful) {
                    response.body()
                } else {
                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
    }

    override fun initiateGetFileKey(
        senderUserId: String,
        recipientUserId: String
    ): Observable<FileKey> {
        return apiImplementation.getFileKey(senderUserId, recipientUserId)
            .map { response ->
                if (response.isSuccessful) {
                    response.body()
                } else {
                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
    }
}