plugins {
    kotlin("jvm")
    id("kotlin-kapt")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data"))
    implementation(project(":exception"))

    implementation(libs.coroutines.core)
    implementation(libs.koin.core)
    implementation(libs.kotlin.std.lib)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    api(libs.retrofit) {
        exclude(module = "okhttp")
    }
    implementation(libs.moshi)
    implementation(libs.retrofit.converter.moshi)

    testImplementation(libs.assert.j)
    testImplementation(libs.junit)
    testImplementation(libs.mock.webserver)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
