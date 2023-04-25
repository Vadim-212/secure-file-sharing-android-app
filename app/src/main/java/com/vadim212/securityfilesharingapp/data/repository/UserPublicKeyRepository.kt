package com.vadim212.securityfilesharingapp.data.repository

import com.vadim212.securityfilesharingapp.data.entity.UserPublicKeyEntity
import com.vadim212.securityfilesharingapp.data.api.ApiImplementation
import com.vadim212.securityfilesharingapp.domain.UserPublicKey
import com.vadim212.securityfilesharingapp.domain.repository.UserPublicKeyDomainRepository
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import javax.inject.Inject

class UserPublicKeyRepository @Inject constructor() : UserPublicKeyDomainRepository {
    var apiImplementation: ApiImplementation

    init {
        apiImplementation = ApiImplementation()
    }

    override fun initiatePostUserPbKey(body: UserPublicKey): Observable<ResponseBody> {
        return apiImplementation.postUserPbKey(UserPublicKeyEntity(body.userId, body.pbKey))
            .map { response ->
                if (response.isSuccessful) {
                    response.body()
                } else {
                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
    }

    override fun initiateGetUserPbKey(userId: String): Observable<UserPublicKey> {
        return apiImplementation.getUserPbKey(userId)
            .map { response ->
                if (response.isSuccessful) {
                    //response.body()
                    UserPublicKey(response.body()!!.userId!!, response.body()!!.pbKey!!)
                } else {
                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
    }
}