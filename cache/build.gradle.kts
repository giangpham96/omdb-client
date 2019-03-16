plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(AndroidSettings.compileSdkVersion)
    defaultConfig {
        minSdkVersion(AndroidSettings.minSdkVersion)
        targetSdkVersion(AndroidSettings.targetSdkVersion)
    }
    compileOptions {
        sourceCompatibility = AndroidSettings.sourceCompatibility
        targetCompatibility = AndroidSettings.targetCompatibility
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data"))

    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.kotlinStdLib)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.roomKtx)
    implementation(Dependencies.roomRuntime)

    kapt(Dependencies.roomCompiler)

    testImplementation(TestDependencies.assertJ)
    testImplementation(TestDependencies.coroutinesTest)
    testImplementation(TestDependencies.junit)
    testImplementation(TestDependencies.mockk)
    testImplementation(TestDependencies.roomTest)
}
