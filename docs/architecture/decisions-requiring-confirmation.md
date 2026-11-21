# Key Decisions Requiring Confirmation

## Project: Food Delivery Platform

**Date:** 2026-11-19  
**Status:** PHASE 0 - Awaiting Stakeholder Decisions

---

## 1. Critical Decisions (Block PHASE 1 Start)

### 1.1 Mobile Platform Strategy
**Question:** Android only (Kotlin/Jetpack Compose) or cross-platform?

| Option | Pros | Cons | Recommendation |
|--------|------|------|----------------|
| **Native Android Only** (Current Spec) | Best performance, platform APIs, smaller team | No iOS support | ✅ If iOS not needed for MVP |
| **Kotlin Multiplatform (KMP)** | Shared business logic, native UI | Immature Compose Multiplatform, more complexity | Consider for PHASE 2 |
| **Flutter** | Single codebase, iOS + Android | Dart language, not Kotlin | Not aligned with stack |
| **React Native** | JavaScript/TypeScript, large ecosystem | Performance, native modules | Not aligned with stack |

**Decision Needed:** Confirm Android-only for MVP, or require iOS from start?

---

### 1.2 Payment Gateway (Future Phase)
**Question:** Which payment provider for online payments (PHASE 9+)?

| Provider | Regions | Fees | Integration |
|----------|---------|------|-------------|
| **Stripe** | Global | 2.9% + 30¢ | Excellent |
| **Adyen** | Global, strong EU | Custom | Enterprise |
| **PayPal/Braintree** | Global | 2.9% + 30¢ | Good |
| **Local (Tunisia)** | CMI, Paymee, etc. | Varies | Required for local |
| **Custom Abstraction** | Any | N/A | Build interface now |

**Decision Needed:** Design payment abstraction now, confirm provider later?

---

### 1.3 Image/File Storage
**Question:** Where to store uploaded images (restaurants, products, avatars, documents)?

| Option | Pros | Cons | Cost |
|--------|------|------|------|
| **Local Disk (Docker Volume)** | Free, simple, fast | Not scalable, no CDN | $0 |
| **MinIO (S3-compatible, self-hosted)** | S3 API, scalable, local dev | Operational overhead | Server cost |
| **AWS S3 / Cloudflare R2** | Managed, CDN, durable | Vendor lock-in, cost | Pay per use |
| **Cloudinary / Imgix** | Image optimization, transform | Cost at scale | Pay per use |

**Recommendation:** MinIO for development (S3-compatible), migrate to S3/R2 in production.

**Decision Needed:** Confirm storage strategy?

---

### 1.4 Geolocation/Map Provider
**Question:** Which map service for address selection, delivery tracking, restaurant discovery?

| Provider | Platform | Cost | Features |
|----------|----------|------|----------|
| **Google Maps Platform** | Web, Android, iOS | $200 free/mo then pay | Best coverage, Places API |
| **Mapbox** | Web, Mobile | 50k free/mo | Customizable, good SDK |
| **OpenStreetMap + Leaflet/MapLibre** | Web | Free | Open source, self-host tiles |
| **HERE Maps** | Web, Mobile | Freemium | Good for logistics |

**Recommendation:** Abstract behind interface, use Google Maps for mobile (Android SDK), Leaflet+OSM for web.

**Decision Needed:** Confirm provider or approve abstraction layer?

---

## 2. Important Decisions (Before PHASE 3-4)

### 2.1 Delivery Zone Configuration
**Question:** Per-restaurant delivery radius or platform-wide zones?

| Approach | Description |
|----------|-------------|
| **Per-Restaurant Radius** (Current Spec) | Each restaurant sets `delivery_radius_km`, `delivery_fee`, `minimum_order_amount` |
| **Platform Zones** | Admin defines zones (polygons), restaurants assigned to zones |
| **Hybrid** | Platform default zones, restaurant can override |

**Impact:** Affects order validation, delivery assignment algorithm, restaurant onboarding.

**Recommendation:** Per-restaurant (simpler, more flexible for marketplace).

---

### 2.2 Commission Model
**Question:** How does platform earn revenue?

| Model | Description |
|-------|-------------|
| **Fixed % per Order** (Current Spec) | `commission_rate` on `restaurant_owner_profiles` (default 15%) |
| **Tiered %** | Based on monthly volume (e.g., 15% → 12% → 10%) |
| **Subscription + Lower %** | Monthly fee + reduced commission |
| **Per-Order Fee + %** | Fixed fee + percentage |

**Recommendation:** Configurable % per restaurant (stored in `restaurant_owner_profiles.commission_rate`), tiered logic in future.

---

### 2.3 Order Timeout Values
**Question:** Auto-cancellation timeouts?

| Timeout | Current Default | Configurable? |
|---------|-----------------|---------------|
| PENDING → Auto-cancel | 10 minutes | Yes (platform setting) |
| Delivery Offer Accept | 60 seconds | Yes |
| Restaurant Accept/Reject | No timeout (manual) | Could add |
| PREPARING → READY | No timeout | Could add |

**Recommendation:** All timeouts as platform settings (admin configurable).

---

### 2.4 Multi-language (i18n)
**Question:** Support multiple languages from start?

| Approach | Effort | Recommendation |
|----------|--------|----------------|
| **English Only** | Low | ✅ MVP - add i18n infrastructure only |
| **English + Arabic (RTL)** | High | PHASE 2+ if Tunisia market |
| **Full i18n Framework** | Medium | Infrastructure in PHASE 1, translations later |

**Recommendation:** English only for MVP. Add `MessageSource` + `Accept-Language` header support in backend. Frontend: `react-i18next` / Compose `stringResource` ready but unused.

---

### 2.5 Restaurant Onboarding Flow
**Question:** Self-service or admin-assisted?

| Flow | Description |
|------|-------------|
| **Self-Service** (Current Spec) | Owner submits application → Admin reviews → Approve/Reject |
| **Admin-Assisted** | Admin creates restaurant, assigns owner |
| **Hybrid** | Self-service application, admin can also create directly |

**Recommendation:** Self-service application (scales better).

---

## 3. Technical Decisions (PHASE 1 Implementation)

### 3.1 Database Migration Tool
**Decision:** Flyway (confirmed in spec) ✅

### 3.2 API Documentation
**Decision:** Springdoc OpenAPI 3.1 + Swagger UI ✅

### 3.3 Caching
**Decision:** Redis (Spring Cache abstraction) ✅

### 3.4 Real-time
**Decision:** WebSocket (STOMP) + Redis broker + FCM push ✅

### 3.5 Testing
**Decision:** JUnit 5 + Mockito + Testcontainers (backend), Vitest + Playwright (web), JUnit + Compose Test (mobile) ✅

### 3.6 CI/CD
**Decision:** GitHub Actions (build, test, security scan, Docker build) ✅

### 3.7 Monitoring
**Decision:** Micrometer + Prometheus + Grafana (PHASE 9) ✅

---

## 4. Business Rule Clarifications

### 4.1 Order Cancellation Rules
| Actor | Can Cancel | Conditions |
|-------|------------|------------|
| **Customer** | Yes | Before ACCEPTED |
| **Restaurant** | Yes | Before PREPARING |
| **Delivery Person** | No | Can only reject offer |
| **Admin** | Yes | Anytime (with reason) |

**Confirmed in spec?** ✅ Yes (BR-05, functional requirements ORD-05)

### 4.2 Duplicate Order Prevention
**Mechanism:** Idempotency key (client-generated UUID) sent in `Idempotency-Key` header.

**Confirmed in spec?** ✅ Yes (BR-14, ORD-11)

### 4.3 Product Snapshots
**Rule:** `product_name_snapshot` and `unit_price_snapshot` stored in `order_items` at order creation.

**Confirmed in spec?** ✅ Yes (BR-07, ORD-02)

---

## 5. Decisions Summary Table

| # | Decision | Category | Blocking Phase | Status | Owner |
|---|----------|----------|----------------|--------|-------|
| 1 | Mobile: Android only vs cross-platform | Platform | PHASE 6 | ❌ PENDING | Stakeholder |
| 2 | Payment Gateway Provider | Integration | PHASE 9 | ⏳ DEFERRED | Stakeholder |
| 3 | Image Storage: MinIO vs S3 vs Local | Infrastructure | PHASE 1 | ❌ PENDING | Architect |
| 4 | Map Provider: Google vs Mapbox vs OSM | Integration | PHASE 4 | ❌ PENDING | Architect |
| 5 | Delivery Zones: Per-restaurant vs Platform | Business | PHASE 2 | ❌ PENDING | Stakeholder |
| 6 | Commission Model: Fixed % vs Tiered | Business | PHASE 2 | ❌ PENDING | Stakeholder |
| 7 | i18n: English only vs Multi-lang | Product | PHASE 1 | ❌ PENDING | Stakeholder |
| 8 | Restaurant Onboarding: Self-service | Business | PHASE 2 | ✅ CONFIRMED | Spec |
| 9 | Order Timeouts: Configurable values | Config | PHASE 3 | ❌ PENDING | Stakeholder |

---

## 6. Recommended Next Steps

1. **Schedule 30-min review meeting** with stakeholder
2. **Present this document** + architecture + functional requirements
3. **Resolve items 1, 3, 4, 5, 6, 7, 9** (7 decisions)
4. **Document decisions** in `docs/architecture/decision-log.md` (ADR format)
5. **Proceed to PHASE 1** with confirmed parameters

---

*All decisions should be recorded as Architecture Decision Records (ADRs) in `docs/architecture/adrs/` once confirmed.*