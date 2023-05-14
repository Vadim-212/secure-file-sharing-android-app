package com.vadim212.securefilesharingapp.presentation.model

class DownloadedFileModel(fileBytes: ByteArray, filename: String){
    var fileBytes: ByteArray = fileBytes
        private set

    var filename: String = filename
        private set
}