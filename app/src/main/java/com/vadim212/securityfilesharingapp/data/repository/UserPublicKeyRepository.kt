package com.vadim212.securityfilesharingapp.data.repository

import com.vadim212.securityfilesharingapp.data.UserPublicKey
import com.vadim212.securityfilesharingapp.data.api.ApiImplementation
import com.vadim212.securityfilesharingapp.domain.repository.UserPublicKeyDomainRepository
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

class UserPublicKeyRepository: UserPublicKeyDomainRepository {
    var apiImplementation: ApiImplementation

    init {
        apiImplementation = ApiImplementation()
    }

    override fun initiatePostUserPbKey(body: UserPublicKey): Observable<ResponseBody> {
        return apiImplementation.postUserPbKey(body)
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
                    response.body()
                } else {
                    throw Exception("Code ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
    }
}