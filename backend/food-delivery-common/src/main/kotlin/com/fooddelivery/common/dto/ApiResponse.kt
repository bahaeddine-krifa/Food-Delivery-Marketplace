package com.fooddelivery.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.OffsetDateTime
import java.util.UUID

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val meta: Meta = Meta(OffsetDateTime.now(), UUID.randomUUID().toString())
) {
    companion object {
        fun <T> ok(data: T, requestId: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                meta = Meta(OffsetDateTime.now(), requestId ?: UUID.randomUUID().toString())
            )
        }

        fun <T> created(data: T, requestId: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                meta = Meta(OffsetDateTime.now(), requestId ?: UUID.randomUUID().toString())
            )
        }
    }
}

data class ErrorDetail(
    val code: String,
    val message: String,
    val details: Map<String, Any>? = null
)

data class Meta(
    val timestamp: OffsetDateTime,
    val requestId: String,
    val path: String? = null
)

data class PageResponse<T>(
    val content: List<T>,
    val pageable: PageableInfo,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int,
    val empty: Boolean
)

data class PageableInfo(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: SortInfo,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)

data class SortInfo(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)