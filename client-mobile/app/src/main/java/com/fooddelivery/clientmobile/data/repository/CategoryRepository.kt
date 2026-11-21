package com.fooddelivery.clientmobile.data.repository

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import com.fooddelivery.clientmobile.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun listCategories(
        restaurantId: String,
        page: Int = 0,
        size: Int = 20,
        sort: String = "displayOrder,asc"
    ): Result<PageResponse<Category>> {
        return try {
            val response = apiService.listCategories(restaurantId, page, size, sort)
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
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