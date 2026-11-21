package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OrderStatus(@JsonValue val value: String) {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    PREPARING("PREPARING"),
    READY("READY"),
    ASSIGNED("ASSIGNED"),
    PICKED_UP("PICKED_UP"),
    DELIVERING("DELIVERING"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED");

    @JsonCreator
    fun fromValue(value: String): OrderStatus = values().first { it.value == value }

    fun canBeCancelledByCustomer(): Boolean = this in setOf(PENDING, ACCEPTED)
    fun canBeCancelledByRestaurant(): Boolean = this in setOf(PENDING, ACCEPTED, PREPARING)

    companion object {
        fun validTransitions(): Map<OrderStatus, Set<OrderStatus>> = mapOf(
            PENDING to setOf(ACCEPTED, REJECTED, CANCELLED),
            ACCEPTED to setOf(PREPARING, CANCELLED),
            REJECTED to emptySet(),
            PREPARING to setOf(READY, CANCELLED),
            READY to setOf(ASSIGNED, CANCELLED),
            ASSIGNED to setOf(PICKED_UP, CANCELLED),
            PICKED_UP to setOf(DELIVERING, CANCELLED),
            DELIVERING to setOf(DELIVERED, CANCELLED),
            DELIVERED to emptySet(),
            CANCELLED to emptySet()
        )

        fun canTransition(from: OrderStatus, to: OrderStatus): Boolean {
            return validTransitions()[from]?.contains(to) ?: false
        }
    }
}