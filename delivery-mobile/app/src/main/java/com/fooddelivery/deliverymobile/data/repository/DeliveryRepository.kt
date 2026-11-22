package com.fooddelivery.deliverymobile.data.repository

import com.fooddelivery.deliverymobile.data.api.ApiResponse
import com.fooddelivery.deliverymobile.data.api.ApiService
import com.fooddelivery.deliverymobile.data.api.AssignDeliveryRequest
import com.fooddelivery.deliverymobile.data.api.DeliveryResponse
import com.fooddelivery.deliverymobile.data.api.DeliveryStatus
import com.fooddelivery.deliverymobile.data.api.DeliverySummaryResponse
import com.fooddelivery.deliverymobile.data.api.OrderSummaryForDelivery
import com.fooddelivery.deliverymobile.data.api.PageResponse
import com.fooddelivery.deliverymobile.data.api.UpdateDeliveryStatusRequest
import com.fooddelivery.deliverymobile.data.local.TokenProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import retrofit2.Response
import java.util.UUID

@ActivityRetainedScoped
class DeliveryRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenProvider: TokenProvider
) {

    private val authHeader: String
        get() {
            val token = tokenProvider.getAccessToken() ?: return ""
            return "Bearer $token"
        }

    suspend fun getMyDeliveries(page: Int = 0, size: Int = 20): Result<PageResponse<DeliverySummaryResponse>> {
        val response = apiService.getMyDeliveries(authHeader, page, size)
        return handlePageResponse(response)
    }

    suspend fun getAvailableDeliveries(
        page: Int = 0,
        size: Int = 20,
        latitude: Double? = null,
        longitude: Double? = null,
        radiusKm: Double? = null
    ): Result<PageResponse<DeliverySummaryResponse>> {
        val response = apiService.getAvailableDeliveries(authHeader, page, size, "createdAt,asc", latitude, longitude, radiusKm)
        return handlePageResponse(response)
    }

    suspend fun getDeliveryById(id: UUID): Result<DeliveryResponse> {
        val response = apiService.getDeliveryById(authHeader, id)
        return handleApiResponse(response)
    }

    suspend fun updateDeliveryStatus(id: UUID, request: UpdateDeliveryStatusRequest): Result<DeliveryResponse> {
        val response = apiService.updateDeliveryStatus(authHeader, id, request)
        return handleApiResponse(response)
    }

    suspend fun assignDelivery(id: UUID, deliveryPersonId: UUID): Result<DeliveryResponse> {
        val response = apiService.assignDelivery(authHeader, id, AssignDeliveryRequest(deliveryPersonId))
        return handleApiResponse(response)
    }

    suspend fun getOrderSummaryForDelivery(orderId: UUID): Result<OrderSummaryForDelivery> {
        val response = apiService.getOrderSummaryForDelivery(authHeader, orderId)
        return handleApiResponse(response)
    }

    // Convenience methods for status transitions with validation
    suspend fun acceptDelivery(id: UUID): Result<DeliveryResponse> {
        return updateDeliveryStatus(id, UpdateDeliveryStatusRequest(DeliveryStatus.ACCEPTED))
    }

    suspend fun pickUpDelivery(
        id: UUID,
        pickupLatitude: Double,
        pickupLongitude: Double,
        pickupPhotoUrl: String? = null
    ): Result<DeliveryResponse> {
        val request = UpdateDeliveryStatusRequest(
            status = DeliveryStatus.PICKED_UP,
            pickupLatitude = java.math.BigDecimal.valueOf(pickupLatitude),
            pickupLongitude = java.math.BigDecimal.valueOf(pickupLongitude),
            pickupPhotoUrl = pickupPhotoUrl
        )
        return updateDeliveryStatus(id, request)
    }

    suspend fun startDelivering(
        id: UUID,
        deliveryLatitude: Double,
        deliveryLongitude: Double
    ): Result<DeliveryResponse> {
        val request = UpdateDeliveryStatusRequest(
            status = DeliveryStatus.DELIVERING,
            deliveryLatitude = java.math.BigDecimal.valueOf(deliveryLatitude),
            deliveryLongitude = java.math.BigDecimal.valueOf(deliveryLongitude)
        )
        return updateDeliveryStatus(id, request)
    }

    suspend fun completeDelivery(
        id: UUID,
        deliveryLatitude: Double,
        deliveryLongitude: Double,
        deliveryPhotoUrl: String? = null,
        customerSignatureUrl: String? = null
    ): Result<DeliveryResponse> {
        val request = UpdateDeliveryStatusRequest(
            status = DeliveryStatus.DELIVERED,
            deliveryLatitude = java.math.BigDecimal.valueOf(deliveryLatitude),
            deliveryLongitude = java.math.BigDecimal.valueOf(deliveryLongitude),
            deliveryPhotoUrl = deliveryPhotoUrl,
            customerSignatureUrl = customerSignatureUrl
        )
        return updateDeliveryStatus(id, request)
    }

    suspend fun failDelivery(
        id: UUID,
        reason: String,
        pickupLatitude: Double? = null,
        pickupLongitude: Double? = null,
        deliveryLatitude: Double? = null,
        deliveryLongitude: Double? = null
    ): Result<DeliveryResponse> {
        val request = UpdateDeliveryStatusRequest(
            status = DeliveryStatus.FAILED,
            reason = reason,
            pickupLatitude = pickupLatitude?.let { java.math.BigDecimal.valueOf(it) },
            pickupLongitude = pickupLongitude?.let { java.math.BigDecimal.valueOf(it) },
            deliveryLatitude = deliveryLatitude?.let { java.math.BigDecimal.valueOf(it) },
            deliveryLongitude = deliveryLongitude?.let { java.math.BigDecimal.valueOf(it) }
        )
        return updateDeliveryStatus(id, request)
    }

    private suspend fun <T> handleApiResponse(response: Response<ApiResponse<T>>): Result<T> {
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { return Result.success(it) }
        }
        return Result.failure(Exception(response.body()?.message ?: response.message() ?: "Request failed"))
    }

    private suspend fun <T> handlePageResponse(response: Response<ApiResponse<PageResponse<T>>>): Result<PageResponse<T>> {
        return handleApiResponse(response)
    }
}