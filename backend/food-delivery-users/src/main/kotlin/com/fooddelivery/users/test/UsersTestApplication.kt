package com.fooddelivery.users.test

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["com.fooddelivery.users.domain"])
@EnableJpaRepositories(basePackages = ["com.fooddelivery.users.repository"])
class UsersTestApplication