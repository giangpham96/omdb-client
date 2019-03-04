plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
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

    androidExtensions {
        isExperimental = true
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
    implementation(project(":common"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))
    implementation(project(":remote"))
    implementation(Dependencies.appCompat)
    implementation(Dependencies.cardView)
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.constraintLayout)
    implementation(Dependencies.glide)
    implementation(Dependencies.glideOkHttp)
    implementation(Dependencies.groupie)
    implementation(Dependencies.groupieKotlinAndroidExtension)
    implementation(Dependencies.koinCore)
    implementation(Dependencies.koinViewModel)
    implementation(Dependencies.kotlinStdLib)
    implementation(Dependencies.materialDesign)
    implementation(Dependencies.recyclerView)

    androidTestImplementation(TestDependencies.assertJAndroid)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.espressoCont)
    androidTestImplementation(TestDependencies.koinTest)
    androidTestImplementation(TestDependencies.mockk)
    androidTestImplementation(TestDependencies.testRule)
    androidTestImplementation(TestDependencies.testRunner)
    kapt(Dependencies.glideCompiler)
}
