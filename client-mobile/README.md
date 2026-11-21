# Food Delivery Client Mobile App

Android client application for the Food Delivery Platform, built with Kotlin, Jetpack Compose, and Material 3.

## Tech Stack

- **Kotlin 1.9.22** with modern language features
- **Jetpack Compose** for declarative UI
- **Material 3** design system
- **Hilt** for dependency injection
- **Retrofit + OkHttp** for networking
- **Kotlinx Serialization** for JSON parsing
- **Coil** for image loading
- **Room** for local database (offline support)
- **DataStore** for preferences storage
- **Navigation Compose** for navigation
- **Coroutines + Flow** for async programming

## Architecture

Clean Architecture with three layers:

```
presentation/     # Compose UI, ViewModels, Navigation
domain/           # Business logic, Use cases, Repository interfaces, Domain models
data/             # Repository implementations, API services, Local data sources
```

## Features

### Authentication
- Email/password login and registration
- JWT token management with auto-refresh
- Remember me functionality
- Password reset flow
- Email verification

### Home & Discovery
- Restaurant listing with pagination
- Category filtering
- Search functionality
- Open now filter
- Horizontal/vertical restaurant cards

### Restaurant Details
- Restaurant info, hours, ratings
- Category-based product browsing
- Product cards with dietary tags
- Add to cart functionality

### Cart & Checkout
- Persistent cart state
- Quantity management
- Delivery address selection
- Order summary with pricing
- Cash on delivery payment
- Order placement

### Order Management
- Order history with status filtering
- Order detail with status timeline
- Order cancellation (when allowed)
- Status history tracking

### Profile & Addresses
- User profile display
- Address management (CRUD)
- Default address setting
- Password change
- Sign out

## Project Structure

```
app/
├── src/main/
│   ├── java/com/fooddelivery/clientmobile/
│   │   ├── data/
│   │   │   ├── api/           # Retrofit service, models, client
│   │   │   ├── local/         # DataStore, Room database
│   │   │   └── repository/    # Repository implementations
│   │   ├── di/                # Hilt modules
│   │   ├── domain/
│   │   │   └── model/         # Domain models
│   │   ├── presentation/
│   │   │   ├── auth/          # AuthViewModel
│   │   │   ├── home/          # HomeViewModel
│   │   │   ├── restaurant/    # RestaurantDetailViewModel
│   │   │   ├── cart/          # CartViewModel
│   │   │   ├── checkout/      # CheckoutViewModel
│   │   │   ├── orders/        # OrdersViewModel
│   │   │   ├── orderdetail/   # OrderDetailViewModel
│   │   │   ├── profile/       # ProfileViewModel
│   │   │   ├── address/       # AddressViewModel
│   │   │   ├── main/          # MainActivity
│   │   │   └── screens/       # Compose screens
│   │   ├── ui/
│   │   │   ├── theme/         # Material 3 theme
│   │   │   └── navigation/    # Navigation graph
│   │   └── FoodDeliveryApplication.kt
│   └── res/
└── build.gradle.kts
```

## Getting Started

### Prerequisites

- Android Studio Koala (2024.1.1) or later
- JDK 17
- Android SDK 34
- Backend API running on `http://localhost:8080`

### Configuration

1. Update `BuildConfig.API_BASE_URL` in `app/src/main/java/com/fooddelivery/clientmobile/BuildConfig.java`:
   ```java
   // For local development with emulator
   public static final String API_BASE_URL = "http://10.0.2.2:8080/api/v1/";
   
   // For physical device (replace with your machine's IP)
   public static final String API_BASE_URL = "http://192.168.x.x:8080/api/v1/";
   ```

2. Sync Gradle and build the project

### Running the App

```bash
# From the client-mobile directory
./gradlew installDebug
```

Or open in Android Studio and click Run.

## API Integration

The app integrates with the Spring Boot backend at `/api/v1`:

| Feature | Endpoints |
|---------|-----------|
| Auth | `/auth/login`, `/auth/register`, `/auth/refresh`, `/auth/me`, `/auth/forgot-password`, `/auth/reset-password`, `/auth/change-password`, `/auth/verify-email` |
| Restaurants | `/restaurants`, `/restaurants/open`, `/restaurants/{id}/public`, `/restaurants/{id}/hours` |
| Categories | `/restaurants/{id}/categories` |
| Products | `/restaurants/{id}/products`, `/restaurants/{id}/products/{id}` |
| Orders | `/orders`, `/orders/{id}`, `/orders/{id}/cancel`, `/orders/{id}/history` |
| Addresses | `/addresses`, `/addresses/default`, `/addresses/{id}`, `/addresses/{id}/default` |
| Reviews | `/reviews`, `/reviews/my-reviews`, `/reviews/target`, `/reviews/target/stats` |

## Key Implementation Details

### JWT Authentication
- Access tokens stored in DataStore (encrypted)
- Automatic token refresh on 401 responses
- Request queuing during refresh to prevent race conditions
- Secure logout with token revocation

### State Management
- ViewModels expose StateFlow for reactive UI
- Cart state persisted across navigation
- Optimistic UI updates for better UX

### Offline Support
- Room database for caching (future enhancement)
- DataStore for persistent preferences
- Graceful error handling for network failures

### Image Loading
- Coil with memory/disk caching
- Crossfade animations
- Placeholder/error states

### Navigation
- Type-safe navigation with sealed class routes
- Hilt ViewModel injection
- Proper back stack management

## Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Build Variants

- `debug` - Development build with logging
- `release` - Production build with minification

## Dependencies

Key versions (defined in `gradle.properties`):
- AGP: 8.4.0
- Kotlin: 1.9.22
- Compose BOM: 2024.05.00
- Hilt: 2.51
- Retrofit: 2.11.0
- Coil: 2.6.0
- Room: 2.6.1
- Navigation: 2.7.7

## License

Private - Food Delivery Platform