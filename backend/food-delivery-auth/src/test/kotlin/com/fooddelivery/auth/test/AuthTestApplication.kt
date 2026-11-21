package com.fooddelivery.auth.test

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = [
    "com.fooddelivery.auth",
    "com.fooddelivery.users",
    "com.fooddelivery.common"
])
@EntityScan(basePackages = [
    "com.fooddelivery.auth.domain",
    "com.fooddelivery.users.domain"
])
@EnableJpaRepositories(basePackages = [
    "com.fooddelivery.auth.repository",
    "com.fooddelivery.users.repository"
])
class AuthTestApplication