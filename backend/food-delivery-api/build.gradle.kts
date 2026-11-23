plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":food-delivery-common"))
    implementation(project(":food-delivery-auth"))
    implementation(project(":food-delivery-users"))
    implementation(project(":food-delivery-restaurants"))
    implementation(project(":food-delivery-categories"))
    implementation(project(":food-delivery-products"))
    implementation(project(":food-delivery-addresses"))
    implementation(project(":food-delivery-orders"))
    implementation(project(":food-delivery-deliveries"))
    implementation(project(":food-delivery-payments"))
    implementation(project(":food-delivery-notifications"))
    implementation(project(":food-delivery-reviews"))
    implementation(project(":food-delivery-admin"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj)
    testRuntimeOnly("com.h2database:h2:2.2.224")
}