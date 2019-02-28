plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain"))
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.kotlinStdLib)

    testImplementation(TestDependencies.assertJ)
    testImplementation(TestDependencies.junit)
    testImplementation(TestDependencies.mockk)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
