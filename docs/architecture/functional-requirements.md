# Functional Requirements

## Project: Food Delivery Platform

**Version:** 1.0  
**Date:** 2026-11-19  
**Status:** PHASE 0 - Proposed (Pending Approval)

---

## 1. Actor Definitions

| Actor | Description | Authentication |
|-------|-------------|----------------|
| **Customer** | End user ordering food | JWT (email/password, social login future) |
| **Delivery Person** | Courier delivering orders | JWT (email/password, phone verification) |
| **Restaurant Owner** | Manages one or more restaurants | JWT (email/password, 2FA optional) |
| **Admin** | Platform administrator | JWT (email/password, 2FA required) |
| **Anonymous User** | Unauthenticated visitor | None (limited public endpoints) |

---

## 2. Functional Requirements by Module

### 2.1 Authentication & Authorization (AUTH)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| AUTH-01 | User registration with email, password, role selection | Must | All |
| AUTH-02 | Email verification via token link | Must | All |
| AUTH-03 | Login with email/password returning access + refresh tokens | Must | All |
| AUTH-04 | JWT access token (15 min) + refresh token (7 days) | Must | All |
| AUTH-05 | Token refresh endpoint | Must | All |
| AUTH-06 | Password reset via email token | Must | All |
| AUTH-07 | Password change (authenticated) | Must | All |
| AUTH-08 | Role-based access control on all endpoints | Must | All |
| AUTH-09 | Permission-based authorization (fine-grained) | Must | All |
| AUTH-10 | Logout (refresh token revocation) | Must | All |
| AUTH-11 | Account lockout after 5 failed attempts (15 min) | Should | All |
| AUTH-12 | Remember me (extend refresh token to 30 days) | Could | All |

### 2.2 User Management (USERS)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| USR-01 | Customer profile: name, phone, avatar, preferences | Must | Customer |
| USR-02 | Delivery profile: vehicle type, license, documents, availability | Must | Delivery |
| USR-03 | Restaurant owner profile: business info, tax ID, bank details | Must | Owner |
| USR-04 | Admin user management: list, search, filter, activate/deactivate | Must | Admin |
| USR-05 | Profile update (own profile) | Must | All |
| USR-06 | Avatar upload (image validation, resize) | Should | All |
| USR-07 | Notification preferences (push, email, SMS) | Should | All |
| USR-08 | Delete account (soft delete, data retention) | Could | All |
| USR-09 | Admin: role assignment, forced password reset | Must | Admin |

### 2.3 Restaurant Management (RESTAURANTS)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| RST-01 | Restaurant application submission (owner) | Must | Owner |
| RST-02 | Application review: approve/reject with reason (admin) | Must | Admin |
| RST-03 | Restaurant profile: name, description, cuisine, phone, address, hours, images | Must | Owner/Admin |
| RST-04 | Restaurant hours: weekly schedule with exceptions | Must | Owner |
| RST-05 | Restaurant status: DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, SUSPENDED, CLOSED | Must | Owner/Admin |
| RST-06 | Owner can only manage own restaurants | Must | Owner |
| RST-07 | Public restaurant listing with filters (cuisine, rating, distance, open now) | Must | Customer |
| RST-08 | Restaurant search by name, cuisine, location | Must | Customer |
| RST-09 | Restaurant details page (menu, hours, reviews, info) | Must | Customer |
| RST-10 | Admin: suspend/unsuspend restaurants | Must | Admin |
| RST-11 | Restaurant analytics: orders, revenue, rating (owner) | Should | Owner |
| RST-12 | Restaurant closure (temporary/permanent) | Could | Owner |

### 2.4 Category Management (CATEGORIES)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| CAT-01 | CRUD categories per restaurant | Must | Owner |
| CAT-02 | Category ordering/display priority | Must | Owner |
| CAT-03 | Category image upload | Should | Owner |
| CAT-04 | Categories visible only for approved restaurants | Must | Customer |
| CAT-05 | Soft delete (archive) categories with products | Must | Owner |

### 2.5 Product Management (PRODUCTS)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| PRD-01 | CRUD products per restaurant | Must | Owner |
| PRD-02 | Product: name, description, price, category, images, allergens, tags | Must | Owner |
| PRD-03 | Product variants (size, toppings) - future phase | Could | Owner |
| PRD-04 | Product availability toggle | Must | Owner |
| PRD-05 | Product sorting within category | Must | Owner |
| PRD-06 | Image upload (multiple, primary marker) | Must | Owner |
| PRD-07 | Price stored as NUMERIC(10,2) | Must | System |
| PRD-08 | Public menu browsing (approved restaurants only) | Must | Customer |
| PRD-09 | Product search within restaurant | Should | Customer |
| PRD-10 | Nutritional info, dietary tags (vegan, gluten-free) | Could | Owner |

### 2.6 Address Management (ADDRESSES)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| ADR-01 | Customer: multiple saved addresses (home, work, other) | Must | Customer |
| ADR-02 | Address: label, street, city, state, postal code, coordinates, instructions | Must | Customer |
| ADR-03 | Default address selection | Must | Customer |
| ADR-04 | Address validation (postal code, coordinates) | Should | Customer |
| ADR-05 | Restaurant address (managed in restaurant profile) | Must | Owner |
| ADR-06 | Delivery address captured at order time (snapshot) | Must | System |

### 2.7 Order Management (ORDERS)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| ORD-01 | Create order from cart (single restaurant) | Must | Customer |
| ORD-02 | Order items snapshot: product name, unit price, quantity, notes | Must | System |
| ORD-03 | Backend calculates subtotal, tax, delivery fee, total | Must | System |
| ORD-04 | Order status: PENDING → ACCEPTED/REJECTED → PREPARING → READY → ASSIGNED → PICKED_UP → DELIVERING → DELIVERED | Must | System |
| ORD-05 | Order cancellation rules (customer: before ACCEPTED; restaurant: before PREPARING; admin: anytime) | Must | All |
| ORD-06 | Order status history (audit trail) | Must | System |
| ORD-07 | Customer: view own orders, filter by status, pagination | Must | Customer |
| ORD-08 | Restaurant: view incoming orders, filter, accept/reject | Must | Owner |
| ORD-09 | Restaurant: update preparation status (PREPARING → READY) | Must | Owner |
| ORD-10 | Admin: view all orders, filter, assign delivery | Must | Admin |
| ORD-11 | Duplicate order prevention (idempotency key) | Must | System |
| ORD-12 | Order timeout: auto-cancel PENDING after 10 min | Should | System |
| ORD-13 | Order notes/special instructions | Should | Customer |
| ORD-14 | Estimated preparation time per restaurant | Could | Owner |
| ORD-15 | Reorder from history | Could | Customer |

### 2.8 Delivery Management (DELIVERIES)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| DLV-01 | Delivery person availability toggle (online/offline) | Must | Delivery |
| DLV-02 | Auto-assignment algorithm (distance, rating, workload) | Should | System |
| DLV-03 | Manual assignment by admin/restaurant | Must | Admin/Owner |
| DLV-04 | Delivery offer: accept/reject (timeout 60 sec) | Must | Delivery |
| DLV-05 | Delivery status: ASSIGNED → PICKED_UP → DELIVERING → DELIVERED | Must | Delivery |
| DLV-06 | Pickup confirmation (code/photo) | Must | Delivery |
| DLV-07 | Delivery confirmation (code/photo/signature) | Must | Delivery |
| DLV-08 | Delivery tracking: real-time location updates | Should | Customer/Delivery |
| DLV-09 | Delivery history with earnings | Must | Delivery |
| DLV-10 | Delivery person rating by customer | Should | Customer |
| DLV-11 | Restaurant rating of delivery person | Could | Owner |
| DLV-12 | Unassigned order queue for admin | Must | Admin |

### 2.9 Payment Management (PAYMENTS)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| PAY-01 | Cash on delivery (COD) only - initial phase | Must | System |
| PAY-02 | Payment record per order: amount, method, status, transaction ref | Must | System |
| PAY-03 | Payment status: PENDING → PAID/FAILED/REFUNDED | Must | System |
| PAY-04 | COD: payment collected at delivery, marked PAID on DELIVERED | Must | Delivery |
| PAY-05 | Refund initiation (admin/restaurant) | Should | Admin/Owner |
| PAY-06 | Payment gateway integration (Stripe, etc.) - future phase | Could | System |

### 2.10 Notification Management (NOTIFICATIONS)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| NOT-01 | In-app notification center | Must | All |
| NOT-02 | Push notifications (FCM) for mobile apps | Must | Customer/Delivery |
| NOT-03 | Email notifications (order confirmations, status changes) | Must | All |
| NOT-04 | SMS notifications (critical alerts) - optional | Could | All |
| NOT-05 | Notification types: ORDER_PLACED, ORDER_ACCEPTED, ORDER_REJECTED, ORDER_READY, DELIVERY_ASSIGNED, DELIVERY_PICKED_UP, DELIVERY_DELIVERED, PAYMENT_RECEIVED, REVIEW_RECEIVED, SYSTEM_ALERT | Must | System |
| NOT-06 | Mark as read, delete, bulk actions | Should | All |
| NOT-07 | Notification preferences per user | Should | All |

### 2.11 Review Management (REVIEWS)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| REV-01 | Customer reviews order after DELIVERED (1-5 stars + comment) | Must | Customer |
| REV-02 | Review targets: restaurant, delivery person | Must | Customer |
| REV-03 | One review per order per target | Must | System |
| REV-04 | Restaurant owner can respond to reviews | Should | Owner |
| REV-05 | Review visibility: public on restaurant/delivery profile | Must | Customer |
| REV-06 | Aggregate ratings (restaurant, delivery person) | Must | System |
| REV-07 | Admin can moderate/remove inappropriate reviews | Must | Admin |

### 2.12 Administration (ADMIN)

| ID | Requirement | Priority | Actor |
|----|-------------|----------|-------|
| ADM-01 | Dashboard: KPIs (orders, revenue, users, restaurants, deliveries) | Must | Admin |
| ADM-02 | User management: list, search, view, activate/deactivate, role change | Must | Admin |
| ADM-03 | Restaurant approval queue | Must | Admin |
| ADM-04 | Restaurant management: view, suspend, delete | Must | Admin |
| ADM-05 | Delivery person management: verify documents, activate/deactivate | Must | Admin |
| ADM-06 | Order monitoring: all orders, filter, manual intervention | Must | Admin |
| ADM-07 | Delivery assignment (manual override) | Must | Admin |
| ADM-08 | Global statistics: revenue, orders, growth, ratings | Must | Admin |
| ADM-09 | Platform settings: fees, delivery radius, tax rates | Should | Admin |
| ADM-10 | Audit logs for admin actions | Should | Admin |

---

## 3. Non-Functional Requirements

| Category | Requirement | Target |
|----------|-------------|--------|
| **Performance** | API response time (p95) | < 500 ms |
| **Performance** | Order creation latency | < 1000 ms |
| **Performance** | Concurrent users | 10,000+ |
| **Availability** | Uptime | 99.9% |
| **Scalability** | Horizontal scaling (stateless) | Supported |
| **Security** | OWASP Top 10 compliance | Must |
| **Security** | Data encryption at rest (PII) | Must |
| **Security** | TLS 1.3 in production | Must |
| **Security** | Rate limiting (auth endpoints) | 10 req/min |
| **Security** | Rate limiting (API endpoints) | 100 req/min |
| **Reliability** | Database backup (daily) | Must |
| **Reliability** | Point-in-time recovery | Should |
| **Maintainability** | Code coverage | > 80% |
| **Maintainability** | API documentation (OpenAPI) | 100% |
| **Usability** | Mobile app offline support (cache) | Should |
| **Usability** | Multi-language (i18n infrastructure) | Could |
| **Compliance** | GDPR data export/deletion | Should |
| **Compliance** | PCI DSS (payments future) | Could |

---

## 4. Business Rules Summary

| Rule | Description | Enforcement |
|------|-------------|-------------|
| BR-01 | One restaurant per order | Backend validation |
| BR-02 | Restaurant must be APPROVED to receive orders | Status check |
| BR-03 | Owner manages only own restaurants | Authorization |
| BR-04 | Restaurant manages only own products/orders | Authorization |
| BR-05 | Order minimum 1 item | Validation |
| BR-06 | Backend calculates all prices | Service layer |
| BR-07 | Product snapshots in order_items | Order creation |
| BR-08 | Customer cannot change order status | Authorization |
| BR-09 | Only available delivery persons assigned | Availability check |
| BR-10 | Delivery person owns their deliveries | Authorization |
| BR-11 | Pickup before delivery completion | State machine |
| BR-12 | COD only initially | Payment module |
| BR-13 | Separate payment record per order | Data model |
| BR-14 | Idempotency key for order creation | Controller |
| BR-15 | Transactions for order creation & state changes | @Transactional |
| BR-16 | Validation + authorization on every endpoint | Controller/Service |

---

## 5. Identified Ambiguities & Decisions Needed

| # | Ambiguity | Impact | Recommendation |
|---|-----------|--------|----------------|
| 1 | **Mobile platforms**: Android only? iOS native? Cross-platform (Flutter/React Native)? | High - affects PHASE 7 & 8 scope | Confirm with stakeholder |
| 2 | **Payment gateway**: Which provider for future online payments? Stripe, Adyen, local? | Medium - affects PAY module design | Defer to PHASE 9, design abstraction |
| 3 | **Real-time infrastructure**: Redis for WebSocket pub/sub? Managed service? | Medium - affects DELIVERY/NOTIFICATION scaling | Redis locally, confirm prod |
| 4 | **Image storage**: Local filesystem, MinIO, S3, Cloudinary? | Medium - affects all image uploads | S3-compatible (MinIO local) |
| 5 | **Geolocation**: Google Maps, Mapbox, OpenStreetMap? | Medium - affects address, delivery tracking | Abstract provider, decide later |
| 6 | **Multi-tenancy**: Single platform or white-label per brand? | Low - current scope is single platform | Single platform |
| 7 | **Languages**: i18n from start or English only? | Low - infrastructure cost | English only, i18n-ready code |
| 8 | **Restaurant onboarding**: Self-service or admin-assisted? | Medium - affects RST-01 flow | Self-service application |
| 9 | **Delivery zones**: Per-restaurant radius or platform-wide? | High - affects assignment algorithm | Per-restaurant configurable |
| 10 | **Commission model**: Fixed %, tiered, subscription? | High - affects PAY/ADMIN | Configurable % per restaurant |
| 11 | **Order timeout values**: 10 min PENDING? Other timeouts? | Medium - affects UX | Configurable, default 10 min |
| 12 | **Tip support**: Customer tips for delivery? | Low - future phase | Defer |
| 13 | **Loyalty/rewards**: Points, coupons, subscriptions? | Low - future phase | Defer |
| 14 | **Analytics/BI**: Built-in or external (Metabase, Superset)? | Low - PHASE 10 | Built-in basics, external later |
| 15 | **WebSocket vs SSE**: For real-time updates? | Medium - affects DELIVERY/NOTIFICATION | WebSocket (STOMP) |

---

## 6. Use Case Summary (for UML)

### Customer Use Cases
- UC-C-01: Browse restaurants
- UC-C-02: View restaurant menu
- UC-C-03: Manage cart
- UC-C-04: Place order
- UC-C-05: Track order
- UC-C-06: View order history
- UC-C-07: Manage addresses
- UC-C-08: Manage profile
- UC-C-09: Submit review
- UC-C-10: Receive notifications

### Delivery Person Use Cases
- UC-D-01: Toggle availability
- UC-D-02: View delivery offers
- UC-D-03: Accept/reject delivery
- UC-D-04: Navigate to pickup
- UC-D-05: Confirm pickup
- UC-D-06: Navigate to delivery
- UC-D-07: Confirm delivery
- UC-D-08: View delivery history
- UC-D-09: View earnings

### Restaurant Owner Use Cases
- UC-R-01: Submit restaurant application
- UC-R-02: Manage restaurant profile
- UC-R-03: Manage categories
- UC-R-04: Manage products
- UC-R-05: View incoming orders
- UC-R-06: Accept/reject order
- UC-R-07: Update preparation status
- UC-R-08: View sales analytics
- UC-R-09: Respond to reviews

### Admin Use Cases
- UC-A-01: View dashboard KPIs
- UC-A-02: Manage users
- UC-A-03: Approve/reject restaurants
- UC-A-04: Manage restaurants
- UC-A-05: Manage delivery persons
- UC-A-06: Monitor all orders
- UC-A-07: Assign deliveries manually
- UC-A-08: View global statistics
- UC-A-09: Configure platform settings

---

*This document serves as the functional specification. All implementation must trace back to these requirements.*