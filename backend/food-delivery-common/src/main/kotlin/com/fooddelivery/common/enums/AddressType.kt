package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AddressType(@JsonValue val value: String) {
    HOME("HOME"),
    WORK("WORK"),
    OTHER("OTHER"),
    RESTAURANT("RESTAURANT"),
    PICKUP("PICKUP"),
    DELIVERY("DELIVERY");

    @JsonCreator
    fun fromValue(value: String): AddressType = values().first { it.value == value }
}