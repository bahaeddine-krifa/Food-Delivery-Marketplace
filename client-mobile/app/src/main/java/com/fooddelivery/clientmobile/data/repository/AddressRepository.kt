package com.fooddelivery.clientmobile.data.repository

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import com.fooddelivery.clientmobile.data.api.ApiService
import com.fooddelivery.clientmobile.data.local.TokenDataStore
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenDataStore: TokenDataStore
) {

    suspend fun createAddress(request: CreateAddressRequest): Result<AddressResponse> {
        return try {
            val response = apiService.createAddress(request)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAddresses(page: Int = 0, size: Int = 20): Result<PageResponse<AddressResponse>> {
        return try {
            val response = apiService.getAddresses(page, size)
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDefaultAddress(): Result<AddressResponse?> {
        return try {
            val response = apiService.getDefaultAddress()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    if (body.success) {
                        return Result.success(body.data)
                    }
                    return Result.failure(Exception(body.message ?: "Request failed"))
                }
                Result.failure(Exception("Empty response"))
            } else {
                if (response.code() == 401) {
                    tokenDataStore.clearTokens()
                }
                Result.failure(Exception(response.message() ?: "Request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAddress(addressId: String): Result<AddressResponse> {
        return try {
            val response = apiService.getAddress(addressId)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAddress(addressId: String, request: UpdateAddressRequest): Result<AddressResponse> {
        return try {
            val response = apiService.updateAddress(addressId, request)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setDefaultAddress(addressId: String): Result<AddressResponse> {
        return try {
            val response = apiService.setDefaultAddress(addressId, SetDefaultAddressRequest(addressId))
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            val response = apiService.deleteAddress(addressId)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    if (body.success) {
                        return Result.success(Unit)
                    }
                    return Result.failure(Exception(body.message ?: "Request failed"))
                }
                Result.failure(Exception("Empty response"))
            } else {
                if (response.code() == 401) {
                    tokenDataStore.clearTokens()
                }
                Result.failure(Exception(response.message() ?: "Request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleSingleResponse(response: Response<ApiResponse<T>>): Result<T> {
        if (response.isSuccessful) {
            response.body()?.let { body ->
                if (body.success && body.data != null) {
                    return Result.success(body.data!!)
                }
                return Result.failure(Exception(body.message ?: "Request failed"))
            }
            Result.failure(Exception("Empty response"))
        } else {
            if (response.code() == 401) {
                tokenDataStore.clearTokens()
            }
            Result.failure(Exception(response.message() ?: "Request failed"))
        }
    }

    private fun <T> handlePageResponse(response: Response<ApiResponse<PageResponse<T>>>): Result<PageResponse<T>> {
        if (response.isSuccessful) {
            response.body()?.let { body ->
                if (body.success && body.data != null) {
                    return Result.success(body.data!!)
                }
                return Result.failure(Exception(body.message ?: "Request failed"))
            }
            Result.failure(Exception("Empty response"))
        } else {
            if (response.code() == 401) {
                tokenDataStore.clearTokens()
            }
            Result.failure(Exception(response.message() ?: "Request failed"))
        }
    }
}