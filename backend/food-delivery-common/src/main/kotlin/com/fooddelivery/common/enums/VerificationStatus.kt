package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class VerificationStatus(@JsonValue val value: String) {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    EXPIRED("EXPIRED");

    @JsonCreator
    fun fromValue(value: String): VerificationStatus = values().first { it.value == value }
}