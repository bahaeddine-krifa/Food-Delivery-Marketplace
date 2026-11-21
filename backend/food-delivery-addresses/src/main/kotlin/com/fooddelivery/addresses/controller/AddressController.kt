package com.fooddelivery.addresses.controller

import com.fooddelivery.addresses.dto.AddressResponse
import com.fooddelivery.addresses.dto.CreateAddressRequest
import com.fooddelivery.addresses.dto.SetDefaultAddressRequest
import com.fooddelivery.addresses.dto.UpdateAddressRequest
import com.fooddelivery.addresses.service.AddressService
import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.security.CurrentUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Addresses", description = "Address management endpoints")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('DELIVERY_PERSON') or hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
class AddressController(
    private val addressService: AddressService
) {

    private fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication found")
        val principal = authentication.principal
        if (principal is CurrentUser) {
            return principal.id
        }
        throw IllegalStateException("Invalid authentication principal")
    }

    @PostMapping
    @Operation(summary = "Create a new address")
    fun createAddress(
        @Valid @RequestBody request: CreateAddressRequest
    ): ResponseEntity<ApiResponse<AddressResponse>> {
        val userId = getCurrentUserId()
        val address = addressService.createAddress(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(address, null))
    }

    @GetMapping
    @Operation(summary = "Get all addresses for the current user")
    fun getAddresses(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<AddressResponse>>> {
        val userId = getCurrentUserId()
        val addresses = addressService.getAddresses(userId, pageable)
        return ResponseEntity.ok(ApiResponse.ok(addresses, null))
    }

    @GetMapping("/default")
    @Operation(summary = "Get the default address for the current user")
    fun getDefaultAddress(): ResponseEntity<ApiResponse<AddressResponse?>> {
        val userId = getCurrentUserId()
        val address = addressService.getDefaultAddress(userId)
        return ResponseEntity.ok(ApiResponse.ok(address, null))
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get a specific address by ID")
    fun getAddress(
        @PathVariable addressId: UUID
    ): ResponseEntity<ApiResponse<AddressResponse>> {
        val userId = getCurrentUserId()
        val address = addressService.getAddressById(userId, addressId)
        return ResponseEntity.ok(ApiResponse.ok(address, null))
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update an address")
    fun updateAddress(
        @PathVariable addressId: UUID,
        @Valid @RequestBody request: UpdateAddressRequest
    ): ResponseEntity<ApiResponse<AddressResponse>> {
        val userId = getCurrentUserId()
        val address = addressService.updateAddress(userId, addressId, request)
        return ResponseEntity.ok(ApiResponse.ok(address, null))
    }

    @PutMapping("/{addressId}/default")
    @Operation(summary = "Set an address as default")
    fun setDefaultAddress(
        @PathVariable addressId: UUID,
        @Valid @RequestBody request: SetDefaultAddressRequest
    ): ResponseEntity<ApiResponse<AddressResponse>> {
        val userId = getCurrentUserId()
        val address = addressService.setDefaultAddress(userId, request)
        return ResponseEntity.ok(ApiResponse.ok(address, null))
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete an address")
    fun deleteAddress(
        @PathVariable addressId: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        val userId = getCurrentUserId()
        addressService.deleteAddress(userId, addressId)
        return ResponseEntity.ok(ApiResponse.ok(Unit, null))
    }
}