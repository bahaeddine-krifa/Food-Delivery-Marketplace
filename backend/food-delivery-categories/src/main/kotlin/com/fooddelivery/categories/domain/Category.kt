package com.fooddelivery.categories.domain

import com.fooddelivery.common.domain.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(
    name = "categories",
    indexes = [Index(name = "idx_categories_restaurant", columnList = "restaurant_id,display_order")]
)
class Category : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    var restaurant: com.fooddelivery.restaurants.domain.Restaurant? = null

    @Column(nullable = false, length = 100)
    var name: String = ""

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @Column(name = "image_url", length = 500)
    var imageUrl: String? = null

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
}