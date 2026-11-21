package com.fooddelivery.products.controller

import com.fooddelivery.common.dto.ApiResponse
import com.fooddelivery.common.dto.PageResponse
import com.fooddelivery.common.dto.PageableInfo
import com.fooddelivery.common.dto.SortInfo
import com.fooddelivery.common.security.CurrentUser
import com.fooddelivery.products.domain.Product
import com.fooddelivery.products.service.ProductService
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
@RequestMapping("/api/v1/restaurants/{restaurantId}/products")
@Tag(name = "Products", description = "Product management for restaurants")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product for the restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun createProduct(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @Valid @RequestBody request: ProductService.CreateProductRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Product>> {
        val product = productService.createProduct(currentUser.id, restaurantId, request)
        return ResponseEntity.status(201).body(ApiResponse.created(product, httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping
    @Operation(summary = "List products", description = "Get paginated list of products for the restaurant")
    fun listProducts(
        @PathVariable restaurantId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) categoryId: UUID?,
        @RequestParam(defaultValue = "true") availableOnly: Boolean,
        @RequestParam(required = false) vegetarian: Boolean?,
        @RequestParam(required = false) vegan: Boolean?,
        @RequestParam(required = false) glutenFree: Boolean?,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "displayOrder,asc") sort: String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<PageResponse<Product>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("displayOrder")))
        val products = productService.getProducts(
            restaurantId, categoryId, availableOnly, vegetarian, vegan, glutenFree, search, pageable
        )
        return ResponseEntity.ok(ApiResponse.ok(toPageResponse(products), httpRequest.getHeader("X-Request-ID")))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product", description = "Get product by ID")
    fun getProduct(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Product>> {
        val product = productService.getProduct(currentUser.id, restaurantId, id)
        return ResponseEntity.ok(ApiResponse.ok(product, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update product", description = "Update product details")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun updateProduct(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: ProductService.UpdateProductRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Product>> {
        val product = productService.updateProduct(currentUser.id, restaurantId, id, request)
        return ResponseEntity.ok(ApiResponse.ok(product, httpRequest.getHeader("X-Request-ID")))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Soft delete product (mark as unavailable)")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun deleteProduct(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        productService.deleteProduct(currentUser.id, restaurantId, id)
        return ResponseEntity.ok(ApiResponse.ok(Unit, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle product availability", description = "Toggle product available/unavailable status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun toggleAvailability(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Product>> {
        val product = productService.toggleAvailability(currentUser.id, restaurantId, id)
        return ResponseEntity.ok(ApiResponse.ok(product, httpRequest.getHeader("X-Request-ID")))
    }

    @PatchMapping("/reorder")
    @Operation(summary = "Reorder products", description = "Update display order of products")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun reorderProducts(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @Valid @RequestBody request: ReorderRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<List<Product>>> {
        val products = productService.reorderProducts(currentUser.id, restaurantId, request.productIds)
        return ResponseEntity.ok(ApiResponse.ok(products, httpRequest.getHeader("X-Request-ID")))
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Upload product images", description = "Upload images for product")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    fun uploadImages(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @PathVariable restaurantId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: ProductService.ImageUploadRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<Product>> {
        val product = productService.uploadImages(currentUser.id, restaurantId, id, request)
        return ResponseEntity.ok(ApiResponse.ok(product, httpRequest.getHeader("X-Request-ID")))
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
        @jakarta.validation.constraints.NotEmpty val productIds: List<UUID>
    )
}