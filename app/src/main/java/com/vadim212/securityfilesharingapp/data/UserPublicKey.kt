package com.vadim212.securityfilesharingapp.data

import com.google.gson.annotations.SerializedName

data class UserPublicKey(
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("pb_key") var pbKey: String? = null
)
