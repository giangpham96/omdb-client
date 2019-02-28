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
    implementation(project(":domain"))
    implementation(project(":exception"))

    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.koinViewModel)
    implementation(Dependencies.kotlinStdLib)
    implementation(Dependencies.livecycleExtensions)
    kapt(Dependencies.lifecycleCompiler)

    testImplementation(TestDependencies.archCoreTesting)
    testImplementation(TestDependencies.assertJ)
    testImplementation(TestDependencies.mockk)
    testImplementation(TestDependencies.coroutinesTest)
    testImplementation(TestDependencies.junit)
}
