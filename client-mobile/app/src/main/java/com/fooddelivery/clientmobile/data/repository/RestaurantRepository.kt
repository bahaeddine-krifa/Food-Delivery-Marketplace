package com.fooddelivery.clientmobile.data.repository

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import com.fooddelivery.clientmobile.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun listRestaurants(
        page: Int = 0,
        size: Int = 20,
        cuisine: String? = null,
        search: String? = null,
        sort: String = "createdAt,desc"
    ): Result<PageResponse<Restaurant>> {
        return try {
            val response = apiService.listRestaurants(page, size, cuisine, search, sort)
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOpenRestaurants(
        page: Int = 0,
        size: Int = 20,
        sort: String = "createdAt,desc"
    ): Result<PageResponse<Restaurant>> {
        return try {
            val response = apiService.getOpenRestaurants(page, size, sort)
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRestaurant(id: String): Result<Restaurant> {
        return try {
            val response = apiService.getPublicRestaurant(id)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRestaurantHours(id: String): Result<List<RestaurantHours>> {
        return try {
            val response = apiService.getRestaurantHours(id)
            handleListResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleSingleResponse(response: retrofit2.Response<ApiResponse<T>>): Result<T> {
        if (response.isSuccessful) {
            response.body()?.let { body ->
                if (body.success && body.data != null) {
                    return Result.success(body.data!!)
                }
                return Result.failure(Exception(body.message ?: "Request failed"))
            }
            Result.failure(Exception("Empty response"))
        } else {
            Result.failure(Exception(response.message() ?: "Request failed"))
        }
    }

    private fun <T> handleListResponse(response: retrofit2.Response<ApiResponse<List<T>>>): Result<List<T>> {
        if (response.isSuccessful) {
            response.body()?.let { body ->
                if (body.success && body.data != null) {
                    return Result.success(body.data!!)
                }
                return Result.failure(Exception(body.message ?: "Request failed"))
            }
            Result.failure(Exception("Empty response"))
        } else {
            Result.failure(Exception(response.message() ?: "Request failed"))
        }
    }

    private fun <T> handlePageResponse(response: retrofit2.Response<ApiResponse<PageResponse<T>>>): Result<PageResponse<T>> {
        if (response.isSuccessful) {
            response.body()?.let { body ->
                if (body.success && body.data != null) {
                    return Result.success(body.data!!)
                }
                return Result.failure(Exception(body.message ?: "Request failed"))
            }
            Result.failure(Exception("Empty response"))
        } else {
            Result.failure(Exception(response.message() ?: "Request failed"))
        }
    }
}