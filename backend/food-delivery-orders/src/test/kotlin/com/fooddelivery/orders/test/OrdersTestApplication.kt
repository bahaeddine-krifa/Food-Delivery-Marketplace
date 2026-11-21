package com.fooddelivery.orders.test

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = [
    "com.fooddelivery.orders",
    "com.fooddelivery.users",
    "com.fooddelivery.restaurants",
    "com.fooddelivery.products",
    "com.fooddelivery.categories",
    "com.fooddelivery.addresses",
    "com.fooddelivery.payments",
    "com.fooddelivery.deliveries",
    "com.fooddelivery.auth",
    "com.fooddelivery.common"
])
@EntityScan(basePackages = [
    "com.fooddelivery.orders.domain",
    "com.fooddelivery.users.domain",
    "com.fooddelivery.restaurants.domain",
    "com.fooddelivery.products.domain",
    "com.fooddelivery.categories.domain",
    "com.fooddelivery.addresses.domain",
    "com.fooddelivery.payments.domain",
    "com.fooddelivery.deliveries.domain"
])
@EnableJpaRepositories(basePackages = [
    "com.fooddelivery.orders.repository",
    "com.fooddelivery.users.repository",
    "com.fooddelivery.restaurants.repository",
    "com.fooddelivery.products.repository",
    "com.fooddelivery.categories.repository",
    "com.fooddelivery.addresses.repository",
    "com.fooddelivery.payments.repository",
    "com.fooddelivery.deliveries.repository",
    "com.fooddelivery.auth.repository"
])
class OrdersTestApplication