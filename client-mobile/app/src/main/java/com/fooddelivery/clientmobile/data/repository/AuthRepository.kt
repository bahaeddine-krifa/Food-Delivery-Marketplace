package com.fooddelivery.clientmobile.data.repository

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import com.fooddelivery.clientmobile.data.api.ApiService
import com.fooddelivery.clientmobile.data.local.TokenDataStore
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenDataStore: TokenDataStore
) {

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(request)
            handleAuthResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(request)
            handleAuthResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = tokenDataStore.getRefreshToken()
            if (refreshToken != null) {
                val response = apiService.logout(LogoutRequest(refreshToken))
                if (response.isSuccessful) {
                    tokenDataStore.clearTokens()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.message() ?: "Logout failed"))
                }
            } else {
                tokenDataStore.clearTokens()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            tokenDataStore.clearTokens()
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(): Result<TokenResponse> {
        return try {
            val refreshToken = tokenDataStore.getRefreshToken()
            if (refreshToken == null) {
                return Result.failure(Exception("No refresh token available"))
            }

            val response = apiService.refreshToken(RefreshRequest(refreshToken))
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    if (body.success && body.data != null) {
                        tokenDataStore.saveTokens(
                            body.data!!.accessToken,
                            body.data!!.refreshToken,
                            body.data!!.expiresIn
                        )
                        return Result.success(body.data!!)
                    }
                }
                Result.failure(Exception(body?.message ?: "Token refresh failed"))
            } else {
                if (response.code() == 401) {
                    tokenDataStore.clearTokens()
                }
                Result.failure(Exception(response.message() ?: "Token refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Result<Unit> {
        return try {
            val response = apiService.forgotPassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to send reset email"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        return try {
            val response = apiService.resetPassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Password reset failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest): Result<Unit> {
        return try {
            val response = apiService.changePassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Password change failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(request: VerifyEmailRequest): Result<Unit> {
        return try {
            val response = apiService.verifyEmail(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Email verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<UserProfileResponse> {
        return try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    if (body.success && body.data != null) {
                        return Result.success(body.data!!)
                    }
                }
                Result.failure(Exception(body?.message ?: "Failed to get user"))
            } else {
                if (response.code() == 401) {
                    tokenDataStore.clearTokens()
                }
                Result.failure(Exception(response.message() ?: "Failed to get user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleAuthResponse(response: Response<ApiResponse<AuthResponse>>): Result<AuthResponse> {
        if (response.isSuccessful) {
            response.body()?.let { body ->
                if (body.success && body.data != null) {
                    tokenDataStore.saveTokens(
                        body.data!!.accessToken,
                        body.data!!.refreshToken,
                        body.data!!.expiresIn
                    )
                    return Result.success(body.data!!)
                }
            }
            Result.failure(Exception(body?.message ?: "Authentication failed"))
        } else {
            Result.failure(Exception(response.message() ?: "Authentication failed"))
        }
    }
}