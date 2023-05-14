package com.vadim212.securefilesharingapp.data.api

import com.vadim212.securefilesharingapp.data.entity.FileKeyEntity
import com.vadim212.securefilesharingapp.data.entity.UserPublicKeyEntity
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiInterface {
    @POST("user_pbkey")
    fun postUserPbKey(@Body body: UserPublicKeyEntity): Observable<Response<ResponseBody>>

    @GET("user_pbkey")
    fun getUserPbKey(@Query("user_id") userId: String): Observable<Response<UserPublicKeyEntity>>

    @Multipart
    @POST("share_file")
    fun shareFile(@Part("sender_user_id") senderUserId: RequestBody,
                  @Part("recipient_user_id") recipientUserId: RequestBody,
                  @Part encryptedFile: MultipartBody.Part,
                  @Part("encrypted_file_key") encryptedFileKey: RequestBody): Observable<Response<ResponseBody>>

    @GET("download_file")
    @Streaming
    fun downloadFile(@Query("sender_user_id") senderUserId: String,
                     @Query("recipient_user_id") recipientUserId: String): Observable<Response<ResponseBody>>

    @GET("file_key")
    fun getFileKey(@Query("sender_user_id") senderUserId: String,
                   @Query("recipient_user_id") recipientUserId: String): Observable<Response<FileKeyEntity>>
}