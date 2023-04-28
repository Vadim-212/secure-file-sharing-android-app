package com.vadim212.securityfilesharingapp.domain.utils

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserIdHelper(private val context: Context): Helper {
    companion object {
        private const val USER_ID_KEY = "user_id_key"
    }

    fun getUserIdFromDataStore(): Flow<String> = context.readStringFromDataStore(USER_ID_KEY)

    suspend fun saveUserIdToDataStore(userId: String) {
        context.writeStringToDataStore(USER_ID_KEY, userId)
    }
}