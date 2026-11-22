package com.fooddelivery.deliverymobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val EXPIRES_AT_KEY = stringPreferencesKey("expires_at")
    private val TOKEN_TYPE_KEY = stringPreferencesKey("token_type")

    override fun getAccessToken(): String? {
        return runBlocking {
            context.dataStore.data.first()[ACCESS_TOKEN_KEY] ?: ""
        }
    }

    override fun getRefreshToken(): String? {
        return runBlocking {
            context.dataStore.data.first()[REFRESH_TOKEN_KEY] ?: ""
        }
    }

    override fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expiresAt = System.currentTimeMillis() + expiresIn * 1000
        runBlocking {
            context.dataStore.edit { mutablePreferences ->
                mutablePreferences[ACCESS_TOKEN_KEY] = accessToken
                mutablePreferences[REFRESH_TOKEN_KEY] = refreshToken
                mutablePreferences[EXPIRES_AT_KEY] = expiresAt.toString()
                mutablePreferences[TOKEN_TYPE_KEY] = "Bearer"
            }
        }
    }

    override fun clearTokens() {
        runBlocking {
            context.dataStore.edit { mutablePreferences ->
                mutablePreferences[ACCESS_TOKEN_KEY] = ""
                mutablePreferences[REFRESH_TOKEN_KEY] = ""
                mutablePreferences[EXPIRES_AT_KEY] = ""
                mutablePreferences[TOKEN_TYPE_KEY] = ""
            }
        }
    }

    override fun isTokenExpired(): Boolean {
        val expiresAt = runBlocking {
            context.dataStore.data.first()[EXPIRES_AT_KEY]?.toLongOrNull() ?: 0L
        }
        return System.currentTimeMillis() >= expiresAt - 60000 // 1 minute buffer
    }
}

interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long)
    fun clearTokens()
    fun isTokenExpired(): Boolean
}