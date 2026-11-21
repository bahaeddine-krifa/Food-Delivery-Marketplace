package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class NotificationPriority(@JsonValue val value: String) {
    LOW("LOW"),
    NORMAL("NORMAL"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL");

    @JsonCreator
    fun fromValue(value: String): NotificationPriority = values().first { it.value == value }
}