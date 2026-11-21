package com.fooddelivery.clientmobile.data.api

import com.fooddelivery.clientmobile.data.api.ApiModels.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== AUTH ENDPOINTS ====================

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<ApiResponse<TokenResponse>>

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<ApiResponse<Unit>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<ApiResponse<Unit>>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserProfileResponse>>

    // ==================== RESTAURANT ENDPOINTS (PUBLIC) ====================

    @GET("restaurants")
    suspend fun listRestaurants(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("cuisine") cuisine: String? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<Restaurant>>>

    @GET("restaurants/open")
    suspend fun getOpenRestaurants(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<Restaurant>>>

    @GET("restaurants/{id}/public")
    suspend fun getPublicRestaurant(@Path("id") id: String): Response<ApiResponse<Restaurant>>

    @GET("restaurants/{id}/hours")
    suspend fun getRestaurantHours(@Path("id") id: String): Response<ApiResponse<List<RestaurantHours>>>

    // ==================== CATEGORY ENDPOINTS (PUBLIC) ====================

    @GET("restaurants/{restaurantId}/categories")
    suspend fun listCategories(
        @Path("restaurantId") restaurantId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "displayOrder,asc"
    ): Response<ApiResponse<PageResponse<Category>>>

    // ==================== PRODUCT ENDPOINTS (PUBLIC) ====================

    @GET("restaurants/{restaurantId}/products")
    suspend fun listProducts(
        @Path("restaurantId") restaurantId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("categoryId") categoryId: String? = null,
        @Query("availableOnly") availableOnly: Boolean = true,
        @Query("vegetarian") vegetarian: Boolean? = null,
        @Query("vegan") vegan: Boolean? = null,
        @Query("glutenFree") glutenFree: Boolean? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String = "displayOrder,asc"
    ): Response<ApiResponse<PageResponse<Product>>>

    @GET("restaurants/{restaurantId}/products/{id}")
    suspend fun getProduct(
        @Path("restaurantId") restaurantId: String,
        @Path("id") id: String
    ): Response<ApiResponse<Product>>

    // ==================== ORDER ENDPOINTS (CUSTOMER) ====================

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderResponse>>

    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: OrderStatus? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<OrderSummaryResponse>>>

    @GET("orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: String): Response<ApiResponse<OrderResponse>>

    @POST("orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: String,
        @Query("reason") reason: String? = null
    ): Response<ApiResponse<OrderResponse>>

    @GET("orders/{orderId}/history")
    suspend fun getOrderHistory(
        @Path("orderId") orderId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<OrderStatusHistoryResponse>>>

    // ==================== ADDRESS ENDPOINTS (CUSTOMER) ====================

    @POST("addresses")
    suspend fun createAddress(@Body request: CreateAddressRequest): Response<ApiResponse<AddressResponse>>

    @GET("addresses")
    suspend fun getAddresses(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<AddressResponse>>>

    @GET("addresses/default")
    suspend fun getDefaultAddress(): Response<ApiResponse<AddressResponse?>>

    @GET("addresses/{addressId}")
    suspend fun getAddress(@Path("addressId") addressId: String): Response<ApiResponse<AddressResponse>>

    @PUT("addresses/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: String,
        @Body request: UpdateAddressRequest
    ): Response<ApiResponse<AddressResponse>>

    @PUT("addresses/{addressId}/default")
    suspend fun setDefaultAddress(
        @Path("addressId") addressId: String,
        @Body request: SetDefaultAddressRequest
    ): Response<ApiResponse<AddressResponse>>

    @DELETE("addresses/{addressId}")
    suspend fun deleteAddress(@Path("addressId") addressId: String): Response<ApiResponse<Unit>>

    // ==================== REVIEW ENDPOINTS (CUSTOMER) ====================

    @POST("reviews")
    suspend fun createReview(@Body request: CreateReviewRequest): Response<ApiResponse<ReviewResponse>>

    @PUT("reviews/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: String,
        @Body request: UpdateReviewRequest
    ): Response<ApiResponse<ReviewResponse>>

    @GET("reviews/my-reviews")
    suspend fun getMyReviews(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<ReviewResponse>>>

    @GET("reviews/target")
    suspend fun getReviewsByTarget(
        @Query("targetType") targetType: ReviewTargetType,
        @Query("targetId") targetId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<ReviewResponse>>>

    @GET("reviews/target/stats")
    suspend fun getReviewStats(
        @Query("targetType") targetType: ReviewTargetType,
        @Query("targetId") targetId: String
    ): Response<ApiResponse<ReviewStatsResponse>>

    // ==================== DELIVERY ENDPOINTS (CUSTOMER) ====================

    // Note: Delivery tracking endpoints may need to be added based on backend
}