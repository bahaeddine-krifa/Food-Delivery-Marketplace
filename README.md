# Food Delivery Platform

A complete food delivery platform built with a **centralized Spring Boot 3.2.5 backend** and **four frontend applications** (Customer Mobile, Restaurant Web, Admin Dashboard, Delivery Mobile).

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot Backend (Port 8080)             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  14 Microservice Modules                                │   │
│  │  Auth | Users | Restaurants | Categories | Products    │   │
│  │  Addresses | Orders | Deliveries | Payments | Notifs  │   │
│  │  Reviews | Categories | Admin | Common | API          │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  PostgreSQL 16 + Redis 7 + MailHog                     │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
   │ Customer    │    │ Restaurant  │    │ Admin       │
   │ Mobile App  │    │ Web App     │    │ Dashboard   │
   │ (Android)   │    │ (React)     │    │ (React)     │
   └─────────────┘    └─────────────┘    └─────────────┘
          ▲                   ▲                   ▲
          └───────────────────┼───────────────────┘
                              ▼
                      ┌─────────────┐
                      │ Delivery    │
                      │ Mobile App  │
                      │ (Android)   │
                      └─────────────┘
```

## Tech Stack

### Backend
- **Spring Boot 3.2.5** with Kotlin 1.9.24
- **Java 21**, Gradle 8.5/8.7 with version catalogs
- **PostgreSQL 16** with Flyway migrations
- **Redis 7** for caching
- **JWT Authentication** (HS512) with role-based access
- **Spring Data JPA** with Hibernate
- **Spring Security** with stateless sessions
- **Spring Boot Actuator** for health/metrics
- **OpenAPI/Swagger** documentation

### Frontend Applications
| Application | Technology | Port |
|-------------|------------|------|
| Customer Mobile | Android (Kotlin, Jetpack Compose) | N/A |
| Restaurant Web | React 18 + TypeScript + Vite | 3001 |
| Admin Dashboard | React 18 + TypeScript + Vite | 3002 |
| Delivery Mobile | Android (Kotlin, Jetpack Compose) | N/A |

### Infrastructure
- **Docker Compose** for local development
- **Nginx** as reverse proxy
- **MailHog** for email testing

## User Roles

| Role | Description | Permissions |
|------|-------------|-------------|
| `ADMIN` | Platform administrator | Full access to all resources, approve/reject restaurants, manage deliveries |
| `RESTAURANT_OWNER` | Restaurant owner | Manage own restaurant, products, categories, orders, reviews |
| `CUSTOMER` | End customer | Browse restaurants, place orders, track deliveries, leave reviews |
| `DELIVERY_PERSON` | Delivery driver | Accept deliveries, pickup/deliver, update location |

## Order Lifecycle

```
PENDING → ACCEPTED → PREPARING → READY → ASSIGNED → PICKED_UP → DELIVERING → DELIVERED
                    ↓
                CANCELLED (at any point by customer/restaurant/admin)
```

## Delivery Lifecycle

```
PENDING_ASSIGNMENT → ASSIGNED → ACCEPTED → PICKED_UP → DELIVERING → DELIVERED
                                              ↓
                                            FAILED
```

## Quick Start

### Prerequisites
- Docker 24+ and Docker Compose 2+
- Java 21 (for local backend development)
- Node.js 18+ (for frontend development)
- Android Studio (for mobile apps)

### 1. Clone and Configure

```bash
git clone <repository-url>
cd food-delivery-platform
cp .env.example .env
# Edit .env with your values (especially JWT_SECRET)
```

### 2. Start Infrastructure

```bash
docker compose up -d postgres redis mailhog
```

### 3. Start Backend

```bash
cd backend
./gradlew :food-delivery-api:bootRun --no-daemon
# Or build and run JAR:
./gradlew :food-delivery-api:bootJar --no-daemon
java -jar food-delivery-api/build/libs/*.jar
```

Backend will be available at:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator Health**: http://localhost:8080/actuator/health

### 4. Start Frontend Applications

```bash
# Restaurant Web (Terminal 1)
cd restaurant-web
npm install
npm run dev

# Admin Dashboard (Terminal 2)
cd admin-dashboard
npm install
npm run dev
```

Frontend apps:
- **Restaurant Web**: http://localhost:3001
- **Admin Dashboard**: http://localhost:3002

### 5. Full Stack with Docker Compose

```bash
docker compose up --build -d
```

All services:
- **Backend**: http://localhost:8080
- **Nginx Proxy**: http://localhost:80 / https://localhost:443
- **MailHog UI**: http://localhost:8025

## Default Test Users

After first startup, Flyway seeds these users (password: `Password123!`):

| Email | Role | Status |
|-------|------|--------|
| admin@fooddelivery.test | ADMIN | ACTIVE |
| owner@fooddelivery.test | RESTAURANT_OWNER | ACTIVE |
| customer@fooddelivery.test | CUSTOMER | ACTIVE |
| delivery@fooddelivery.test | DELIVERY_PERSON | ACTIVE |

Seeded restaurant: **Maria Italian Kitchen** (APPROVED, 14 products, 6 categories)

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh access token
- `GET /api/v1/auth/me` - Get current user

### Restaurants (Public)
- `GET /api/v1/restaurants` - List approved restaurants
- `GET /api/v1/restaurants/open` - List currently open restaurants
- `GET /api/v1/restaurants/{id}/public` - Get restaurant details
- `GET /api/v1/restaurants/{id}/categories` - Get categories
- `GET /api/v1/restaurants/{id}/products` - Get products

### Restaurants (Owner)
- `POST /api/v1/restaurants` - Create draft restaurant
- `GET /api/v1/restaurants/mine` - List own restaurants
- `PATCH /api/v1/restaurants/{id}` - Update restaurant
- `POST /api/v1/restaurants/{id}/submit` - Submit for approval
- `GET/PUT /api/v1/restaurants/{id}/hours` - Manage opening hours

### Restaurants (Admin)
- `GET /api/v1/restaurants/admin/applications` - List applications
- `POST /api/v1/restaurants/admin/applications/{id}/approve` - Approve
- `POST /api/v1/restaurants/admin/applications/{id}/reject` - Reject

### Orders
- `POST /api/v1/orders` - Create order (CUSTOMER)
- `GET /api/v1/orders` - List orders (CUSTOMER/OWNER/ADMIN)
- `GET /api/v1/orders/{id}` - Get order details
- `PUT /api/v1/orders/{id}/status` - Update status (OWNER/DELIVERY/ADMIN)
- `POST /api/v1/orders/{id}/cancel` - Cancel order
- `GET /api/v1/orders/{id}/history` - Status history

### Deliveries
- `POST /api/v1/deliveries/{orderId}` - Create delivery (ADMIN)
- `POST /api/v1/deliveries/{id}/assign` - Assign delivery person (ADMIN)
- `PUT /api/v1/deliveries/{id}/status` - Update delivery status (DELIVERY/ADMIN)
- `GET /api/v1/deliveries/my-deliveries` - My deliveries (DELIVERY)
- `GET /api/v1/deliveries/available` - Available deliveries (DELIVERY/ADMIN)

### Reviews
- `POST /api/v1/reviews` - Create review (CUSTOMER)
- `GET /api/v1/reviews/target?targetType=...&targetId=...` - Get reviews
- `GET /api/v1/reviews/target/stats` - Review statistics
- `POST /api/v1/reviews/{id}/respond` - Respond to review (OWNER/DELIVERY)

## Database

Flyway migrations in `backend/food-delivery-common/src/main/resources/db/migration/`:
- `V1__initial_schema.sql` - Core schema (all tables, indexes, constraints)
- `V2__add_indexes.sql` - Performance indexes
- `V3__add_triggers_functions.sql` - Triggers for ratings, status history, slugs
- `V4__seed_data.sql` - Test users, restaurant, categories, products

All enum columns use `VARCHAR` with JPA `AttributeConverter` (not PostgreSQL enums).

## Security

- **JWT** with HS512 (256-bit minimum secret)
- **Stateless** sessions
- **Role-based** authorization via `@PreAuthorize`
- **CSRF disabled** for API endpoints
- **Password** hashing with BCrypt (cost 12)
- **Rate limiting** on auth endpoints

## Testing

### Backend Tests
```bash
cd backend
./gradlew test --no-daemon
```

### Frontend Tests
```bash
cd restaurant-web && npm run type-check && npm run lint
cd admin-dashboard && npm run type-check && npm run lint
```

## Development

### Backend Module Structure
```
backend/
├── food-delivery-api/          # Main Spring Boot application
├── food-delivery-common/       # Shared entities, enums, converters, config
├── food-delivery-auth/         # Authentication, JWT, security
├── food-delivery-users/        # User management, profiles, addresses
├── food-delivery-restaurants/  # Restaurant CRUD, applications, hours
├── food-delivery-categories/   # Category management
├── food-delivery-products/     # Product management
├── food-delivery-addresses/    # Address management
├── food-delivery-orders/       # Order lifecycle, items, history
├── food-delivery-deliveries/   # Delivery lifecycle, assignment
├── food-delivery-payments/     # Payment processing
├── food-delivery-notifications/# Notifications (push, email, in-app)
├── food-delivery-reviews/      # Reviews and ratings
├── food-delivery-admin/        # Admin operations
└── food-delivery-parent/       # Parent module (if used)
```

### Adding a New Module
1. Create module directory with `build.gradle.kts`
2. Add to `settings.gradle.kts` include list
3. Add dependency to `food-delivery-api/build.gradle.kts`
4. Run `./gradlew build`

## Production Deployment

### Environment Variables
Set these in production:
```bash
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<64-char-base64-secret>
DATABASE_URL=jdbc:postgresql://<host>:5432/food_delivery
DATABASE_USERNAME=<user>
DATABASE_PASSWORD=<secure-password>
REDIS_HOST=<redis-host>
MAIL_HOST=<smtp-host>
MAIL_PORT=587
MAIL_USERNAME=<user>
MAIL_PASSWORD=<password>
FRONTEND_*_URL=<production-urls>
```

### Docker Production
```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Database Migration
Flyway runs automatically on startup. For manual migration:
```bash
docker exec food-delivery-postgres flyway migrate
```

## Documentation

- [API Documentation](docs/api/)
- [Architecture](docs/architecture/)
- [Database Schema](docs/database/)
- [UML Diagrams](docs/uml/)
- [Development Progress](docs/development-progress.md)
- [Development Roadmap](docs/development-roadmap.md)

## Demo Workflow

See [DEMO_INSTRUCTIONS.md](DEMO_INSTRUCTIONS.md) for complete walkthrough.

## License

MIT License - see [LICENSE](backend/LICENSE) for details.

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request