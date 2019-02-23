plugins {
    kotlin("jvm")
    id("kotlin-kapt")
}

dependencies {
    implementation(Dependencies.kotlinStdLib)
    implementation(Dependencies.okHttp)
    api(Dependencies.retrofit) {
        exclude(module = "okhttp")
    }
    implementation(Dependencies.moshi)
    kapt(Dependencies.moshiCodeGen)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
