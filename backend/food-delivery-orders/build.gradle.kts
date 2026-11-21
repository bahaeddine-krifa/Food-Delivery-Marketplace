dependencies {
    implementation(project(":food-delivery-common"))
    implementation(project(":food-delivery-users"))
    implementation(project(":food-delivery-restaurants"))
    implementation(project(":food-delivery-products"))
    implementation(project(":food-delivery-addresses"))
    implementation(project(":food-delivery-auth"))
    implementation(project(":food-delivery-payments"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.swagger.core.jakarta)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testImplementation(libs.jackson.module.kotlin)
    testImplementation(project(":food-delivery-auth"))
    testImplementation(project(":food-delivery-categories"))
    testImplementation(project(":food-delivery-products"))
    testImplementation(project(":food-delivery-restaurants"))
    testImplementation(project(":food-delivery-deliveries"))
    testRuntimeOnly("com.h2database:h2:2.2.224")
}