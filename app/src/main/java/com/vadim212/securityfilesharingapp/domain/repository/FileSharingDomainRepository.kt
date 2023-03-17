package com.vadim212.securityfilesharingapp.domain.repository

import com.vadim212.securityfilesharingapp.data.FileKey
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface FileSharingDomainRepository {
    fun initiateShareFile(
        senderUserId: String,
        recipientUserId: String,
        encryptedFile: MultipartBody.Part,
        encryptedFileKey: String
    ): Observable<ResponseBody>

    fun initiateDownloadFile(
        senderUserId: String,
        recipientUserId: String
    ): Observable<ResponseBody>

    fun initiateGetFileKey(
        senderUserId: String,
        recipientUserId: String
    ): Observable<FileKey>
}