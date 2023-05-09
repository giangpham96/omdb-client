plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain"))
    implementation(libs.coroutines.core)
    implementation(libs.koin.core)
    implementation(libs.kotlin.std.lib)

    testImplementation(libs.assert.j)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
