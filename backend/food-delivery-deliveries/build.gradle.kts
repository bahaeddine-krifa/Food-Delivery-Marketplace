dependencies {
    implementation(project(":food-delivery-common"))
    implementation(project(":food-delivery-orders"))
    implementation(project(":food-delivery-users"))
    implementation(project(":food-delivery-addresses"))
    implementation(project(":food-delivery-restaurants"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.swagger.core.jakarta)

    // For CurrentUser
    implementation(project(":food-delivery-auth"))

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testRuntimeOnly("com.h2database:h2:2.2.224")
}