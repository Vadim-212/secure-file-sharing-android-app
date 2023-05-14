package com.vadim212.securefilesharingapp.data.repository

import com.vadim212.securefilesharingapp.data.entity.UserPublicKeyEntity
import com.vadim212.securefilesharingapp.data.api.ApiImplementation
import com.vadim212.securefilesharingapp.domain.UserPublicKey
import com.vadim212.securefilesharingapp.domain.repository.UserPublicKeyDomainRepository
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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