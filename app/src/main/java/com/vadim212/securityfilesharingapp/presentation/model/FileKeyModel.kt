package com.vadim212.securityfilesharingapp.presentation.model

class FileKeyModel(fileKey: String) {
    private var _fileKey: String = fileKey

    val fileKey: String
        get() = _fileKey
}