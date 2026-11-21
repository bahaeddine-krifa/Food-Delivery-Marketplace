package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DeliveryStatus(@JsonValue val value: String) {
    PENDING_ASSIGNMENT("PENDING_ASSIGNMENT"),
    ASSIGNED("ASSIGNED"),
    ACCEPTED("ACCEPTED"),
    PICKED_UP("PICKED_UP"),
    DELIVERING("DELIVERING"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED"),
    FAILED("FAILED");

    @JsonCreator
    fun fromValue(value: String): DeliveryStatus = values().first { it.value == value }
}