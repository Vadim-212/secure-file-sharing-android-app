package com.vadim212.securityfilesharingapp.domain.repository

import com.vadim212.securityfilesharingapp.data.UserPublicKey
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

interface UserPublicKeyDomainRepository {
    fun initiatePostUserPbKey(body: UserPublicKey): Observable<ResponseBody>

    fun initiateGetUserPbKey(userId: String): Observable<UserPublicKey>
}