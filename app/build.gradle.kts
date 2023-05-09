import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

val app_name: String by project

android {
    compileSdk = AndroidSettings.compileSdkVersion
    namespace = "leo.me.la.movies"

    defaultConfig {
        val omdbApiKey: String = gradleLocalProperties(rootDir).getProperty("omdb_api_key", "")
        versionName = "0.0.1"
        versionCode = 1

        minSdk = AndroidSettings.minSdkVersion
        targetSdk = AndroidSettings.targetSdkVersion
        testInstrumentationRunner = AndroidSettings.testInstrumentationRunner

        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "OMDB_API_KEY", "\"$omdbApiKey\"")
        resValue("string", "app_name", "$app_name")
    }
    compileOptions {
        sourceCompatibility = AndroidSettings.sourceCompatibility
        targetCompatibility = AndroidSettings.targetCompatibility
    }
    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-Debug"
            signingConfig = signingConfigs.getByName("debug")
            resValue("string", "app_name", "$app_name - Debug")
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    testOptions {
        animationsDisabled = true
        unitTests {
            unitTests.isReturnDefaultValues = true
            unitTests.isIncludeAndroidResources = true
        }
    }

    lint {
        disable += setOf("ObsoleteLintCustomCheck")
    }
}

dependencies {
    implementation(project(":cache"))
    implementation(project(":common"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))
    implementation(project(":remote"))
    implementation(libs.appcompat)
    implementation(libs.coil)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.foundation)
    implementation(libs.compose.meterial)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.core.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.constraint.layout)
    implementation(libs.expandable.textview)
    implementation(libs.glide)
    implementation(libs.glide.okhttp)
    implementation(libs.groupie)
    implementation(libs.groupie.view.binding)
    implementation(libs.koin.core)
    implementation(libs.koin.view.model)
    implementation(libs.koin.compose)
    implementation(libs.kotlin.std.lib)
    debugImplementation(libs.leakcanary)
    releaseImplementation(libs.leakcanary.noop)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.material.design)
    implementation(libs.recycler.view)

    kapt(libs.glide.compiler)
}
