package com.fooddelivery.clientmobile.data.repository

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import com.fooddelivery.clientmobile.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun listProducts(
        restaurantId: String,
        page: Int = 0,
        size: Int = 20,
        categoryId: String? = null,
        availableOnly: Boolean = true,
        vegetarian: Boolean? = null,
        vegan: Boolean? = null,
        glutenFree: Boolean? = null,
        search: String? = null,
        sort: String = "displayOrder,asc"
    ): Result<PageResponse<Product>> {
        return try {
            val response = apiService.listProducts(
                restaurantId, page, size, categoryId, availableOnly,
                vegetarian, vegan, glutenFree, search, sort
            )
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProduct(restaurantId: String, productId: String): Result<Product> {
        return try {
            val response = apiService.getProduct(restaurantId, productId)
            handleSingleResponse(response)
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