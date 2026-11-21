package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ReviewTargetType(@JsonValue val value: String) {
    RESTAURANT("RESTAURANT"),
    DELIVERY_PERSON("DELIVERY_PERSON"),
    PRODUCT("PRODUCT");

    @JsonCreator
    fun fromValue(value: String): ReviewTargetType = values().first { it.value == value }
}