package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION,
    DELETED;

    companion object {
        @JsonCreator
        fun fromValue(value: String): UserStatus = values().first { it.name == value }
    }
}