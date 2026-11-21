package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ApplicationStatus(@JsonValue val value: String) {
    SUBMITTED("SUBMITTED"),
    UNDER_REVIEW("UNDER_REVIEW"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    @JsonCreator
    fun fromValue(value: String): ApplicationStatus = values().first { it.value == value }
}