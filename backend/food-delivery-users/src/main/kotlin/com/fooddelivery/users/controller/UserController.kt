package com.fooddelivery.users.controller

import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.dto.PageResponse
import com.fooddelivery.users.domain.Address
import com.fooddelivery.users.domain.User
import com.fooddelivery.users.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User profile and address management")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me/profile")
    @Operation(summary = "Get own profile", description = "Get current user's profile information")
    @PreAuthorize("isAuthenticated()")
    fun getMyProfile(httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<User>> {
        val userId = getCurrentUserId(httpRequest)
        val user = userService.findById(userId)
        return ResponseEntity.ok(ApiResponse.ok(user, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/me/profile")
    @Operation(summary = "Update own profile", description = "Update current user's profile")
    @PreAuthorize("isAuthenticated()")
    fun updateMyProfile(
        @Valid @RequestBody request: UpdateProfileRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<User>> {
        val userId = getCurrentUserId(httpRequest)
        val user = userService.updateProfile(userId, request.firstName, request.lastName, request.phone, request.avatarUrl)
        return ResponseEntity.ok(ApiResponse.ok(user, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/me/addresses")
    @Operation(summary = "Create address", description = "Add a new address for current user")
    @PreAuthorize("isAuthenticated()")
    fun createAddress(
        @Valid @RequestBody address: Address,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Address>> {
        val userId = getCurrentUserId(httpRequest)
        val savedAddress = userService.addAddress(userId, address)
        return ResponseEntity.status(201).body(ApiResponse.created(savedAddress, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/me/addresses")
    @Operation(summary = "List addresses", description = "Get all addresses for current user")
    @PreAuthorize("isAuthenticated()")
    fun getAddresses(httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<List<Address>>> {
        val userId = getCurrentUserId(httpRequest)
        val addresses = userService.getAddresses(userId)
        return ResponseEntity.ok(ApiResponse.ok(addresses, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/me/addresses/default")
    @Operation(summary = "Get default address", description = "Get current user's default address")
    @PreAuthorize("isAuthenticated()")
    fun getDefaultAddress(httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Address?>> {
        val userId = getCurrentUserId(httpRequest)
        val address = userService.getDefaultAddress(userId)
        return ResponseEntity.ok(ApiResponse.ok(address, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/me/addresses/{id}")
    @Operation(summary = "Get address", description = "Get specific address by ID")
    @PreAuthorize("isAuthenticated()")
    fun getAddress(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Address>> {
        val userId = getCurrentUserId(httpRequest)
        val addresses = userService.getAddresses(userId)
        val address = addresses.find { it.id == id }
            ?: throw com.fooddelivery.common.exception.ResourceNotFoundException("Address", id)
        return ResponseEntity.ok(ApiResponse.ok(address, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/me/addresses/{id}")
    @Operation(summary = "Update address", description = "Update specific address")
    @PreAuthorize("isAuthenticated()")
    fun updateAddress(
        @PathVariable id: UUID,
        @Valid @RequestBody address: Address,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Address>> {
        val userId = getCurrentUserId(httpRequest)
        val updated = userService.updateAddress(userId, id, address)
        return ResponseEntity.ok(ApiResponse.ok(updated, httpRequest.getHeader("X-Request-ID")))
    }

    @DeleteMapping("/me/addresses/{id}")
    @Operation(summary = "Delete address", description = "Delete specific address")
    @PreAuthorize("isAuthenticated()")
    fun deleteAddress(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        val userId = getCurrentUserId(httpRequest)
        userService.deleteAddress(userId, id)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/me/addresses/{id}/default")
    @Operation(summary = "Set default address", description = "Set address as default")
    @PreAuthorize("isAuthenticated()")
    fun setDefaultAddress(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Address>> {
        val userId = getCurrentUserId(httpRequest)
        val address = userService.setDefaultAddress(userId, id)
        return ResponseEntity.ok(ApiResponse.ok(address, httpRequest.getHeader("X-Request-ID")))
    }

    // Admin endpoints
    @GetMapping
    @Operation(summary = "List users (Admin)", description = "List all users with pagination and filtering")
    @PreAuthorize("hasRole('ADMIN')")
    fun listUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt,desc") sort: String,
        @RequestParam(required = false) role: String?,
        @RequestParam(required = false) status: String?,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<PageResponse<User>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")))
        // TODO: Implement filtered pagination
        val usersPage: Page<User> = userService.findAll(pageable)
        val pageResponse = toPageResponse(usersPage)
        return ResponseEntity.ok(ApiResponse.ok(pageResponse, httpRequest.getHeader("X-Request-ID")))
    }

    private fun <T> toPageResponse(page: Page<T>): PageResponse<T> {
        val pageable = page.pageable
        val pageableInfo = com.fooddelivery.common.dto.PageableInfo(
            pageNumber = page.number,
            pageSize = page.size,
            sort = com.fooddelivery.common.dto.SortInfo(
                sorted = page.sort.isSorted,
                unsorted = page.sort.isUnsorted,
                empty = page.sort.isEmpty
            ),
            offset = pageable.offset,
            paged = pageable.isPaged,
            unpaged = pageable.isUnpaged
        )
        return com.fooddelivery.common.dto.PageResponse(
            content = page.content,
            pageable = pageableInfo,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.isFirst,
            last = page.isLast,
            numberOfElements = page.numberOfElements,
            empty = page.isEmpty
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user (Admin)", description = "Get user details by ID")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUser(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<User>> {
        val user = userService.findById(id)
        return ResponseEntity.ok(ApiResponse.ok(user, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status (Admin)", description = "Activate/deactivate/suspend user")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUserStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateStatusRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<User>> {
        val user = userService.updateStatus(id, request.status)
        return ResponseEntity.ok(ApiResponse.ok(user, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user role (Admin)", description = "Change user role")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUserRole(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateRoleRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<User>> {
        val user = userService.updateRole(id, request.role)
        return ResponseEntity.ok(ApiResponse.ok(user, httpRequest.getHeader("X-Request-ID")))
    }

    private fun getCurrentUserId(httpRequest: HttpServletRequest): UUID {
        val authHeader = httpRequest.getHeader("Authorization")
            ?: throw IllegalStateException("No authorization header")
        val token = authHeader.removePrefix("Bearer ").trim()
        // In a real implementation, extract from JWT or SecurityContext
        // For now, this is a placeholder - the actual implementation would use JwtService
        return UUID.fromString(httpRequest.getHeader("X-User-ID") ?: throw IllegalStateException("User ID not found"))
    }

    data class UpdateProfileRequest(
        val firstName: String? = null,
        val lastName: String? = null,
        val phone: String? = null,
        val avatarUrl: String? = null
    )

    data class UpdateStatusRequest(
        val status: com.fooddelivery.common.enums.UserStatus
    )

    data class UpdateRoleRequest(
        val role: com.fooddelivery.common.enums.UserRole
    )
}