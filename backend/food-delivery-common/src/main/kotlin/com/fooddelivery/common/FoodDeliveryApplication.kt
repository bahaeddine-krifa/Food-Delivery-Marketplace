package com.fooddelivery.common

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.fooddelivery"])
@EnableAsync
@EnableScheduling
class FoodDeliveryApplication

fun main(args: Array<String>) {
    runApplication<FoodDeliveryApplication>(*args)
}