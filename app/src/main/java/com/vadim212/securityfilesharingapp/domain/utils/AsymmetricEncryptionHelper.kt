package com.vadim212.securityfilesharingapp.domain.utils

import java.security.Key
import javax.crypto.Cipher

interface AsymmetricEncryptionHelper: Helper {
    fun encrypt(dataBytes: ByteArray, publicKey: Key): ByteArray

    fun decrypt(dataBytes: ByteArray, privateKey: Key): ByteArray
}