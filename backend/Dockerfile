# Multi-stage Dockerfile for Food Delivery Backend
# Build stage
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

# Copy gradle files
COPY settings.gradle.kts build.gradle.kts gradle/libs.versions.toml ./
COPY gradle ./gradle

# Copy all module build files
COPY food-delivery-common/build.gradle.kts food-delivery-common/
COPY food-delivery-auth/build.gradle.kts food-delivery-auth/
COPY food-delivery-users/build.gradle.kts food-delivery-users/
COPY food-delivery-restaurants/build.gradle.kts food-delivery-restaurants/
COPY food-delivery-categories/build.gradle.kts food-delivery-categories/
COPY food-delivery-products/build.gradle.kts food-delivery-products/
COPY food-delivery-addresses/build.gradle.kts food-delivery-addresses/
COPY food-delivery-orders/build.gradle.kts food-delivery-orders/
COPY food-delivery-deliveries/build.gradle.kts food-delivery-deliveries/
COPY food-delivery-payments/build.gradle.kts food-delivery-payments/
COPY food-delivery-notifications/build.gradle.kts food-delivery-notifications/
COPY food-delivery-reviews/build.gradle.kts food-delivery-reviews/
COPY food-delivery-admin/build.gradle.kts food-delivery-admin/
COPY food-delivery-api/build.gradle.kts food-delivery-api/

# Download dependencies
RUN gradle dependencies --no-daemon

# Copy source code
COPY food-delivery-common/src ./food-delivery-common/src
COPY food-delivery-auth/src ./food-delivery-auth/src
COPY food-delivery-users/src ./food-delivery-users/src
COPY food-delivery-restaurants/src ./food-delivery-restaurants/src
COPY food-delivery-categories/src ./food-delivery-categories/src
COPY food-delivery-products/src ./food-delivery-products/src
COPY food-delivery-addresses/src ./food-delivery-addresses/src
COPY food-delivery-orders/src ./food-delivery-orders/src
COPY food-delivery-deliveries/src ./food-delivery-deliveries/src
COPY food-delivery-payments/src ./food-delivery-payments/src
COPY food-delivery-notifications/src ./food-delivery-notifications/src
COPY food-delivery-reviews/src ./food-delivery-reviews/src
COPY food-delivery-admin/src ./food-delivery-admin/src
COPY food-delivery-api/src ./food-delivery-api/src

# Build application
RUN gradle :food-delivery-api:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 -S appgroup && \
    adduser -u 1000 -S appuser -G appgroup

# Install curl for health checks
RUN apk add --no-cache curl

# Copy built jar
COPY --from=builder /app/food-delivery-api/build/libs/*.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app/uploads

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]