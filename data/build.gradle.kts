plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation(Dependencies.kotlinStdLib)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
