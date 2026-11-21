package com.fooddelivery.categories.service

import com.fooddelivery.categories.domain.Category
import com.fooddelivery.categories.repository.CategoryRepository
import com.fooddelivery.common.exception.BusinessException
import com.fooddelivery.common.exception.ForbiddenException
import com.fooddelivery.common.exception.ResourceNotFoundException
import com.fooddelivery.restaurants.domain.Restaurant
import com.fooddelivery.restaurants.repository.RestaurantRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val restaurantRepository: RestaurantRepository
) {

    fun createCategory(ownerId: UUID, restaurantId: UUID, request: CreateCategoryRequest): Category {
        val restaurant = getOwnedRestaurant(ownerId, restaurantId)

        if (categoryRepository.existsByRestaurantIdAndName(restaurantId, request.name)) {
            throw BusinessException("Category with this name already exists", "DUPLICATE_NAME")
        }

        val maxOrder = categoryRepository.findByRestaurantId(restaurantId)
            .maxByOrNull { it.displayOrder }?.displayOrder ?: -1

        val category = Category().apply {
            this.restaurant = restaurant
            this.name = request.name
            this.description = request.description
            this.imageUrl = request.imageUrl
            this.displayOrder = maxOrder + 1
            this.isActive = true
        }

        return categoryRepository.save(category)
    }

    fun getCategories(restaurantId: UUID, pageable: Pageable): Page<Category> {
        val restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow { ResourceNotFoundException("Restaurant", restaurantId) }
        return categoryRepository.findActiveByRestaurantId(restaurantId, pageable)
    }

    fun getAllCategories(restaurantId: UUID): List<Category> {
        return categoryRepository.findByRestaurantId(restaurantId)
    }

    fun getCategory(ownerId: UUID, restaurantId: UUID, categoryId: UUID): Category {
        val restaurant = getOwnedRestaurant(ownerId, restaurantId)
        return categoryRepository.findByRestaurantIdAndId(restaurantId, categoryId)
            ?: throw ResourceNotFoundException("Category", categoryId)
    }

    fun updateCategory(ownerId: UUID, restaurantId: UUID, categoryId: UUID, request: UpdateCategoryRequest): Category {
        val category = getCategory(ownerId, restaurantId, categoryId)

        request.name?.let {
            if (categoryRepository.existsByRestaurantIdAndName(restaurantId, it) && it != category.name) {
                throw BusinessException("Category with this name already exists", "DUPLICATE_NAME")
            }
            category.name = it
        }
        request.description?.let { category.description = it }
        request.imageUrl?.let { category.imageUrl = it }
        request.isActive?.let { category.isActive = it }

        return categoryRepository.save(category)
    }

    fun deleteCategory(ownerId: UUID, restaurantId: UUID, categoryId: UUID) {
        val category = getCategory(ownerId, restaurantId, categoryId)
        // Soft delete - just mark as inactive
        category.isActive = false
        categoryRepository.save(category)
    }

    fun reorderCategories(ownerId: UUID, restaurantId: UUID, categoryIds: List<UUID>): List<Category> {
        val restaurant = getOwnedRestaurant(ownerId, restaurantId)
        val categories = categoryRepository.findByRestaurantId(restaurantId)
            .associateBy { it.id }

        return categoryIds.mapIndexed { index, id ->
            val category = categories[id] ?: throw ResourceNotFoundException("Category", id)
            category.displayOrder = index
            categoryRepository.save(category)
        }
    }

    fun uploadImage(ownerId: UUID, restaurantId: UUID, categoryId: UUID, imageUrl: String): Category {
        val category = getCategory(ownerId, restaurantId, categoryId)
        category.imageUrl = imageUrl
        return categoryRepository.save(category)
    }

    private fun getOwnedRestaurant(ownerId: UUID, restaurantId: UUID): Restaurant {
        val restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow { ResourceNotFoundException("Restaurant", restaurantId) }

        if (restaurant.owner?.id != ownerId) {
            throw ForbiddenException("You can only manage categories for your own restaurants")
        }

        return restaurant
    }

    data class CreateCategoryRequest(
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 100) val name: String,
        val description: String? = null,
        val imageUrl: String? = null
    )

    data class UpdateCategoryRequest(
        val name: String? = null,
        val description: String? = null,
        val imageUrl: String? = null,
        val isActive: Boolean? = null
    )
}