package com.fooddelivery.common.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindException
import com.fooddelivery.common.exception.UnauthorizedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.OffsetDateTime
import java.util.UUID

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.warn("Business exception: {} - {}", ex.errorCode, ex.message)
        return buildResponse(ex.httpStatus, ex.errorCode, ex.message ?: "Business error", ex.details, request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors
            .associate { it.field to (it.defaultMessage ?: "Invalid value") }
        logger.warn("Validation failed: {}", details)
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details, request)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors
            .associate { it.field to (it.defaultMessage ?: "Invalid value") }
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details, request)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val details = ex.constraintViolations.associate { it.propertyPath.toString() to it.message }
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details, request)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Malformed JSON request", emptyMap(), request)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "Required parameter '${ex.parameterName}' is missing", emptyMap(), request)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", "Parameter '${ex.name}' has invalid type", emptyMap(), request)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password", emptyMap(), request)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied", emptyMap(), request)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.message ?: "Unauthorized", emptyMap(), request)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error("Data integrity violation: ", ex)
        val message = when {
            ex.message?.contains("duplicate key") == true -> "Resource already exists"
            ex.message?.contains("foreign key") == true -> "Referenced resource does not exist"
            else -> "Data integrity violation"
        }
        return buildResponse(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", message, emptyMap(), request)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ", ex)
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", emptyMap(), request)
    }

    private fun buildResponse(
        status: HttpStatus,
        code: String,
        message: String,
        details: Map<String, Any>,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val requestId = request.getHeader("X-Request-ID") ?: UUID.randomUUID().toString()
        val error = ErrorResponse(
            success = false,
            error = ErrorDetail(
                code = code,
                message = message,
                details = if (details.isNotEmpty()) details else null
            ),
            meta = Meta(
                timestamp = OffsetDateTime.now(),
                requestId = requestId,
                path = request.requestURI
            )
        )
        return ResponseEntity.status(status).body(error)
    }
}

data class ErrorResponse(
    val success: Boolean,
    val error: ErrorDetail,
    val meta: Meta
)

data class ErrorDetail(
    val code: String,
    val message: String,
    val details: Map<String, Any>? = null
)

data class Meta(
    val timestamp: OffsetDateTime,
    val requestId: String,
    val path: String
)