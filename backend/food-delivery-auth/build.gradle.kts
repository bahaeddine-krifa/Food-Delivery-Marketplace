dependencies {
    implementation(project(":food-delivery-common"))
    implementation(project(":food-delivery-users"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)

    implementation(libs.jjwt.api)
    implementation(libs.jjwt.impl)
    implementation(libs.jjwt.jackson)
    implementation(libs.nimbus.jose.jwt)

    implementation(libs.swagger.core.jakarta)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testImplementation(libs.jackson.module.kotlin)
    testRuntimeOnly("com.h2database:h2:2.2.224")
}