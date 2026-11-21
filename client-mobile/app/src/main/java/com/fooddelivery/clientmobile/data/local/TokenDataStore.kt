package com.fooddelivery.clientmobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fooddelivery.clientmobile.data.api.AuthTokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext context: Context
) : AuthTokenProvider {

    private val Context.dataStore by preferencesDataStore(name = "auth_tokens")

    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
    private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")

    override suspend fun getAccessToken(): String? {
        val expiresAt = context.dataStore.data
            .map { it[EXPIRES_AT_KEY] ?: 0L }
            .first()

        if (expiresAt > 0 && System.currentTimeMillis() < expiresAt) {
            return context.dataStore.data
                .map { it[ACCESS_TOKEN_KEY] }
                .first()
        }
        return null
    }

    override suspend fun getRefreshToken(): String? {
        return context.dataStore.data
            .map { it[REFRESH_TOKEN_KEY] }
            .first()
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
        context.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
            it[EXPIRES_AT_KEY] = expiresAt
        }
    }

    override suspend fun clearTokens() {
        context.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = ""
            it[REFRESH_TOKEN_KEY] = ""
            it[EXPIRES_AT_KEY] = 0L
        }
    }

    suspend fun setRememberMe(remember: Boolean) {
        context.dataStore.edit {
            it[REMEMBER_ME_KEY] = remember
        }
    }

    suspend fun getRememberMe(): Boolean {
        return context.dataStore.data
            .map { it[REMEMBER_ME_KEY] ?: false }
            .first()
    }
}