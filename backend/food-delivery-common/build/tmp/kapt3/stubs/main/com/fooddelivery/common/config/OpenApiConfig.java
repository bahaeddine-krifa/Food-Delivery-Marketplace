package com.fooddelivery.common.config;

@org.springframework.context.annotation.Configuration()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0017J\b\u0010\u0005\u001a\u00020\u0004H\u0017J\b\u0010\u0006\u001a\u00020\u0007H\u0017J\b\u0010\b\u001a\u00020\u0004H\u0017J\b\u0010\t\u001a\u00020\u0004H\u0017\u00a8\u0006\n"}, d2 = {"Lcom/fooddelivery/common/config/OpenApiConfig;", "", "()V", "adminApi", "Lorg/springdoc/core/models/GroupedOpenApi;", "authApi", "openAPI", "Lio/swagger/v3/oas/models/OpenAPI;", "publicApi", "userApi", "food-delivery-common"})
public class OpenApiConfig {
    
    public OpenApiConfig() {
        super();
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public io.swagger.v3.oas.models.OpenAPI openAPI() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springdoc.core.models.GroupedOpenApi authApi() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springdoc.core.models.GroupedOpenApi userApi() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springdoc.core.models.GroupedOpenApi publicApi() {
        return null;
    }
    
    @org.springframework.context.annotation.Bean()
    @org.jetbrains.annotations.NotNull()
    public org.springdoc.core.models.GroupedOpenApi adminApi() {
        return null;
    }
}