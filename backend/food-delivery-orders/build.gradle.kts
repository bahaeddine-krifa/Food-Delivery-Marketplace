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
}