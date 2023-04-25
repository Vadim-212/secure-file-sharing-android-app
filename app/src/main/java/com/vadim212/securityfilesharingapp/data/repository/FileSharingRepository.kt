package com.vadim212.securityfilesharingapp.data.repository

import com.vadim212.securityfilesharingapp.data.entity.FileKeyEntity
import com.vadim212.securityfilesharingapp.data.api.ApiImplementation
import com.vadim212.securityfilesharingapp.domain.DownloadedFile
import com.vadim212.securityfilesharingapp.domain.FileKey
import com.vadim212.securityfilesharingapp.domain.repository.FileSharingDomainRepository
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSharingRepository @Inject constructor() : FileSharingDomainRepository {
    var apiImplementation: ApiImplementation

    init {
        apiImplementation = ApiImplementation()
    }

    override fun initiateShareFile(
        senderUserId: RequestBody,
        recipientUserId: RequestBody,
        encryptedFile: MultipartBody.Part,
        encryptedFileKey: RequestBody
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

//    override fun initiateDownloadFile(
//        senderUserId: String,
//        recipientUserId: String
//    ): Observable<ResponseBody> {
//        return apiImplementation.downloadFile(senderUserId, recipientUserId)
//            .map { response ->
//                if (response.isSuccessful) {
//                    response.body() // TODO: change return type to Response so headers can be retrieved
//                } else {
//                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
//                }
//            }
//    }
        override fun initiateDownloadFile(
            senderUserId: String,
            recipientUserId: String
        ): Observable<DownloadedFile> { // TODO: changed return type to Response so headers can be retrieved
            return apiImplementation.downloadFile(senderUserId, recipientUserId).map { response ->
                if (response.isSuccessful) {
                    //response
                    DownloadedFile(response.body()!!, response.headers()) // TODO: add if null check
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
                    //response.body()
                    FileKey(response.body()!!.fileKey!!) // TODO: add if null check
                } else {
                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
    }
}