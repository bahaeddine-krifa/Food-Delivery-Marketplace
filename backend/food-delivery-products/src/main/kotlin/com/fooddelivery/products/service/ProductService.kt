package com.fooddelivery.products.service

import com.fooddelivery.categories.domain.Category
import com.fooddelivery.categories.repository.CategoryRepository
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ForbiddenException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.products.domain.Product
import com.fooddelivery.products.repository.ProductRepository
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.repository.RestaurantRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val restaurantRepository: RestaurantRepository
) {

    fun createProduct(ownerId: UUID, restaurantId: UUID, request: CreateProductRequest): Product {
        val restaurant = getOwnedRestaurant(ownerId, restaurantId)

        val category = request.categoryId?.let { categoryId ->
            categoryRepository.findById(categoryId)
                .orElseThrow { ResourceNotFoundException("Category", categoryId) }
        }

        if (category?.restaurant?.id != restaurantId) {
            throw BusinessException("Category does not belong to this restaurant", "CATEGORY_MISMATCH")
        }

        val maxOrder = productRepository.findByRestaurantId(restaurantId, org.springframework.data.domain.Pageable.unpaged())
            .content.maxByOrNull { it.displayOrder }?.displayOrder ?: -1

        val product = Product().apply {
            this.restaurant = restaurant
            this.category = category
            this.name = request.name
            this.description = request.description
            this.price = request.price
            this.imageUrl = request.imageUrl
            this.additionalImages = request.additionalImages
            this.isAvailable = true
            this.isVegetarian = request.isVegetarian ?: false
            this.isVegan = request.isVegan ?: false
            this.isGlutenFree = request.isGlutenFree ?: false
            this.allergens = request.allergens
            this.tags = request.tags
            this.preparationTimeMinutes = request.preparationTimeMinutes
            this.displayOrder = maxOrder + 1
        }

        return productRepository.save(product)
    }

    fun getProducts(
        restaurantId: UUID,
        categoryId: UUID?,
        availableOnly: Boolean,
        vegetarian: Boolean?,
        vegan: Boolean?,
        glutenFree: Boolean?,
        search: String?,
        pageable: Pageable
    ): Page<Product> {
        val restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow { ResourceNotFoundException("Restaurant", restaurantId) }

        return when {
            search != null && search.isNotBlank() -> productRepository.searchByName(restaurantId, search.toLowerCase(), pageable)
            categoryId != null -> productRepository.findAvailableByCategoryId(categoryId, pageable)
            vegetarian == true -> productRepository.findVegetarianByRestaurantId(restaurantId, pageable)
            vegan == true -> productRepository.findVeganByRestaurantId(restaurantId, pageable)
            glutenFree == true -> productRepository.findGlutenFreeByRestaurantId(restaurantId, pageable)
            availableOnly -> productRepository.findAvailableByRestaurantId(restaurantId, pageable)
            else -> productRepository.findByRestaurantId(restaurantId, pageable) // This needs a custom query
        }
    }

    fun getProduct(ownerId: UUID, restaurantId: UUID, productId: UUID): Product {
        val product = productRepository.findByRestaurantIdAndId(restaurantId, productId)
            ?: throw ResourceNotFoundException("Product", productId)

        if (product.restaurant?.owner?.id != ownerId) {
            throw ForbiddenException("You can only manage products for your own restaurants")
        }

        return product
    }

    fun getPublicProduct(restaurantId: UUID, productId: UUID): Product {
        val product = productRepository.findByRestaurantIdAndId(restaurantId, productId)
            ?: throw ResourceNotFoundException("Product", productId)

        if (product.restaurant?.status != com.fooddelivery.common.enums.RestaurantStatus.APPROVED) {
            throw ResourceNotFoundException("Product", productId)
        }

        if (!product.isAvailable) {
            throw BusinessException("Product is not available", "PRODUCT_UNAVAILABLE")
        }

        return product
    }

    fun updateProduct(ownerId: UUID, restaurantId: UUID, productId: UUID, request: UpdateProductRequest): Product {
        val product = getProduct(ownerId, restaurantId, productId)

        request.name?.let { product.name = it }
        request.description?.let { product.description = it }
        request.price?.let { product.price = it }
        request.imageUrl?.let { product.imageUrl = it }
        request.additionalImages?.let { product.additionalImages = it }
        request.isAvailable?.let { product.isAvailable = it }
        request.isVegetarian?.let { product.isVegetarian = it }
        request.isVegan?.let { product.isVegan = it }
        request.isGlutenFree?.let { product.isGlutenFree = it }
        request.allergens?.let { product.allergens = it }
        request.tags?.let { product.tags = it }
        request.preparationTimeMinutes?.let { product.preparationTimeMinutes = it }

        request.categoryId?.let { categoryId ->
            val category = categoryRepository.findById(categoryId)
                .orElseThrow { ResourceNotFoundException("Category", categoryId) }
            if (category.restaurant?.id != restaurantId) {
                throw BusinessException("Category does not belong to this restaurant", "CATEGORY_MISMATCH")
            }
            product.category = category
        }

        return productRepository.save(product)
    }

    fun deleteProduct(ownerId: UUID, restaurantId: UUID, productId: UUID) {
        val product = getProduct(ownerId, restaurantId, productId)
        // Soft delete - mark as unavailable
        product.isAvailable = false
        productRepository.save(product)
    }

    fun toggleAvailability(ownerId: UUID, restaurantId: UUID, productId: UUID): Product {
        val product = getProduct(ownerId, restaurantId, productId)
        product.isAvailable = !product.isAvailable
        return productRepository.save(product)
    }

    fun reorderProducts(ownerId: UUID, restaurantId: UUID, productIds: List<UUID>): List<Product> {
        val restaurant = getOwnedRestaurant(ownerId, restaurantId)
        val products = productRepository.findByRestaurantId(restaurantId, org.springframework.data.domain.Pageable.unpaged())
            .content.associateBy { it.id }

        return productIds.mapIndexed { index, id ->
            val product = products[id] ?: throw ResourceNotFoundException("Product", id)
            product.displayOrder = index
            productRepository.save(product)
        }
    }

    fun uploadImages(ownerId: UUID, restaurantId: UUID, productId: UUID, request: ImageUploadRequest): Product {
        val product = getProduct(ownerId, restaurantId, productId)

        request.primaryImageUrl?.let { product.imageUrl = it }
        request.additionalImageUrls?.let { product.additionalImages = it }

        return productRepository.save(product)
    }

    private fun getOwnedRestaurant(ownerId: UUID, restaurantId: UUID): Restaurant {
        val restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow { ResourceNotFoundException("Restaurant", restaurantId) }

        if (restaurant.owner?.id != ownerId) {
            throw ForbiddenException("You can only manage products for your own restaurants")
        }

        return restaurant
    }

    data class CreateProductRequest(
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 200) val name: String,
        val description: String? = null,
        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.0") val price: java.math.BigDecimal,
        val imageUrl: String? = null,
        val additionalImages: Array<String>? = null,
        val categoryId: UUID? = null,
        val isVegetarian: Boolean? = null,
        val isVegan: Boolean? = null,
        val isGlutenFree: Boolean? = null,
        val allergens: String? = null,
        val tags: Array<String>? = null,
        val preparationTimeMinutes: Int? = null
    )

    data class UpdateProductRequest(
        val name: String? = null,
        val description: String? = null,
        val price: java.math.BigDecimal? = null,
        val imageUrl: String? = null,
        val additionalImages: Array<String>? = null,
        val categoryId: UUID? = null,
        val isAvailable: Boolean? = null,
        val isVegetarian: Boolean? = null,
        val isVegan: Boolean? = null,
        val isGlutenFree: Boolean? = null,
        val allergens: String? = null,
        val tags: Array<String>? = null,
        val preparationTimeMinutes: Int? = null
    )

    data class ImageUploadRequest(
        val primaryImageUrl: String? = null,
        val additionalImageUrls: Array<String>? = null
    )
}