package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class NotificationType(@JsonValue val value: String) {
    ORDER_PLACED("ORDER_PLACED"),
    ORDER_ACCEPTED("ORDER_ACCEPTED"),
    ORDER_REJECTED("ORDER_REJECTED"),
    ORDER_READY("ORDER_READY"),
    DELIVERY_ASSIGNED("DELIVERY_ASSIGNED"),
    DELIVERY_PICKED_UP("DELIVERY_PICKED_UP"),
    DELIVERY_DELIVERED("DELIVERY_DELIVERED"),
    PAYMENT_RECEIVED("PAYMENT_RECEIVED"),
    REVIEW_RECEIVED("REVIEW_RECEIVED"),
    SYSTEM_ALERT("SYSTEM_ALERT"),
    RESTAURANT_APPROVED("RESTAURANT_APPROVED"),
    RESTAURANT_REJECTED("RESTAURANT_REJECTED");

    @JsonCreator
    fun fromValue(value: String): NotificationType = values().first { it.value == value }
}