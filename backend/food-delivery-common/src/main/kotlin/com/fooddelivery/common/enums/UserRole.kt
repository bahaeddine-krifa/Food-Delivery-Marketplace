package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class UserRole(@JsonValue val value: String) {
    CUSTOMER("CUSTOMER"),
    DELIVERY_PERSON("DELIVERY_PERSON"),
    RESTAURANT_OWNER("RESTAURANT_OWNER"),
    ADMIN("ADMIN");

    companion object {
        @JsonCreator
        fun fromValue(value: String): UserRole = values().first { it.value == value }
    }
}