package com.fooddelivery.deliverymobile.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface ApiService {

    // ==================== AUTH ENDPOINTS ====================

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<ApiResponse<TokenResponse>>

    @POST("/api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<ApiResponse<Unit>>

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @PUT("/api/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") authorization: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<Unit>>

    @GET("/api/auth/me")
    suspend fun getProfile(@Header("Authorization") authorization: String): Response<ApiResponse<UserProfileResponse>>

    // ==================== DELIVERY ENDPOINTS ====================

    @GET("/api/deliveries/my-deliveries")
    suspend fun getMyDeliveries(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<DeliverySummaryResponse>>>

    @GET("/api/deliveries/available")
    suspend fun getAvailableDeliveries(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,asc",
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radiusKm") radiusKm: Double? = null
    ): Response<ApiResponse<PageResponse<DeliverySummaryResponse>>>

    @GET("/api/deliveries/{id}")
    suspend fun getDeliveryById(
        @Header("Authorization") authorization: String,
        @Path("id") id: UUID
    ): Response<ApiResponse<DeliveryResponse>>

    @PUT("/api/deliveries/{id}/status")
    suspend fun updateDeliveryStatus(
        @Header("Authorization") authorization: String,
        @Path("id") id: UUID,
        @Body request: UpdateDeliveryStatusRequest
    ): Response<ApiResponse<DeliveryResponse>>

    @POST("/api/deliveries/{id}/assign")
    suspend fun assignDelivery(
        @Header("Authorization") authorization: String,
        @Path("id") id: UUID,
        @Body request: AssignDeliveryRequest
    ): Response<ApiResponse<DeliveryResponse>>

    // ==================== ORDER ENDPOINTS (minimal) ====================

    @GET("/api/orders/{id}/summary")
    suspend fun getOrderSummaryForDelivery(
        @Header("Authorization") authorization: String,
        @Path("id") id: UUID
    ): Response<ApiResponse<OrderSummaryForDelivery>>

    // ==================== ADDRESS ENDPOINTS (minimal) ====================

    @GET("/api/addresses/{id}")
    suspend fun getAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: UUID
    ): Response<ApiResponse<AddressResponse>>
}