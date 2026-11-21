package com.fooddelivery.restaurants.domain

import com.fooddelivery.common.domain.AuditableEntity
import com.fooddelivery.common.enums.DayOfWeek
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalTime

@Entity
@Table(
    name = "restaurant_hours",
    uniqueConstraints = [UniqueConstraint(name = "uk_restaurant_hours_restaurant_day", columnNames = ["restaurant_id", "day_of_week"])],
    indexes = [Index(name = "idx_restaurant_hours_restaurant", columnList = "restaurant_id")]
)
class RestaurantHours : AuditableEntity() {

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    var restaurant: Restaurant? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    var dayOfWeek: DayOfWeek = DayOfWeek.MONDAY

    @Column(name = "open_time")
    var openTime: LocalTime? = null

    @Column(name = "close_time")
    var closeTime: LocalTime? = null

    @Column(name = "is_closed", nullable = false)
    var isClosed: Boolean = false
}