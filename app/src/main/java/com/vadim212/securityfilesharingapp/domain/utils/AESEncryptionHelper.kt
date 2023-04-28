package com.vadim212.securityfilesharingapp.domain.utils

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class AESEncryptionHelper(): SymmetricEncryptionHelper {
    private val cipher: Cipher = Cipher.getInstance(EncryptionConstants.AES_CIPHER_TRANSFORMATION)

    override fun encrypt(dataBytes: ByteArray, secretKey: SecretKey, initializationVector: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(initializationVector))
        return cipher.doFinal(dataBytes)
    }

    override fun decrypt(dataBytes: ByteArray, secretKey: SecretKey, initializationVector: ByteArray): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(initializationVector))
        return cipher.doFinal(dataBytes)
    }

    fun getBlockSize(): Int = cipher.blockSize
}