package com.vadim212.securityfilesharingapp.domain.repository

import com.vadim212.securityfilesharingapp.data.entity.FileKeyEntity
import com.vadim212.securityfilesharingapp.domain.DownloadedFile
import com.vadim212.securityfilesharingapp.domain.FileKey
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

interface FileSharingDomainRepository {
    fun initiateShareFile(
        senderUserId: RequestBody,
        recipientUserId: RequestBody,
        encryptedFile: MultipartBody.Part,
        encryptedFileKey: RequestBody
    ): Observable<ResponseBody>

    fun initiateDownloadFile(
        senderUserId: String,
        recipientUserId: String
    ): Observable<DownloadedFile> // TODO: changed return type to Response so headers can be retrieved

    fun initiateGetFileKey(
        senderUserId: String,
        recipientUserId: String
    ): Observable<FileKey>
}