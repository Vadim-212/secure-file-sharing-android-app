package com.vadim212.securityfilesharingapp.data

import com.google.gson.annotations.SerializedName

data class FileKey(
    @SerializedName("file_key") var fileKey: String? = null
)
