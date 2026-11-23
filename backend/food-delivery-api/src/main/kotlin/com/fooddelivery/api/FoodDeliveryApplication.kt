package com.fooddelivery.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    scanBasePackages = ["com.fooddelivery"],
    exclude = [UserDetailsServiceAutoConfiguration::class]
)
@EnableJpaRepositories("com.fooddelivery")
@EntityScan("com.fooddelivery")
@EnableAsync
@EnableScheduling
class FoodDeliveryApplication

fun main(args: Array<String>) {
    runApplication<FoodDeliveryApplication>(*args)
}