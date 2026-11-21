package com.fooddelivery.categories.repository

import com.fooddelivery.categories.domain.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CategoryRepository : JpaRepository<Category, UUID> {

    fun findByRestaurantId(restaurantId: UUID): List<Category>

    @Query("SELECT c FROM Category c WHERE c.restaurant.id = :restaurantId AND c.isActive = true ORDER BY c.displayOrder")
    fun findActiveByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Category>

    fun findByRestaurantIdAndId(restaurantId: UUID, id: UUID): Category?

    fun countByRestaurantId(restaurantId: UUID): Long

    fun existsByRestaurantIdAndName(restaurantId: UUID, name: String): Boolean
}