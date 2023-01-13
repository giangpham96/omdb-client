import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

val app_name: String by project

android {
    compileSdk = AndroidSettings.compileSdkVersion

    defaultConfig {
        val omdbApiKey: String = gradleLocalProperties(rootDir).getProperty("omdb_api_key", "")
        versionName = "0.0.1"
        versionCode = 1

        minSdk = AndroidSettings.minSdkVersion
        targetSdk = AndroidSettings.targetSdkVersion
        testInstrumentationRunner = AndroidSettings.testInstrumentationRunner

        vectorDrawables.useSupportLibrary = true

        base.archivesBaseName = "$versionName-$app_name"

        buildConfigField("String", "OMDB_API_KEY", "\"$omdbApiKey\"")
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
    packagingOptions {
        pickFirst("META-INF/services/javax.annotation.processing.Processor")
        exclude("META-INF/main.kotlin_module")
    }

    androidExtensions {
        isExperimental = true
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
    implementation(Dependencies.appCompat)
    implementation(Dependencies.cardView)
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.constraintLayout)
    implementation(Dependencies.expandableTextview)
    implementation(Dependencies.glide)
    implementation(Dependencies.glideOkHttp)
    implementation(Dependencies.groupie)
    implementation(Dependencies.groupieKotlinAndroidExtension)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.koinViewModel)
    implementation(Dependencies.kotlinStdLib)
    debugImplementation(Dependencies.leakCanary)
    releaseImplementation(Dependencies.leakCanaryNoOp)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.materialDesign)
    implementation(Dependencies.recyclerView)

    kapt(Dependencies.glideCompiler)
}
