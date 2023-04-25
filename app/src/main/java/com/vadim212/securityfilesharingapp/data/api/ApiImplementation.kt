package com.vadim212.securityfilesharingapp.data.api

import com.vadim212.securityfilesharingapp.data.entity.FileKeyEntity
import com.vadim212.securityfilesharingapp.data.entity.UserPublicKeyEntity
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class ApiImplementation() : ApiInterface {
    var api: ApiInterface

    init {
        api = ApiConnection().initializeApi()
    }

    override fun postUserPbKey(body: UserPublicKeyEntity): Observable<Response<ResponseBody>> {
        return api.postUserPbKey(body)
    }

    override fun getUserPbKey(userId: String): Observable<Response<UserPublicKeyEntity>> {
        return api.getUserPbKey(userId)
    }

    override fun shareFile(
        senderUserId: RequestBody,
        recipientUserId: RequestBody,
        encryptedFile: MultipartBody.Part,
        encryptedFileKey: RequestBody
    ): Observable<Response<ResponseBody>> {
        return api.shareFile(senderUserId, recipientUserId, encryptedFile, encryptedFileKey)
    }

    override fun downloadFile(
        senderUserId: String,
        recipientUserId: String
    ): Observable<Response<ResponseBody>> {
        return api.downloadFile(senderUserId, recipientUserId)
    }

    override fun getFileKey(
        senderUserId: String,
        recipientUserId: String
    ): Observable<Response<FileKeyEntity>> {
        return api.getFileKey(senderUserId, recipientUserId)
    }
}