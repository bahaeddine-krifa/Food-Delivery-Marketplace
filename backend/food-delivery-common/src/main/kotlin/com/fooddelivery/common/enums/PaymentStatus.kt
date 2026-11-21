package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class PaymentStatus(@JsonValue val value: String) {
    PENDING("PENDING"),
    PAID("PAID"),
    FAILED("FAILED"),
    REFUNDED("REFUNDED"),
    PARTIALLY_REFUNDED("PARTIALLY_REFUNDED");

    @JsonCreator
    fun fromValue(value: String): PaymentStatus = values().first { it.value == value }
}