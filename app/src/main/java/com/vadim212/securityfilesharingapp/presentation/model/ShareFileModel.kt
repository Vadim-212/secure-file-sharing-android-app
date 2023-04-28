package com.vadim212.securityfilesharingapp.presentation.model

import java.io.File

class ShareFileModel(senderUserId: String, recipientUserId: String,
                     encryptedFile: File, encryptedFileKey: String) {
    private val _senderUserId: String = senderUserId
    private val _recipientUserId: String = recipientUserId
    private val _encryptedFile: File = encryptedFile
    private val _encryptedFileKey: String = encryptedFileKey

    val senderUserId: String
        get() = _senderUserId
    val recipientUserId: String
        get() = _recipientUserId
    val encryptedFile: File
        get() = _encryptedFile
    val encryptedFileKey: String
        get() = _encryptedFileKey
}