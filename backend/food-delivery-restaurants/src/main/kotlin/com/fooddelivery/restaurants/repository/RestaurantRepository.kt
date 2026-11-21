package com.fooddelivery.restaurants.repository

import com.fooddelivery.common.enums.RestaurantStatus
import com.fooddelivery.restaurants.domain.Restaurant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantRepository : JpaRepository<Restaurant, UUID> {

    fun findByOwnerId(ownerId: UUID): List<Restaurant>

    fun findBySlug(slug: String): Restaurant?

    fun findByStatus(status: RestaurantStatus, pageable: Pageable): Page<Restaurant>

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND r.isOpenNow = true")
    fun findOpenRestaurants(pageable: Pageable): Page<Restaurant>

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND (:cuisine IS NULL OR r.cuisineType = :cuisine)")
    fun findByCuisine(cuisine: String?, pageable: Pageable): Page<Restaurant>

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND (:query IS NULL OR LOWER(r.name) LIKE %:query% OR LOWER(r.cuisineType) LIKE %:query%)")
    fun searchRestaurants(query: String?, pageable: Pageable): Page<Restaurant>

    fun countByOwnerId(ownerId: UUID): Long

    fun countByStatus(status: RestaurantStatus): Long

    override fun findAll(pageable: Pageable): Page<Restaurant>
}