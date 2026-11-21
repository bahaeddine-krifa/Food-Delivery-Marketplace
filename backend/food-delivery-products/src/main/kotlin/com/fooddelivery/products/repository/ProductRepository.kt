package com.fooddelivery.products.repository

import com.fooddelivery.products.domain.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductRepository : JpaRepository<Product, UUID> {

    fun findByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.restaurant.id = :restaurantId AND p.isAvailable = true ORDER BY p.displayOrder")
    fun findAvailableByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isAvailable = true ORDER BY p.displayOrder")
    fun findAvailableByCategoryId(categoryId: UUID, pageable: Pageable): Page<Product>

    fun findByRestaurantIdAndId(restaurantId: UUID, id: UUID): Product?

    fun countByRestaurantId(restaurantId: UUID): Long

    @Query("SELECT p FROM Product p WHERE p.restaurant.id = :restaurantId AND p.isAvailable = true AND LOWER(p.name) LIKE %:query%")
    fun searchByName(restaurantId: UUID, query: String, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.restaurant.id = :restaurantId AND p.isAvailable = true AND p.isVegetarian = true")
    fun findVegetarianByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.restaurant.id = :restaurantId AND p.isAvailable = true AND p.isVegan = true")
    fun findVeganByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.restaurant.id = :restaurantId AND p.isAvailable = true AND p.isGlutenFree = true")
    fun findGlutenFreeByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Product>
}