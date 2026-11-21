package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class PaymentMethod(@JsonValue val value: String) {
    CASH_ON_DELIVERY("CASH_ON_DELIVERY"),
    CARD("CARD"),
    WALLET("WALLET"),
    BANK_TRANSFER("BANK_TRANSFER");

    @JsonCreator
    fun fromValue(value: String): PaymentMethod = values().first { it.value == value }
}