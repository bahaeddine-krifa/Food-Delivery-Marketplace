package com.fooddelivery.deliverymobile.data.repository

import com.fooddelivery.deliverymobile.data.api.ApiResponse
import com.fooddelivery.deliverymobile.data.api.ApiService
import com.fooddelivery.deliverymobile.data.api.AuthResponse
import com.fooddelivery.deliverymobile.data.api.ChangePasswordRequest
import com.fooddelivery.deliverymobile.data.api.ForgotPasswordRequest
import com.fooddelivery.deliverymobile.data.api.LoginRequest
import com.fooddelivery.deliverymobile.data.api.LogoutRequest
import com.fooddelivery.deliverymobile.data.api.RefreshRequest
import com.fooddelivery.deliverymobile.data.api.ResetPasswordRequest
import com.fooddelivery.deliverymobile.data.api.TokenResponse
import com.fooddelivery.deliverymobile.data.api.UserProfileResponse
import com.fooddelivery.deliverymobile.data.local.TokenProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import retrofit2.Response

@ActivityRetainedScoped
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenProvider: TokenProvider
) {

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        val response = apiService.login(request)
        return handleAuthResponse(response)
    }

    suspend fun refreshToken(): Result<TokenResponse> {
        val refreshToken = tokenProvider.getRefreshToken() ?: return Result.failure(Exception("No refresh token available"))
        val response = apiService.refreshToken(RefreshRequest(refreshToken))
        return handleTokenResponse(response)
    }

    suspend fun logout(): Result<Unit> {
        val refreshToken = tokenProvider.getRefreshToken() ?: return Result.success(Unit)
        val response = apiService.logout(LogoutRequest(refreshToken))
        tokenProvider.clearTokens()
        return if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Logout failed"))
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Result<Unit> {
        val response = apiService.forgotPassword(request)
        return if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception(response.message() ?: "Failed to send reset email"))
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        val response = apiService.resetPassword(request)
        return if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception(response.message() ?: "Failed to reset password"))
    }

    suspend fun changePassword(request: ChangePasswordRequest): Result<Unit> {
        val accessToken = tokenProvider.getAccessToken() ?: return Result.failure(Exception("Not authenticated"))
        val response = apiService.changePassword("Bearer $accessToken", request)
        return if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception(response.message() ?: "Failed to change password"))
    }

    suspend fun getProfile(): Result<UserProfileResponse> {
        val accessToken = tokenProvider.getAccessToken() ?: return Result.failure(Exception("Not authenticated"))
        val response = apiService.getProfile("Bearer $accessToken")
        return handleApiResponse(response)
    }

    suspend fun isAuthenticated(): Boolean {
        val accessToken = tokenProvider.getAccessToken()
        return accessToken != null && accessToken.isNotEmpty() && !tokenProvider.isTokenExpired()
    }

    fun getAccessToken(): String? = tokenProvider.getAccessToken()
    fun getRefreshToken(): String? = tokenProvider.getRefreshToken()

    private suspend fun handleAuthResponse(response: Response<ApiResponse<AuthResponse>>): Result<AuthResponse> {
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { authResponse ->
                tokenProvider.saveTokens(authResponse.accessToken, authResponse.refreshToken, authResponse.expiresIn)
                return Result.success(authResponse)
            }
        }
        return Result.failure(Exception(response.body()?.message ?: response.message() ?: "Login failed"))
    }

    private suspend fun handleTokenResponse(response: Response<ApiResponse<TokenResponse>>): Result<TokenResponse> {
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { tokenResponse ->
                tokenProvider.saveTokens(tokenResponse.accessToken, tokenResponse.refreshToken, tokenResponse.expiresIn)
                return Result.success(tokenResponse)
            }
        }
        tokenProvider.clearTokens()
        return Result.failure(Exception(response.body()?.message ?: response.message() ?: "Token refresh failed"))
    }

    private suspend fun <T> handleApiResponse(response: Response<ApiResponse<T>>): Result<T> {
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { return Result.success(it) }
        }
        return Result.failure(Exception(response.body()?.message ?: response.message() ?: "Request failed"))
    }
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure<T>(val exception: Exception) : Result<T>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> failure(exception: Exception): Result<T> = Failure(exception)
    }

    fun isSuccess(): Boolean = this is Success<*>
    fun isFailure(): Boolean = this is Failure<*>

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Failure -> throw exception
    }

    fun exceptionOrNull(): Exception? = when (this) {
        is Success -> null
        is Failure -> exception
    }
}