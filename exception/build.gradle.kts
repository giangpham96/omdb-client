plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.kotlin.std.lib)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
