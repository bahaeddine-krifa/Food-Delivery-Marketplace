package com.fooddelivery.products.domain

import com.fooddelivery.common.domain.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "products",
    indexes = [
        Index(name = "idx_products_restaurant", columnList = "restaurant_id,is_available,display_order"),
        Index(name = "idx_products_category", columnList = "category_id,is_available,display_order"),
        Index(name = "idx_products_search", columnList = "name")
    ]
)
class Product : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    var restaurant: com.fooddelivery.restaurants.domain.Restaurant? = null

    @ManyToOne
    @JoinColumn(name = "category_id")
    var category: com.fooddelivery.categories.domain.Category? = null

    @Column(nullable = false, length = 200)
    var name: String = ""

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal = BigDecimal.ZERO

    @Column(name = "image_url", length = 500)
    var imageUrl: String? = null

    @Column(name = "additional_images", columnDefinition = "TEXT[]")
    var additionalImages: Array<String>? = null

    @Column(name = "is_available", nullable = false)
    var isAvailable: Boolean = true

    @Column(name = "is_vegetarian", nullable = false)
    var isVegetarian: Boolean = false

    @Column(name = "is_vegan", nullable = false)
    var isVegan: Boolean = false

    @Column(name = "is_gluten_free", nullable = false)
    var isGlutenFree: Boolean = false

    @Column(name = "allergens", columnDefinition = "TEXT")
    var allergens: String? = null

    @Column(name = "tags", columnDefinition = "TEXT[]")
    var tags: Array<String>? = null

    @Column(name = "preparation_time_minutes")
    var preparationTimeMinutes: Int? = null

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0

    @Column(name = "average_rating", precision = 3, scale = 2, nullable = false)
    var averageRating: BigDecimal = BigDecimal.ZERO

    @Column(name = "total_reviews", nullable = false)
    var totalReviews: Int = 0
}