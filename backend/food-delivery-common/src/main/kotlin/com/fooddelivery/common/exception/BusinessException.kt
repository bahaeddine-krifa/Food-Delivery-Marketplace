package com.fooddelivery.common.exception

import org.springframework.http.HttpStatus

open class BusinessException(
    message: String,
    val errorCode: String,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    val details: Map<String, Any> = emptyMap()
) : RuntimeException(message)

class ValidationException(
    message: String = "Validation failed",
    details: Map<String, Any> = emptyMap()
) : BusinessException(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, details)

class ResourceNotFoundException(
    resource: String,
    identifier: Any
) : BusinessException(
    "$resource not found with identifier: $identifier",
    "NOT_FOUND",
    HttpStatus.NOT_FOUND,
    mapOf("resource" to resource, "identifier" to identifier)
)

class ConflictException(
    message: String,
    details: Map<String, Any> = emptyMap()
) : BusinessException(message, "CONFLICT", HttpStatus.CONFLICT, details)

class IdempotencyConflictException(
    idempotencyKey: String
) : BusinessException(
    "Request with idempotency key '$idempotencyKey' already processed",
    "IDEMPOTENCY_CONFLICT",
    HttpStatus.CONFLICT,
    mapOf("idempotencyKey" to idempotencyKey)
)

class UnauthorizedException(
    message: String = "Unauthorized"
) : BusinessException(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED)

class ForbiddenException(
    message: String = "Access denied"
) : BusinessException(message, "FORBIDDEN", HttpStatus.FORBIDDEN)

class InvalidStateTransitionException(
    from: String,
    to: String,
    resource: String = "Order"
) : BusinessException(
    "Invalid $resource state transition from '$from' to '$to'",
    "INVALID_STATE_TRANSITION",
    HttpStatus.CONFLICT,
    mapOf("from" to from, "to" to to, "resource" to resource)
)

class InsufficientInventoryException(
    productName: String,
    requested: Int,
    available: Int
) : BusinessException(
    "Insufficient inventory for '$productName': requested $requested, available $available",
    "INSUFFICIENT_INVENTORY",
    HttpStatus.CONFLICT,
    mapOf("productName" to productName, "requested" to requested, "available" to available)
)

class PaymentRequiredException(
    message: String = "Payment required"
) : BusinessException(message, "PAYMENT_REQUIRED", HttpStatus.PAYMENT_REQUIRED)

class RateLimitedException(
    message: String = "Too many requests"
) : BusinessException(message, "RATE_LIMITED", HttpStatus.TOO_MANY_REQUESTS)

class AccessDeniedException(
    message: String = "Access denied"
) : BusinessException(message, "FORBIDDEN", HttpStatus.FORBIDDEN)