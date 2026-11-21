package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class RestaurantStatus(@JsonValue val value: String) {
    DRAFT("DRAFT"),
    PENDING_APPROVAL("PENDING_APPROVAL"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    SUSPENDED("SUSPENDED"),
    CLOSED("CLOSED");

    @JsonCreator
    fun fromValue(value: String): RestaurantStatus = values().first { it.value == value }
}