package com.fooddelivery.categories.controller

import com.fooddelivery.categories.domain.Category
import com.fooddelivery.categories.service.CategoryService
import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.dto.PageResponse
import com.fooddelivery.common.dto.PageableInfo
import com.fooddelivery.common.dto.SortInfo
import com.fooddelivery.common.security.CurrentUser
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/categories")
@Tag(name = "Categories", description = "Category management for restaurants")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category for the restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun createCategory(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @Valid @RequestBody request: CategoryService.CreateCategoryRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Category>> {
        val category = categoryService.createCategory(currentUser.id, restaurantId, request)
        return ResponseEntity.status(201).body(ApiResponse.created(category, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping
    @Operation(summary = "List categories", description = "Get paginated list of active categories for the restaurant")
    fun listCategories(
        @PathVariable restaurantId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "displayOrder,asc") sort: String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<PageResponse<Category>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("displayOrder")))
        val categories = categoryService.getCategories(restaurantId, pageable)
        return ResponseEntity.ok(ApiResponse.ok(toPageResponse(categories), httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/all")
    @Operation(summary = "List all categories", description = "Get all categories including inactive ones")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    fun listAllCategories(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<List<Category>>> {
        val categories = categoryService.getAllCategories(restaurantId)
        return ResponseEntity.ok(ApiResponse.ok(categories, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category", description = "Get category by ID")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    fun getCategory(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Category>> {
        val category = categoryService.getCategory(currentUser.id, restaurantId, id)
        return ResponseEntity.ok(ApiResponse.ok(category, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", description = "Update category details")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun updateCategory(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: CategoryService.UpdateCategoryRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Category>> {
        val category = categoryService.updateCategory(currentUser.id, restaurantId, id, request)
        return ResponseEntity.ok(ApiResponse.ok(category, httpRequest.getHeader("X-Request-ID")))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Soft delete category (mark as inactive)")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun deleteCategory(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        categoryService.deleteCategory(currentUser.id, restaurantId, id)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/reorder")
    @Operation(summary = "Reorder categories", description = "Update display order of categories")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun reorderCategories(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @Valid @RequestBody request: ReorderRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<List<Category>>> {
        val categories = categoryService.reorderCategories(currentUser.id, restaurantId, request.categoryIds)
        return ResponseEntity.ok(ApiResponse.ok(categories, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Upload category image", description = "Upload image for category")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun uploadImage(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: ImageUploadRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Category>> {
        val category = categoryService.uploadImage(currentUser.id, restaurantId, id, request.imageUrl)
        return ResponseEntity.ok(ApiResponse.ok(category, httpRequest.getHeader("X-Request-ID")))
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

    data class ReorderRequest(
        @jakarta.validation.constraints.NotEmpty val categoryIds: List<UUID>
    )

    data class ImageUploadRequest(
        @jakarta.validation.constraints.NotBlank val imageUrl: String
    )
}