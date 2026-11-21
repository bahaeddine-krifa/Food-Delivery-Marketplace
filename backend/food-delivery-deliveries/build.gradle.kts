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
}