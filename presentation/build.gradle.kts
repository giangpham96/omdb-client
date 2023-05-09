plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = AndroidSettings.compileSdkVersion
    namespace = "leo.me.la.presentation"
    defaultConfig {
        minSdk = AndroidSettings.minSdkVersion
    }
    compileOptions {
        sourceCompatibility = AndroidSettings.sourceCompatibility
        targetCompatibility = AndroidSettings.targetCompatibility
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain"))
    implementation(project(":exception"))

    implementation(libs.coroutines.android)
    implementation(libs.koin.view.model)
    implementation(libs.kotlin.std.lib)
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.view.model.ktx)
    kapt(libs.lifecycle.compiler)

    testImplementation(libs.arch.core.testing)
    testImplementation(libs.assert.j)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
}
