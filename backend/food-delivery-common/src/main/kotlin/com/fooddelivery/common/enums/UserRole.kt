package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class UserRole {
    CUSTOMER,
    DELIVERY_PERSON,
    RESTAURANT_OWNER,
    ADMIN;

    companion object {
        @JsonCreator
        fun fromValue(value: String): UserRole = values().first { it.name == value }
    }
}