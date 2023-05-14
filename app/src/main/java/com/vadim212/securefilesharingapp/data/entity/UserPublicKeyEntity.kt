package com.vadim212.securefilesharingapp.data.entity

import com.google.gson.annotations.SerializedName

data class UserPublicKeyEntity(
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("pb_key") var pbKey: String? = null
)
