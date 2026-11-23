pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://repo.spring.io/release") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
    // Let Gradle auto-discover the libs.versions.toml file in the default location
}

rootProject.name = "food-delivery-platform"

include(
    "food-delivery-common",
    "food-delivery-auth",
    "food-delivery-users",
    "food-delivery-restaurants",
    "food-delivery-categories",
    "food-delivery-products",
    "food-delivery-addresses",
    "food-delivery-orders",
    "food-delivery-deliveries",
    "food-delivery-payments",
    "food-delivery-notifications",
    "food-delivery-reviews",
    "food-delivery-admin",
    "food-delivery-api"
)