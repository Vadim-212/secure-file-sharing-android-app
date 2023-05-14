package com.vadim212.securefilesharingapp.domain.utils

import android.content.Context
import kotlinx.coroutines.flow.Flow
import com.vadim212.securefilesharingapp.domain.UserPublicKey

class RecipientUserPublicKeyHelper: Helper {
    companion object {
        //private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
        private const val RECIPIENT_PUBLIC_KEY_KEY = "recipient_user_public_key"

        fun getRecipientPublicKeyFromDataStore(context: Context): Flow<String> = context.readStringFromDataStore(
            RECIPIENT_PUBLIC_KEY_KEY)

        suspend fun saveRecipientPublicKeyToDataStore(userPublicKey: UserPublicKey, context: Context) {
            context.writeStringToDataStore(RECIPIENT_PUBLIC_KEY_KEY, userPublicKey.pbKey)
        }
    }
}