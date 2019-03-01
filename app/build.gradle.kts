plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

apply {
    from("signing.gradle.kts")
    from("api-key.gradle.kts")
}

val app_name: String by project

android {
    compileSdkVersion(AndroidSettings.compileSdkVersion)

    defaultConfig {
        val omdbApiKey: String by extra
        versionName = "0.0.1"
        versionCode = 1

        minSdkVersion(AndroidSettings.minSdkVersion)
        targetSdkVersion(AndroidSettings.targetSdkVersion)
        testInstrumentationRunner = AndroidSettings.testInstrumentationRunner

        vectorDrawables.useSupportLibrary = true

        base.archivesBaseName = "$versionName-$app_name"

        buildConfigField("String", "OMDB_API_KEY", "\"$omdbApiKey\"")
    }
    signingConfigs {
        create("release") {
            try {
                val signaturePath: String by extra
                val signatureKeystorePassword: String by extra
                val signatureKeystoreAlias: String by extra
                val signatureKeyPassword: String by extra
                storeFile = file(signaturePath)
                storePassword = signatureKeyPassword
                keyAlias = signatureKeystoreAlias
                keyPassword = signatureKeystorePassword
            } catch (ignore: Exception) {
                logger.log(LogLevel.WARN, "sign.properties file could not be parsed or is missing")
            }
        }
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
    implementation(Dependencies.appCompat)
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.constraintLayout)
    implementation(Dependencies.kotlinStdLib)
}
