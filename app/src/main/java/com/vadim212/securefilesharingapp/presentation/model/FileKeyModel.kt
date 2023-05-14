package com.vadim212.securefilesharingapp.presentation.model

class FileKeyModel(fileKey: String) {
    private var _fileKey: String = fileKey

    val fileKey: String
        get() = _fileKey
}