package com.fooddelivery.clientmobile.data.repository

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import com.fooddelivery.clientmobile.data.api.ApiService
import com.fooddelivery.clientmobile.data.local.TokenDataStore
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenDataStore: TokenDataStore
) {

    suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse> {
        return try {
            val response = apiService.createOrder(request)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrders(
        status: OrderStatus? = null,
        page: Int = 0,
        size: Int = 20,
        sort: String = "createdAt,desc"
    ): Result<PageResponse<OrderSummaryResponse>> {
        return try {
            val response = apiService.getOrders(status, page, size, sort)
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrder(orderId: String): Result<OrderResponse> {
        return try {
            val response = apiService.getOrder(orderId)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelOrder(orderId: String, reason: String? = null): Result<OrderResponse> {
        return try {
            val response = apiService.cancelOrder(orderId, reason)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderHistory(
        orderId: String,
        page: Int = 0,
        size: Int = 20
    ): Result<PageResponse<OrderStatusHistoryResponse>> {
        return try {
            val response = apiService.getOrderHistory(orderId, page, size)
            handlePageResponse(response)
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