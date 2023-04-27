package com.vadim212.securityfilesharingapp.domain.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec

class AsymmetricKeysHelper: Helper {
    fun createAsymmetricKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, EncryptionConstants.KEYSTORE_TYPE_NAME)
        val builder = KeyGenParameterSpec.Builder(EncryptionConstants.KEYSTORE_ALIAS_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        generator.initialize(builder.build())
        return generator.genKeyPair()
    }

    fun getAsymmetricKeyPair(): KeyPair? {
        val keyStore = KeyStore.getInstance(EncryptionConstants.KEYSTORE_TYPE_NAME)
        keyStore.load(null)

        val privateKey = keyStore.getKey(EncryptionConstants.KEYSTORE_ALIAS_NAME, null) as PrivateKey?
        val publicKey = keyStore.getCertificate(EncryptionConstants.KEYSTORE_ALIAS_NAME)?.publicKey

        return if (privateKey != null && publicKey != null) {
            KeyPair(publicKey, privateKey)
        } else {
            null
        }
    }

    fun stringToPublicKey(publicKeyBase64String: String): PublicKey {
        val bytes = Base64.decode(publicKeyBase64String, Base64.DEFAULT)
        val x509KeySpec = X509EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance(EncryptionConstants.RSA_ALGORITHM_NAME)
        return keyFactory.generatePublic(x509KeySpec)
    }
}