plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = AndroidSettings.compileSdkVersion
    namespace = "leo.me.la.cache"
    defaultConfig {
        minSdk = AndroidSettings.minSdkVersion
        testInstrumentationRunner = AndroidSettings.testInstrumentationRunner
    }
    compileOptions {
        sourceCompatibility = AndroidSettings.sourceCompatibility
        targetCompatibility = AndroidSettings.targetCompatibility
    }
}

dependencies {
    implementation(project(":data"))

    implementation(libs.coroutines.core)
    implementation(libs.kotlin.std.lib)
    implementation(libs.koin.core)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)

    kapt(libs.room.compiler)

    testImplementation(libs.assert.j)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.room.test)
    testImplementation(libs.test.ext)
    testImplementation(libs.test.runner)
    testImplementation(libs.test.rule)
}
