package com.vadim212.securityfilesharingapp.domain.utils

class EncryptionConstants {
    companion object {
        const val RSA_CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        const val AES_CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding"

        const val RSA_ALGORITHM_NAME = "RSA"

        const val AES_ALGORITHM_NAME = "AES"
        const val AES_SECRET_KEY_SIZE = 256

        const val KEYSTORE_TYPE_NAME = "AndroidKeyStore"
        const val KEYSTORE_ALIAS_NAME = "UserRSAKeys"
    }
}