package com.fooddelivery.common.exception

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/test/business-exception")
    fun throwBusinessException(): String {
        throw BusinessException("Test error", "TEST_ERROR")
    }

    @GetMapping("/test/not-found")
    fun throwNotFound(): String {
        throw ResourceNotFoundException("User", "123")
    }
}