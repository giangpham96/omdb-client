plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

val app_name: String by project

android {
    compileSdkVersion(AndroidSettings.compileSdkVersion)

    defaultConfig {
        versionName = "0.0.1"
        versionCode = 1

        minSdkVersion(AndroidSettings.minSdkVersion)
        targetSdkVersion(AndroidSettings.targetSdkVersion)
        testInstrumentationRunner = AndroidSettings.testInstrumentationRunner

        vectorDrawables.useSupportLibrary = true

        base.archivesBaseName = "$versionName-$app_name"
    }
    signingConfigs {
        create("release") {}
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

        getByName("release") {
            isDebuggable = false
            isZipAlignEnabled = true
            isShrinkResources = true
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            setProguardFiles(
                setOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
                )
            )
            resValue("string", "app_name", app_name)
        }
    }
    packagingOptions {
        pickFirst("META-INF/services/javax.annotation.processing.Processor")
        exclude("META-INF/main.kotlin_module")
    }

    testOptions {
        animationsDisabled = true
        unitTests(delegateClosureOf<Any?> {
            unitTests.isReturnDefaultValues = true
            unitTests.isIncludeAndroidResources = true
        })
    }

    lintOptions {
        isWarningsAsErrors = true
        isCheckReleaseBuilds = false
        disable("ObsoleteLintCustomCheck")
    }
}

dependencies {
    implementation (Dependencies.appCompat)
    implementation (Dependencies.coreKtx)
    implementation(Dependencies.kotlinStdLib)
    implementation (Dependencies.constraintLayout)
}
