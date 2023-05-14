package com.vadim212.securefilesharingapp.domain.repository

import com.vadim212.securefilesharingapp.domain.UserPublicKey
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

interface UserPublicKeyDomainRepository {
    fun initiatePostUserPbKey(body: UserPublicKey): Observable<ResponseBody>

    fun initiateGetUserPbKey(userId: String): Observable<UserPublicKey>
}