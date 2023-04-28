package com.vadim212.securityfilesharingapp.domain.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.vadim212.securityfilesharingapp.domain.UserPublicKey

class RecipientUserPublicKeyHelper(private val context: Context): Helper {
    companion object {
        //private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
        private const val RECIPIENT_PUBLIC_KEY_KEY = "recipient_user_public_key"
    }

    fun getRecipientPublicKeyFromDataStore(): Flow<String> = context.readStringFromDataStore(
        RECIPIENT_PUBLIC_KEY_KEY)

    suspend fun saveRecipientPublicKeyToDataStore(userPublicKey: UserPublicKey) {
        context.writeStringToDataStore(RECIPIENT_PUBLIC_KEY_KEY, userPublicKey.pbKey)
    }

}