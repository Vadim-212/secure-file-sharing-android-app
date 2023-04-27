package com.vadim212.securityfilesharingapp.domain.utils

import java.security.Key
import javax.crypto.Cipher

class RSAEncryptionHelper() : AsymmetricEncryptionHelper {
    private val cipher: Cipher = Cipher.getInstance(EncryptionConstants.RSA_CIPHER_TRANSFORMATION)

    override fun encrypt(dataBytes: ByteArray, publicKey: Key): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(dataBytes)
    }

    override fun decrypt(dataBytes: ByteArray, privateKey: Key): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(dataBytes)
    }
}