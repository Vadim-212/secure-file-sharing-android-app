package com.vadim212.securefilesharingapp.domain.utils

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserIdHelper: Helper {
    companion object {
        private const val USER_ID_KEY = "user_id_key"

        fun getUserIdFromDataStore(context: Context): Flow<String> = context.readStringFromDataStore(USER_ID_KEY)

        suspend fun saveUserIdToDataStore(userId: String, context: Context) {
            context.writeStringToDataStore(USER_ID_KEY, userId)
        }
    }
}