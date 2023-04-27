package com.vadim212.securityfilesharingapp.domain.utils

import javax.crypto.SecretKey

interface SymmetricEncryptionHelper: Helper {
    fun encrypt(dataBytes: ByteArray, secretKey: SecretKey, initializationVector: ByteArray): ByteArray

    fun decrypt(dataBytes: ByteArray, secretKey: SecretKey, initializationVector: ByteArray): ByteArray
}