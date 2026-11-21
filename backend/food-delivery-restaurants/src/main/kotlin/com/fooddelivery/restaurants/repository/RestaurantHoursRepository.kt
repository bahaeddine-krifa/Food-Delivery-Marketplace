package com.fooddelivery.restaurants.repository

import com.fooddelivery.common.enums.DayOfWeek
import com.fooddelivery.restaurants.domain.RestaurantHours
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantHoursRepository : JpaRepository<RestaurantHours, UUID> {

    fun findByRestaurantId(restaurantId: UUID): List<RestaurantHours>

    fun findByRestaurantIdAndDayOfWeek(restaurantId: UUID, dayOfWeek: DayOfWeek): RestaurantHours?

    fun deleteByRestaurantId(restaurantId: UUID)
}