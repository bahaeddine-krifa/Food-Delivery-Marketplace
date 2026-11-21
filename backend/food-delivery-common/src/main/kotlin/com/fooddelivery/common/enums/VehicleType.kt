package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class VehicleType(@JsonValue val value: String) {
    MOTORCYCLE("MOTORCYCLE"),
    BICYCLE("BICYCLE"),
    CAR("CAR"),
    SCOOTER("SCOOTER"),
    WALKING("WALKING");

    @JsonCreator
    fun fromValue(value: String): VehicleType = values().first { it.value == value }
}