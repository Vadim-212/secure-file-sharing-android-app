package com.vadim212.securefilesharingapp.presentation.view

interface ReceivingFilesView: LoadDataView {
    fun onFileDownloaded()

    fun onFileDecrypted()

    fun onFilesSaved()
}