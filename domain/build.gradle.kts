plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.kotlinStdLib)

    testImplementation(TestDependencies.assertJ)
    testImplementation(TestDependencies.junit)
    testImplementation(TestDependencies.mockitoKotlin)
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
