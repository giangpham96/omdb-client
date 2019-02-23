plugins {
    kotlin("jvm")
    id("kotlin-kapt")
}

dependencies {}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
