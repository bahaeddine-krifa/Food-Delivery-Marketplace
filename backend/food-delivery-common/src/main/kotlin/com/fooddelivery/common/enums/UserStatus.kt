package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class UserStatus(@JsonValue val value: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"),
    PENDING_VERIFICATION("PENDING_VERIFICATION"),
    DELETED("DELETED");

    @JsonCreator
    fun fromValue(value: String): UserStatus = values().first { it.value == value }
}