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
        testInstrumentationRunner = AndroidSettings.testInstrumentationRunner
    }
    compileOptions {
        sourceCompatibility = AndroidSettings.sourceCompatibility
        targetCompatibility = AndroidSettings.targetCompatibility
    }
}

dependencies {
    implementation(project(":data"))

    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.kotlinStdLib)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.roomKtx)
    implementation(Dependencies.roomRuntime)

    kapt(Dependencies.roomCompiler)

    androidTestImplementation(TestDependencies.assertJ)
    androidTestImplementation(TestDependencies.junit)
    androidTestImplementation(TestDependencies.roomTest)
    androidTestImplementation(TestDependencies.testExt)
    androidTestImplementation(TestDependencies.testRunner)
    androidTestImplementation(TestDependencies.testRule)
}
