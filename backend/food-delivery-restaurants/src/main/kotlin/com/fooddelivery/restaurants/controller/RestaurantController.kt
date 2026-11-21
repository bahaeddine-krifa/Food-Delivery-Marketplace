package com.fooddelivery.restaurants.controller

import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.dto.PageResponse
import com.fooddelivery.common.dto.PageableInfo
import com.fooddelivery.common.dto.SortInfo
import com.fooddelivery.common.enums.ApplicationStatus
import com.fooddelivery.common.enums.DayOfWeek
import com.fooddelivery.common.enums.RestaurantStatus
import com.fooddelivery.common.security.CurrentUser
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.domain.RestaurantApplication
import com.fooddelivery.restaurants.domain.RestaurantHours
import com.fooddelivery.restaurants.service.RestaurantService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurants", description = "Restaurant management and public listing")
class RestaurantController(
    private val restaurantService: RestaurantService
) {

    // Owner endpoints
    @PostMapping
    @Operation(summary = "Create restaurant draft", description = "Create a new restaurant in draft status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun createDraft(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @Valid @RequestBody request: CreateRestaurantRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Restaurant>> {
        val restaurant = restaurantService.createDraft(currentUser.id, request.name, request.phone, request.addressId)
        return ResponseEntity.status(201).body(ApiResponse.created(restaurant, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/mine")
    @Operation(summary = "List own restaurants", description = "Get all restaurants owned by current user")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun getMyRestaurants(
        @AuthenticationPrincipal currentUser: CurrentUser,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<List<Restaurant>>> {
        val restaurants = restaurantService.getMyRestaurants(currentUser.id)
        return ResponseEntity.ok(ApiResponse.ok(restaurants, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant details", description = "Get restaurant by ID (owner or admin)")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    fun getRestaurant(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Restaurant>> {
        val restaurant = restaurantService.getRestaurantById(id)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update restaurant", description = "Update restaurant details")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun updateRestaurant(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable id: UUID,
        @Valid @RequestBody request: RestaurantService.RestaurantUpdateRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Restaurant>> {
        val restaurant = restaurantService.updateRestaurant(currentUser.id, id, request)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit for approval", description = "Submit draft restaurant for admin approval")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun submitForApproval(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Restaurant>> {
        val restaurant = restaurantService.submitForApproval(currentUser.id, id)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, httpRequest.getHeader("X-Request-ID")))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant", description = "Delete draft or rejected restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun deleteRestaurant(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        restaurantService.deleteRestaurant(currentUser.id, id)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{id}/hours")
    @Operation(summary = "Get opening hours", description = "Get restaurant opening hours")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    fun getHours(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<List<RestaurantHours>>> {
        val hours = restaurantService.getHours(id)
        return ResponseEntity.ok(ApiResponse.ok(hours, httpRequest.getHeader("X-Request-ID")))
    }

    @PutMapping("/{id}/hours")
    @Operation(summary = "Update opening hours", description = "Update restaurant opening hours (bulk)")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun updateHours(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable id: UUID,
        @Valid @RequestBody hours: List<RestaurantService.RestaurantHoursRequest>,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<List<RestaurantHours>>> {
        val updatedHours = restaurantService.updateHours(currentUser.id, id, hours)
        return ResponseEntity.ok(ApiResponse.ok(updatedHours, httpRequest.getHeader("X-Request-ID")))
    }

    // Application endpoints
    @PostMapping("/applications")
    @Operation(summary = "Submit restaurant application", description = "Submit a new restaurant application for approval")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun submitApplication(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @Valid @RequestBody request: SubmitApplicationRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<RestaurantApplication>> {
        val application = restaurantService.submitApplication(
            currentUser.id,
            request.proposedName,
            request.proposedCuisine,
            request.addressId,
            request.businessLicenseUrl,
            request.taxIdDocumentUrl,
            request.ownerIdDocumentUrl
        )
        return ResponseEntity.status(201).body(ApiResponse.created(application, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/applications/mine")
    @Operation(summary = "Get my application", description = "Get current user's restaurant application status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun getMyApplication(
        @AuthenticationPrincipal currentUser: CurrentUser,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<RestaurantApplication?>> {
        val application = restaurantService.getMyApplication(currentUser.id)
        return ResponseEntity.ok(ApiResponse.ok(application, httpRequest.getHeader("X-Request-ID")))
    }

    // Public endpoints
    @GetMapping
    @Operation(summary = "List restaurants", description = "Get paginated list of approved restaurants")
    fun listRestaurants(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) cuisine: String?,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "createdAt,desc") sort: String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<PageResponse<Restaurant>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")))
        val restaurants = if (search != null && search.isNotBlank()) {
            restaurantService.searchRestaurants(search, pageable)
        } else {
            restaurantService.getPublicRestaurants(cuisine, pageable)
        }
        return ResponseEntity.ok(ApiResponse.ok(toPageResponse(restaurants), httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/open")
    @Operation(summary = "List open restaurants", description = "Get currently open restaurants")
    fun getOpenRestaurants(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt,desc") sort: String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<PageResponse<Restaurant>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")))
        val restaurants = restaurantService.getOpenRestaurants(pageable)
        return ResponseEntity.ok(ApiResponse.ok(toPageResponse(restaurants), httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{id}/public")
    @Operation(summary = "Get public restaurant details", description = "Get approved restaurant details for customers")
    fun getPublicRestaurant(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Restaurant>> {
        val restaurant = restaurantService.getPublicRestaurantById(id)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, httpRequest.getHeader("X-Request-ID")))
    }

    // Admin endpoints
    @GetMapping("/admin/applications")
    @Operation(summary = "List applications (Admin)", description = "List all restaurant applications with filtering")
    @PreAuthorize("hasRole('ADMIN')")
    fun listApplications(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) status: ApplicationStatus?,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<PageResponse<RestaurantApplication>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("submittedAt")))
        val applications = restaurantService.getApplications(status, pageable)
        return ResponseEntity.ok(ApiResponse.ok(toPageResponse(applications), httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/admin/applications/{id}")
    @Operation(summary = "Get application (Admin)", description = "Get application details")
    @PreAuthorize("hasRole('ADMIN')")
    fun getApplication(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<RestaurantApplication>> {
        val application = restaurantService.getApplicationById(id)
        return ResponseEntity.ok(ApiResponse.ok(application, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/admin/applications/{id}/approve")
    @Operation(summary = "Approve application (Admin)", description = "Approve restaurant application and create restaurant")
    @PreAuthorize("hasRole('ADMIN')")
    fun approveApplication(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Restaurant>> {
        val restaurant = restaurantService.approveApplication(id, currentUser.id)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/admin/applications/{id}/reject")
    @Operation(summary = "Reject application (Admin)", description = "Reject restaurant application with reason")
    @PreAuthorize("hasRole('ADMIN')")
    fun rejectApplication(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable id: UUID,
        @Valid @RequestBody request: RejectApplicationRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<RestaurantApplication>> {
        val application = restaurantService.rejectApplication(id, currentUser.id, request.reason)
        return ResponseEntity.ok(ApiResponse.ok(application, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/admin")
    @Operation(summary = "List all restaurants (Admin)", description = "List all restaurants including drafts")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminListRestaurants(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) status: RestaurantStatus?,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<PageResponse<Restaurant>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")))
        // TODO: Implement filtered list for admin
        val restaurants = restaurantService.getPublicRestaurants(null, pageable)
        return ResponseEntity.ok(ApiResponse.ok(toPageResponse(restaurants), httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/admin/{id}/status")
    @Operation(summary = "Update restaurant status (Admin)", description = "Update restaurant status")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminUpdateStatus(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateStatusRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Restaurant>> {
        val restaurant = restaurantService.adminUpdateStatus(id, request.status, currentUser.id)
        return ResponseEntity.ok(ApiResponse.ok(restaurant, httpRequest.getHeader("X-Request-ID")))
    }

    @DeleteMapping("/admin/{id}")
    @Operation(summary = "Delete restaurant (Admin)", description = "Delete any restaurant")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminDeleteRestaurant(@PathVariable id: UUID, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        restaurantService.adminDeleteRestaurant(id)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    private fun <T> toPageResponse(page: org.springframework.data.domain.Page<T>): PageResponse<T> {
        return PageResponse(
            content = page.content,
            pageable = PageableInfo(
                pageNumber = page.number,
                pageSize = page.size,
                sort = SortInfo(page.sort.isSorted, page.sort.isUnsorted, page.sort.isEmpty),
                offset = page.pageable.offset,
                paged = page.pageable.isPaged,
                unpaged = page.pageable.isUnpaged
            ),
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.isFirst,
            last = page.isLast,
            numberOfElements = page.numberOfElements,
            empty = page.isEmpty
        )
    }

    data class CreateRestaurantRequest(
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 200) val name: String,
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 20) val phone: String,
        val addressId: UUID
    )

    data class SubmitApplicationRequest(
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 200) val proposedName: String,
        @jakarta.validation.constraints.Size(max = 100) val proposedCuisine: String? = null,
        val addressId: UUID,
        @jakarta.validation.constraints.NotBlank val businessLicenseUrl: String,
        @jakarta.validation.constraints.NotBlank val taxIdDocumentUrl: String,
        @jakarta.validation.constraints.NotBlank val ownerIdDocumentUrl: String
    )

    data class RejectApplicationRequest(
        @jakarta.validation.constraints.NotBlank val reason: String
    )

    data class UpdateStatusRequest(
        val status: RestaurantStatus
    )
}