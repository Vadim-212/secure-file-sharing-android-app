package com.vadim212.securityfilesharingapp.data.api

import com.vadim212.securityfilesharingapp.data.FileKey
import com.vadim212.securityfilesharingapp.data.UserPublicKey
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class ApiImplementation() : ApiInterface {
    var api: ApiInterface

    init {
        api = ApiConnection().initializeApi()
    }

    override fun postUserPbKey(body: UserPublicKey): Observable<Response<ResponseBody>> {
        return api.postUserPbKey(body)
    }

    override fun getUserPbKey(userId: String): Observable<Response<UserPublicKey>> {
        return api.getUserPbKey(userId)
    }

    override fun shareFile(
        senderUserId: String,
        recipientUserId: String,
        encryptedFile: MultipartBody.Part,
        encryptedFileKey: String
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
    ): Observable<Response<FileKey>> {
        return api.getFileKey(senderUserId, recipientUserId)
    }
}