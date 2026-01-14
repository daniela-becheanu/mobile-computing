package com.example.billboards.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "auth")

class TokenStore(private val context: Context) {
    private val KEY_TOKEN = stringPreferencesKey("token")
    private val KEY_USER = stringPreferencesKey("userId")

    suspend fun setSession(token: String, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER] = userId
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_USER)
        }
    }

    suspend fun tokenFlow() = context.dataStore.data

    suspend fun getUserId(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_USER]
    }

    // folosit în interceptor (ok pentru demo)
    fun getTokenBlocking(): String? = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[KEY_TOKEN]
    }
}
