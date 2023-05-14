package com.vadim212.securefilesharingapp.domain.utils

import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AESEncryptionOptionsHelper: Helper {
    companion object {
        fun createSymmetricKey(keySize: Int = EncryptionConstants.AES_SECRET_KEY_SIZE): SecretKey {
            val generator = KeyGenerator.getInstance(EncryptionConstants.AES_ALGORITHM_NAME)
            generator.init(keySize)
            return generator.generateKey()
        }

        fun generateRandomInitializationVector(blockSize: Int): ByteArray {
            val secureRandom = SecureRandom()
            val iv = ByteArray(blockSize)
            secureRandom.nextBytes(iv)
            return iv
        }
    }
}