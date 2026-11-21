package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ModerationStatus(@JsonValue val value: String) {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    HIDDEN("HIDDEN");

    @JsonCreator
    fun fromValue(value: String): ModerationStatus = values().first { it.value == value }
}