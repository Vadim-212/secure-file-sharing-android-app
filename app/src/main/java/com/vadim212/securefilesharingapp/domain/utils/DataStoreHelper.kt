package com.vadim212.securefilesharingapp.domain.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//class DataStoreHelper: Helper {
//    companion object {
//        const val PREFERENCE_NAME = "app_settings"
//    }

private val Context.dataStore by preferencesDataStore("app_settings")

suspend fun Context.writeStringToDataStore(key: String, value: String) {
    dataStore.edit { preferences ->
        preferences[stringPreferencesKey(key)] = value
    }
}

fun Context.readStringFromDataStore(key: String): Flow<String> {
    return dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(key)] ?: ""
    }
}

suspend fun Context.writeBooleanToDataStore(key: String, value: Boolean) {
    dataStore.edit { preferences ->
        preferences[booleanPreferencesKey(key)] = value
    }
}

fun Context.readBooleanFromDataStore(key: String): Flow<Boolean> {
    return dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(key)] ?: false
    }
}

//}