package com.fooddelivery.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DayOfWeek(@JsonValue val value: String) {
    MONDAY("MONDAY"),
    TUESDAY("TUESDAY"),
    WEDNESDAY("WEDNESDAY"),
    THURSDAY("THURSDAY"),
    FRIDAY("FRIDAY"),
    SATURDAY("SATURDAY"),
    SUNDAY("SUNDAY");

    @JsonCreator
    fun fromValue(value: String): DayOfWeek = values().first { it.value == value }

    companion object {
        fun fromJava(javaDay: java.time.DayOfWeek): DayOfWeek = values().first { it.name == javaDay.name }
        fun toJava(day: DayOfWeek): java.time.DayOfWeek = java.time.DayOfWeek.valueOf(day.name)
    }
}