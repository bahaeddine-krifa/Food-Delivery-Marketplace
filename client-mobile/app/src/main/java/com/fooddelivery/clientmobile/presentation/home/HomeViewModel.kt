package com.fooddelivery.clientmobile.presentation.home

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.RestaurantRepository
import com.fooddelivery.clientmobile.data.repository.CategoryRepository
import com.fooddelivery.clientmobile.domain.model.Restaurant
import com.fooddelivery.clientmobile.domain.model.Category
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val categoryRepository: CategoryRepository
) {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants = _restaurants.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _openRestaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val openRestaurants = _openRestaurants.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentPage = 0
    private var hasMorePages = true
    private var currentRestaurantId: String? = null

    fun loadRestaurants(restaurantId: String? = null, refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
            hasMorePages = true
            _restaurants.value = emptyList()
            _isLoading.value = true
        } else if (!hasMorePages || _isLoadingMore.value) {
            return
        } else {
            _isLoadingMore.value = true
        }

        _error.value = null
        currentRestaurantId = restaurantId

        viewModelScope.launch {
            val categoryFilter = _selectedCategory.value
            val searchFilter = _searchQuery.value.ifBlank { null }

            val result = restaurantRepository.listRestaurants(
                page = currentPage,
                size = 20,
                cuisine = categoryFilter,
                search = searchFilter
            )

            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        val newRestaurants = response.content.map { mapToDomain(it) }
                        if (refresh || currentPage == 0) {
                            _restaurants.value = newRestaurants
                        } else {
                            _restaurants.value = _restaurants.value + newRestaurants
                        }
                        currentPage = response.pageable.pageNumber + 1
                        hasMorePages = !response.last
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load restaurants"
                }
            }
            _isLoading.value = false
            _isLoadingMore.value = false
        }
    }

    fun loadOpenRestaurants() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val result = restaurantRepository.getOpenRestaurants(page = 0, size = 10)
            _isLoading.value = false
            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        _openRestaurants.value = response.content.map { mapToDomain(it) }
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load open restaurants"
                }
            }
        }
    }

    fun loadCategories(restaurantId: String) {
        viewModelScope.launch {
            val result = categoryRepository.listCategories(restaurantId, page = 0, size = 50)
            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        _categories.value = response.content.map { mapToDomain(it) }
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load categories"
                }
            }
        }
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
        loadRestaurants(refresh = true)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadRestaurants(refresh = true)
    }

    fun clearError() {
        _error.value = null
    }

    private fun mapToDomain(apiRestaurant: com.fooddelivery.clientmobile.data.api.ApiModels.Restaurant): Restaurant {
        return Restaurant(
            id = java.util.UUID.fromString(apiRestaurant.id),
            name = apiRestaurant.name,
            slug = apiRestaurant.slug,
            description = apiRestaurant.description,
            phone = apiRestaurant.phone,
            email = apiRestaurant.email,
            logoUrl = apiRestaurant.logoUrl,
            coverImageUrl = apiRestaurant.coverImageUrl,
            cuisineType = apiRestaurant.cuisineType,
            status = com.fooddelivery.clientmobile.domain.model.RestaurantStatus.valueOf(apiRestaurant.status.value),
            addressId = java.util.UUID.fromString(apiRestaurant.addressId),
            deliveryRadiusKm = apiRestaurant.deliveryRadiusKm,
            minimumOrderAmount = apiRestaurant.minimumOrderAmount,
            deliveryFee = apiRestaurant.deliveryFee,
            preparationTimeMinutes = apiRestaurant.preparationTimeMinutes,
            averageRating = apiRestaurant.averageRating,
            totalReviews = apiRestaurant.totalReviews,
            isOpenNow = apiRestaurant.isOpenNow,
            rejectionReason = apiRestaurant.rejectionReason,
            approvedAt = apiRestaurant.approvedAt?.let { java.time.OffsetDateTime.parse(it) },
            approvedBy = apiRestaurant.approvedBy?.let { java.util.UUID.fromString(it) },
            createdAt = java.time.OffsetDateTime.parse(apiRestaurant.createdAt),
            updatedAt = java.time.OffsetDateTime.parse(apiRestaurant.updatedAt)
        )
    }

    private fun mapToDomain(apiCategory: com.fooddelivery.clientmobile.data.api.ApiModels.Category): Category {
        return Category(
            id = java.util.UUID.fromString(apiCategory.id),
            name = apiCategory.name,
            description = apiCategory.description,
            imageUrl = apiCategory.imageUrl,
            displayOrder = apiCategory.displayOrder,
            isActive = apiCategory.isActive,
            createdAt = java.time.OffsetDateTime.parse(apiCategory.createdAt),
            updatedAt = java.time.OffsetDateTime.parse(apiCategory.updatedAt)
        )
    }
}