plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Dependencies.kotlinStdLib)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
