# API Endpoint Inventory & Contract

## Project: Food Delivery Platform

**Version:** 1.0  
**Date:** 2026-11-19  
**Status:** PHASE 0 - Proposed (Pending Approval)

---

## 1. API Overview

| Aspect | Specification |
|--------|---------------|
| **Base URL** | `https://api.fooddelivery.example.com/api/v1` |
| **Protocol** | HTTPS only |
| **Authentication** | JWT Bearer token (access token) |
| **Content-Type** | `application/json` |
| **Versioning** | URL path (`/api/v1`), header `Accept-Version` for minor |
| **Rate Limiting** | Auth: 10/min, API: 100/min, WebSocket: 50 conn/user |
| **Pagination** | `page` (0-based), `size` (default 20, max 100), `sort` |
| **Filtering** | Query parameters per endpoint |
| **Error Format** | RFC 7807 Problem Details |

---

## 2. Authentication Endpoints (`/api/v1/auth`)

| Method | Endpoint | Description | Auth | Request | Response |
|--------|----------|-------------|------|---------|----------|
| POST | `/register` | Register new user | Public | `RegisterRequest` | `AuthResponse` |
| POST | `/login` | Login user | Public | `LoginRequest` | `AuthResponse` |
| POST | `/refresh` | Refresh access token | Public* | `RefreshRequest` | `AuthResponse` |
| POST | `/logout` | Revoke refresh token | User | `LogoutRequest` | `204 No Content` |
| GET | `/verify-email?token={token}` | Verify email | Public | - | `200 OK` |
| POST | `/forgot-password` | Request password reset | Public | `ForgotPasswordRequest` | `200 OK` |
| POST | `/reset-password` | Reset password with token | Public | `ResetPasswordRequest` | `200 OK` |
| POST | `/change-password` | Change password (authenticated) | User | `ChangePasswordRequest` | `200 OK` |
| GET | `/me` | Get current user profile | User | - | `UserProfileResponse` |

*Refresh endpoint uses refresh token in body, not Authorization header

### DTOs

```kotlin
// RegisterRequest
data class RegisterRequest(
    @Email @NotBlank val email: String,
    @Size(min = 8, max = 100) @NotBlank val password: String,
    @NotBlank val firstName: String,
    @NotBlank val lastName: String,
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") val phone: String?,
    @NotNull val role: UserRole, // CUSTOMER, DELIVERY_PERSON, RESTAURANT_OWNER
    val profileData: JsonObject? // Role-specific data
)

// LoginRequest
data class LoginRequest(
    @Email @NotBlank val email: String,
    @NotBlank val password: String,
    val rememberMe: Boolean = false
)

// AuthResponse
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long, // seconds
    val user: UserProfileResponse
)

// UserProfileResponse
data class UserProfileResponse(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val avatarUrl: String?,
    val role: UserRole,
    val status: UserStatus,
    val emailVerified: Boolean,
    val createdAt: OffsetDateTime
)
```

---

## 3. User Management Endpoints (`/api/v1/users`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `/me/profile` | Get own profile | User | All |
| PATCH | `/me/profile` | Update own profile | User | All |
| POST | `/me/avatar` | Upload avatar | User | All |
| DELETE | `/me/avatar` | Delete avatar | User | All |
| GET | `/me/addresses` | List own addresses | User | Customer, Owner |
| POST | `/me/addresses` | Create address | User | Customer, Owner |
| GET | `/me/addresses/{id}` | Get address | User | Customer, Owner |
| PATCH | `/me/addresses/{id}` | Update address | User | Customer, Owner |
| DELETE | `/me/addresses/{id}` | Delete address | User | Customer, Owner |
| PATCH | `/me/addresses/{id}/default` | Set default address | User | Customer |
| GET | `/me/preferences` | Get notification preferences | User | All |
| PATCH | `/me/preferences` | Update notification preferences | User | All |

### Admin User Management (`/api/v1/admin/users`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List users (paginated, filterable) | Admin | ADMIN |
| GET | `/{id}` | Get user details | Admin | ADMIN |
| PATCH | `/{id}/status` | Activate/deactivate user | Admin | ADMIN |
| PATCH | `/{id}/role` | Change user role | Admin | ADMIN |
| POST | `/{id}/reset-password` | Force password reset | Admin | ADMIN |
| GET | `/{id}/audit-log` | User audit log | Admin | ADMIN |

---

## 4. Restaurant Endpoints

### Public (`/api/v1/restaurants`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `` | List restaurants (paginated, filterable) | Public |
| GET | `/{id}` | Get restaurant details | Public |
| GET | `/{id}/menu` | Get restaurant menu (categories + products) | Public |
| GET | `/{id}/reviews` | Get restaurant reviews | Public |
| GET | `/search` | Search restaurants | Public |

### Owner (`/api/v1/restaurants`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| POST | `` | Create restaurant (draft) | Owner | RESTAURANT_OWNER |
| GET | `/mine` | List own restaurants | Owner | RESTAURANT_OWNER |
| GET | `/{id}` | Get own restaurant details | Owner | RESTAURANT_OWNER |
| PATCH | `/{id}` | Update restaurant profile | Owner | RESTAURANT_OWNER |
| POST | `/{id}/submit` | Submit for approval | Owner | RESTAURANT_OWNER |
| DELETE | `/{id}` | Delete draft restaurant | Owner | RESTAURANT_OWNER |
| GET | `/{id}/analytics` | Get restaurant analytics | Owner | RESTAURANT_OWNER |
| GET | `/{id}/hours` | Get opening hours | Owner | RESTAURANT_OWNER |
| PUT | `/{id}/hours` | Update opening hours (bulk) | Owner | RESTAURANT_OWNER |
| POST | `/{id}/images` | Upload restaurant image | Owner | RESTAURANT_OWNER |

### Admin (`/api/v1/admin/restaurants`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List all restaurants (paginated) | Admin | ADMIN |
| GET | `/{id}` | Get restaurant details | Admin | ADMIN |
| PATCH | `/{id}/status` | Approve/suspend/close | Admin | ADMIN |
| DELETE | `/{id}` | Delete restaurant | Admin | ADMIN |

### Restaurant Applications (`/api/v1/restaurant-applications`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| POST | `` | Submit application | Owner | RESTAURANT_OWNER |
| GET | `/mine` | Get own application status | Owner | RESTAURANT_OWNER |

### Admin Applications (`/api/v1/admin/restaurant-applications`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List applications (filter by status) | Admin | ADMIN |
| GET | `/{id}` | Get application details | Admin | ADMIN |
| POST | `/{id}/approve` | Approve application | Admin | ADMIN |
| POST | `/{id}/reject` | Reject application | Admin | ADMIN |

---

## 5. Category Endpoints (`/api/v1/restaurants/{restaurantId}/categories`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List categories | Public* | - |
| GET | `/{id}` | Get category | Public* | - |
| POST | `` | Create category | Owner | RESTAURANT_OWNER |
| PATCH | `/{id}` | Update category | Owner | RESTAURANT_OWNER |
| DELETE | `/{id}` | Delete category (archive) | Owner | RESTAURANT_OWNER |
| PATCH | `/reorder` | Reorder categories | Owner | RESTAURANT_OWNER |
| POST | `/{id}/image` | Upload category image | Owner | RESTAURANT_OWNER |

*Public only for APPROVED restaurants

---

## 6. Product Endpoints (`/api/v1/restaurants/{restaurantId}/products`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List products (paginated, filterable) | Public* | - |
| GET | `/{id}` | Get product details | Public* | - |
| POST | `` | Create product | Owner | RESTAURANT_OWNER |
| PATCH | `/{id}` | Update product | Owner | RESTAURANT_OWNER |
| DELETE | `/{id}` | Delete product (archive) | Owner | RESTAURANT_OWNER |
| PATCH | `/{id}/availability` | Toggle availability | Owner | RESTAURANT_OWNER |
| PATCH | `/reorder` | Reorder products | Owner | RESTAURANT_OWNER |
| POST | `/{id}/images` | Upload product images | Owner | RESTAURANT_OWNER |
| DELETE | `/{id}/images/{imageId}` | Delete product image | Owner | RESTAURANT_OWNER |

*Public only for APPROVED restaurants

---

## 7. Order Endpoints

### Customer (`/api/v1/orders`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| POST | `` | Create order | Customer | CUSTOMER |
| GET | `` | List own orders (paginated, filterable) | Customer | CUSTOMER |
| GET | `/{id}` | Get order details | Customer | CUSTOMER |
| GET | `/{id}/tracking` | Get order tracking info | Customer | CUSTOMER |
| POST | `/{id}/cancel` | Cancel order | Customer | CUSTOMER |
| POST | `/{id}/reorder` | Reorder from history | Customer | CUSTOMER |

### Restaurant (`/api/v1/restaurants/{restaurantId}/orders`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List restaurant orders (paginated, filterable) | Owner | RESTAURANT_OWNER |
| GET | `/{id}` | Get order details | Owner | RESTAURANT_OWNER |
| POST | `/{id}/accept` | Accept order | Owner | RESTAURANT_OWNER |
| POST | `/{id}/reject` | Reject order | Owner | RESTAURANT_OWNER |
| PATCH | `/{id}/status` | Update preparation status | Owner | RESTAURANT_OWNER |

### Admin (`/api/v1/admin/orders`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List all orders (paginated, filterable) | Admin | ADMIN |
| GET | `/{id}` | Get order details | Admin | ADMIN |
| PATCH | `/{id}/status` | Force status change | Admin | ADMIN |
| POST | `/{id}/assign-delivery` | Manually assign delivery | Admin | ADMIN |

---

## 8. Delivery Endpoints

### Delivery Person (`/api/v1/deliveries`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `/offers` | List available delivery offers | Delivery | DELIVERY_PERSON |
| POST | `/offers/{offerId}/accept` | Accept delivery offer | Delivery | DELIVERY_PERSON |
| POST | `/offers/{offerId}/reject` | Reject delivery offer | Delivery | DELIVERY_PERSON |
| GET | `/mine` | List own deliveries (paginated, filterable) | Delivery | DELIVERY_PERSON |
| GET | `/{id}` | Get delivery details | Delivery | DELIVERY_PERSON |
| POST | `/{id}/pickup` | Confirm pickup | Delivery | DELIVERY_PERSON |
| POST | `/{id}/complete` | Confirm delivery | Delivery | DELIVERY_PERSON |
| PATCH | `/availability` | Toggle availability | Delivery | DELIVERY_PERSON |
| GET | `/earnings` | Get earnings summary | Delivery | DELIVERY_PERSON |
| PATCH | `/location` | Update current location | Delivery | DELIVERY_PERSON |

### Restaurant/Admin Delivery (`/api/v1/restaurants/{restaurantId}/deliveries` or `/api/v1/admin/deliveries`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `/unassigned` | List unassigned deliveries | Owner/Admin | RESTAURANT_OWNER, ADMIN |
| POST | `/{id}/assign` | Assign delivery person | Owner/Admin | RESTAURANT_OWNER, ADMIN |

---

## 9. Payment Endpoints

### Customer/Order (`/api/v1/orders/{orderId}/payment`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | Get payment details | Customer/Owner | CUSTOMER, RESTAURANT_OWNER |

### Admin (`/api/v1/admin/payments`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List payments (paginated) | Admin | ADMIN |
| GET | `/{id}` | Get payment details | Admin | ADMIN |
| POST | `/{id}/refund` | Initiate refund | Admin | ADMIN |

---

## 10. Notification Endpoints (`/api/v1/notifications`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List notifications (paginated, filterable) | User | All |
| GET | `/unread-count` | Get unread count | User | All |
| PATCH | `/{id}/read` | Mark as read | User | All |
| PATCH | `/read-all` | Mark all as read | User | All |
| DELETE | `/{id}` | Delete notification | User | All |

---

## 11. Review Endpoints

### Customer (`/api/v1/reviews`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| POST | `` | Create review | Customer | CUSTOMER |
| GET | `/mine` | List own reviews | Customer | CUSTOMER |

### Public (`/api/v1/restaurants/{restaurantId}/reviews`, `/api/v1/delivery-persons/{id}/reviews`, `/api/v1/products/{id}/reviews`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `` | List reviews (paginated) | Public |

### Restaurant Owner (`/api/v1/restaurants/{restaurantId}/reviews`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| POST | `/{id}/respond` | Respond to review | Owner | RESTAURANT_OWNER |

### Admin (`/api/v1/admin/reviews`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `` | List all reviews (moderation) | Admin | ADMIN |
| PATCH | `/{id}/moderate` | Moderate review | Admin | ADMIN |

---

## 12. Admin Dashboard Endpoints (`/api/v1/admin`)

| Method | Endpoint | Description | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `/dashboard/kpis` | Global KPIs | Admin | ADMIN |
| GET | `/dashboard/charts/revenue` | Revenue chart data | Admin | ADMIN |
| GET | `/dashboard/charts/orders` | Orders chart data | Admin | ADMIN |
| GET | `/dashboard/charts/users` | Users chart data | Admin | ADMIN |
| GET | `/dashboard/charts/ratings` | Ratings chart data | Admin | ADMIN |
| GET | `/delivery-persons` | List delivery persons | Admin | ADMIN |
| PATCH | `/delivery-persons/{id}/verify` | Verify delivery person | Admin | ADMIN |
| PATCH | `/delivery-persons/{id}/status` | Activate/deactivate | Admin | ADMIN |
| GET | `/settings` | Get platform settings | Admin | ADMIN |
| PATCH | `/settings` | Update platform settings | Admin | ADMIN |
| GET | `/audit-logs` | Audit logs (paginated) | Admin | ADMIN |

---

## 13. WebSocket Endpoints

### Connection
```
GET /ws?token={accessToken}
```

### STOMP Destinations

| Destination | Description | Publishers | Subscribers |
|-------------|-------------|------------|-------------|
| `/topic/orders/{orderId}` | Order status updates | Backend | Customer, Restaurant, Delivery |
| `/topic/deliveries/{deliveryId}` | Delivery status + location | Backend | Customer, Restaurant, Delivery |
| `/user/{userId}/notifications` | Personal notifications | Backend | Specific user |
| `/topic/restaurants/{restaurantId}/orders` | New orders for restaurant | Backend | Restaurant staff |

### Client Frames

```javascript
// Subscribe to order updates
stompClient.subscribe('/topic/orders/ORDER-123', (message) => {
    const event = JSON.parse(message.body);
    // event: { type: 'STATUS_CHANGED', data: { status: 'PREPARING', timestamp: '...' } }
});

// Subscribe to delivery location
stompClient.subscribe('/topic/deliveries/DEL-456', (message) => {
    const event = JSON.parse(message.body);
    // event: { type: 'LOCATION_UPDATE', data: { lat: 36.8, lng: 10.2, speed: 25 } }
});
```

---

## 14. Query Parameters Reference

### Pagination
```
page=0&size=20&sort=createdAt,desc
```

### Restaurant Filters
```
cuisine=italian&status=APPROVED&openNow=true&lat=36.8&lng=10.2&radius=5&minRating=4.0
```

### Order Filters
```
status=PENDING,ACCEPTED&fromDate=2026-01-01&toDate=2026-01-31&restaurantId=uuid
```

### Product Filters
```
categoryId=uuid&available=true&vegetarian=true&search=pizza
```

---

## 15. Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Missing or invalid token |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `CONFLICT` | 409 | Resource conflict (duplicate, state) |
| `IDEMPOTENCY_CONFLICT` | 409 | Duplicate idempotency key |
| `ORDER_STATE_INVALID` | 409 | Invalid order status transition |
| `INSUFFICIENT_INVENTORY` | 409 | Product unavailable |
| `PAYMENT_REQUIRED` | 402 | Payment needed |
| `RATE_LIMITED` | 429 | Too many requests |
| `INTERNAL_ERROR` | 500 | Server error |
| `SERVICE_UNAVAILABLE` | 503 | Temporary unavailable |

---

## 16. OpenAPI Specification

The complete OpenAPI 3.1 specification will be generated from Springdoc at:
- **JSON:** `/api/v1/api-docs`
- **YAML:** `/api/v1/api-docs.yaml`
- **Swagger UI:** `/swagger-ui.html`

### Key OpenAPI Annotations (Backend)

```kotlin
@Operation(summary = "Create order", description = "Creates a new order from cart")
@ApiResponses(
    ApiResponse(responseCode = "201", description = "Order created",
        content = @Content(schema = @Schema(implementation = OrderResponse::class))),
    ApiResponse(responseCode = "400", description = "Validation error",
        content = @Content(schema = @Schema(implementation = ProblemDetail::class))),
    ApiResponse(responseCode = "409", description = "Conflict - invalid state or duplicate",
        content = @Content(schema = @Schema(implementation = ProblemDetail::class))),
    ApiResponse(responseCode = "422", description = "Business rule violation",
        content = @Content(schema = @Schema(implementation = ProblemDetail::class)))
)
@PostMapping
fun createOrder(
    @Valid @RequestBody request: CreateOrderRequest,
    @RequestHeader("Idempotency-Key") idempotencyKey: String
): ResponseEntity<OrderResponse>
```

---

*This API contract is the single source of truth for all frontend-backend communication. Any changes must follow the API versioning policy and be communicated to all frontend teams.*