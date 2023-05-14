package com.vadim212.securefilesharingapp.domain

class FileKey(fileKey: String) {
    private var _fileKey: String = fileKey

    val fileKey: String
        get() = _fileKey
}