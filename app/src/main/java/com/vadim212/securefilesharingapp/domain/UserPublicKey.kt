package com.vadim212.securefilesharingapp.domain

class UserPublicKey(userId: String, pbKey: String) {
    private var _userId: String = userId
    private var _pbKey: String = pbKey

    val userId: String
        get() = _userId
    val pbKey: String
        get() = _pbKey
}