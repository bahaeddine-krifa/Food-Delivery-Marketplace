package com.fooddelivery.clientmobile.data.repository

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import com.fooddelivery.clientmobile.data.api.ApiService
import com.fooddelivery.clientmobile.data.local.TokenDataStore
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenDataStore: TokenDataStore
) {

    suspend fun createReview(request: CreateReviewRequest): Result<ReviewResponse> {
        return try {
            val response = apiService.createReview(request)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReview(reviewId: String, request: UpdateReviewRequest): Result<ReviewResponse> {
        return try {
            val response = apiService.updateReview(reviewId, request)
            handleSingleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyReviews(
        page: Int = 0,
        size: Int = 20,
        sort: String = "createdAt,desc"
    ): Result<PageResponse<ReviewResponse>> {
        return try {
            val response = apiService.getMyReviews(page, size, sort)
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewsByTarget(
        targetType: ReviewTargetType,
        targetId: String,
        page: Int = 0,
        size: Int = 20,
        sort: String = "createdAt,desc"
    ): Result<PageResponse<ReviewResponse>> {
        return try {
            val response = apiService.getReviewsByTarget(targetType, targetId, page, size, sort)
            handlePageResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewStats(
        targetType: ReviewTargetType,
        targetId: String
    ): Result<ReviewStatsResponse> {
        return try {
            val response = apiService.getReviewStats(targetType, targetId)
            handleSingleResponse(response)
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