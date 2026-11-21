dependencies {
    implementation(project(":food-delivery-common"))
    implementation(project(":food-delivery-users"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.mail)

    implementation(libs.swagger.core.jakarta)
}