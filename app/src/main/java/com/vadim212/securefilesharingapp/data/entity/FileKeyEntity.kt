package com.vadim212.securefilesharingapp.data.entity

import com.google.gson.annotations.SerializedName

data class FileKeyEntity(
    @SerializedName("file_key") var fileKey: String? = null
)
