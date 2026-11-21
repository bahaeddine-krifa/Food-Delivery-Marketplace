package com.fooddelivery.clientmobile.presentation.restaurant

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.fooddelivery.clientmobile.data.repository.RestaurantRepository
import com.fooddelivery.clientmobile.data.repository.CategoryRepository
import com.fooddelivery.clientmobile.data.repository.ProductRepository
import com.fooddelivery.clientmobile.domain.model.Restaurant
import com.fooddelivery.clientmobile.domain.model.Category
import com.fooddelivery.clientmobile.domain.model.Product
import com.fooddelivery.clientmobile.domain.model.RestaurantHours
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ActivityRetainedScoped
class RestaurantDetailViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {

    private val _restaurant = MutableStateFlow<Restaurant?>(null)
    val restaurant = _restaurant.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _hours = MutableStateFlow<List<RestaurantHours>>(emptyList())
    val hours = _hours.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts = _isLoadingProducts.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Product Detail State
    sealed interface ProductDetailState {
        data class Loading : ProductDetailState
        data class Success(val product: Product) : ProductDetailState
        data class Error(val message: String) : ProductDetailState
    }

    private val _productDetailState = MutableStateFlow<ProductDetailState>(ProductDetailState.Loading)
    val productDetailState = _productDetailState.asStateFlow()

    private var currentProductPage = 0
    private var hasMoreProducts = true

    fun loadRestaurant(restaurantId: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val result = restaurantRepository.getRestaurant(restaurantId)
            when (result) {
                is Result.Success -> {
                    val apiRestaurant = result.getOrNull()
                    apiRestaurant?.let { _restaurant.value = mapToDomain(it) }
                    loadCategories(restaurantId)
                    loadHours(restaurantId)
                    loadProducts(restaurantId, refresh = true)
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load restaurant"
                }
            }
            _isLoading.value = false
        }
    }

    private fun loadCategories(restaurantId: String) {
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

    private fun loadHours(restaurantId: String) {
        viewModelScope.launch {
            val result = restaurantRepository.getRestaurantHours(restaurantId)
            when (result) {
                is Result.Success -> {
                    val apiHours = result.getOrNull()
                    apiHours?.let { hoursList ->
                        _hours.value = hoursList.map { mapToDomain(it) }
                    }
                }
                is Result.Failure -> {
                    // Hours are optional, don't show error
                }
            }
        }
    }

    fun loadProducts(restaurantId: String, refresh: Boolean = false) {
        if (refresh) {
            currentProductPage = 0
            hasMoreProducts = true
            _products.value = emptyList()
            _isLoadingProducts.value = true
        } else if (!hasMoreProducts || _isLoadingProducts.value) {
            return
        } else {
            _isLoadingProducts.value = true
        }

        _error.value = null
        viewModelScope.launch {
            val categoryFilter = _selectedCategoryId.value

            val result = productRepository.listProducts(
                restaurantId = restaurantId,
                page = currentProductPage,
                size = 20,
                categoryId = categoryFilter
            )

            when (result) {
                is Result.Success -> {
                    val pageResponse = result.getOrNull()
                    pageResponse?.let { response ->
                        val newProducts = response.content.map { mapToDomain(it) }
                        if (refresh || currentProductPage == 0) {
                            _products.value = newProducts
                        } else {
                            _products.value = _products.value + newProducts
                        }
                        currentProductPage = response.pageable.pageNumber + 1
                        hasMoreProducts = !response.last
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to load products"
                }
            }
            _isLoadingProducts.value = false
        }
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
        _restaurant.value?.let { restaurant ->
            loadProducts(restaurant.id.toString(), refresh = true)
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun loadProductDetail(productId: Long) {
        _productDetailState.value = ProductDetailState.Loading
        viewModelScope.launch {
            val restaurantId = _restaurant.value?.id.toString() ?: return@launch
            val result = productRepository.getProduct(restaurantId, productId.toString())
            when (result) {
                is Result.Success -> {
                    val apiProduct = result.getOrNull()
                    apiProduct?.let { _productDetailState.value = ProductDetailState.Success(mapToDomain(it)) }
                }
                is Result.Failure -> {
                    _productDetailState.value = ProductDetailState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to load product"
                    )
                }
            }
        }
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

    private fun mapToDomain(apiProduct: com.fooddelivery.clientmobile.data.api.ApiModels.Product): Product {
        return Product(
            id = java.util.UUID.fromString(apiProduct.id),
            restaurantId = java.util.UUID.fromString(apiProduct.restaurantId),
            categoryId = apiProduct.categoryId?.let { java.util.UUID.fromString(it) },
            name = apiProduct.name,
            description = apiProduct.description,
            price = apiProduct.price,
            imageUrl = apiProduct.imageUrl,
            additionalImages = apiProduct.additionalImages ?: emptyList(),
            isAvailable = apiProduct.isAvailable,
            isVegetarian = apiProduct.isVegetarian,
            isVegan = apiProduct.isVegan,
            isGlutenFree = apiProduct.isGlutenFree,
            allergens = apiProduct.allergens,
            tags = apiProduct.tags ?: emptyList(),
            preparationTimeMinutes = apiProduct.preparationTimeMinutes,
            displayOrder = apiProduct.displayOrder,
            averageRating = apiProduct.averageRating,
            totalReviews = apiProduct.totalReviews,
            createdAt = java.time.OffsetDateTime.parse(apiProduct.createdAt),
            updatedAt = java.time.OffsetDateTime.parse(apiProduct.updatedAt)
        )
    }

    private fun mapToDomain(apiHours: com.fooddelivery.clientmobile.data.api.ApiModels.RestaurantHours): RestaurantHours {
        return RestaurantHours(
            id = java.util.UUID.fromString(apiHours.id),
            dayOfWeek = com.fooddelivery.clientmobile.domain.model.DayOfWeek.valueOf(apiHours.dayOfWeek.value),
            openTime = apiHours.openTime,
            closeTime = apiHours.closeTime,
            isClosed = apiHours.isClosed
        )
    }
}