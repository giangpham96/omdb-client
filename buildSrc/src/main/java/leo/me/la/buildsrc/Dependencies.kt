object Versions {
    const val androidGradlePlugin = "7.4.0"
    const val androidX = "1.6.0"
    const val assertJ = "3.24.1"
    const val cardView = "1.0.0"
    const val constraintlayout = "2.1.4"
    const val coreKtx = "1.9.0"
    const val coreTesting = "1.1.1"
    const val coroutinesCore = "1.6.4"
    const val expandableTextview = "0.1.4"
    const val groupie = "2.10.1"
    const val glide = "4.14.2"
    const val junit = "4.13.2"
    const val koin = "3.3.2"
    const val kotlin = "1.7.20"
    const val leakCanary = "2.10"
    const val leakCanaryNoop = "1.6.3"
    const val lifecycle = "2.5.1"
    const val material = "1.7.0"
    const val mockk = "1.13.3"
    const val mockWebServer = "3.14.1"
    const val moshi = "1.8.0"
    const val okHttp = "4.10.0"
    const val outdatedVersion = "0.44.0"
    const val retrofit = "2.5.0"
    const val retrofitCoroutinesAdapter = "0.9.2"
    const val room = "2.5.0"
    const val testExt = "1.1.5"
}

object Dependencies {
    const val appCompat = "androidx.appcompat:appcompat:${Versions.androidX}"
    const val cardView = "androidx.cardview:cardview:${Versions.cardView}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesCore}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesCore}"
    const val expandableTextview = "com.ms-square:expandableTextView:${Versions.expandableTextview}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    const val glideOkHttp = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"
    const val groupie = "com.github.lisawray.groupie:groupie:${Versions.groupie}"
    const val groupieKotlinAndroidExtension = "com.github.lisawray.groupie:groupie-kotlin-android-extensions:${Versions.groupie}"
    const val koinCore = "io.insert-koin:koin-core:${Versions.koin}"
    const val koinViewModel = "io.insert-koin:koin-android:${Versions.koin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val leakCanaryNoOp = "com.squareup.leakcanary:leakcanary-android-no-op:${Versions.leakCanaryNoop}"
    const val livecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    const val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val materialDesign = "com.google.android.material:material:${Versions.material}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshiCodeGen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitCoroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofitCoroutinesAdapter}"
    const val retrofitConverterMoshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
}


object TestDependencies {
    const val archCoreTesting = "android.arch.core:core-testing:${Versions.coreTesting}"
    const val assertJ = "org.assertj:assertj-core:${Versions.assertJ}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesCore}"
    const val junit = "junit:junit:${Versions.junit}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
    const val roomTest = "androidx.room:room-testing:${Versions.room}"
    const val testExt = "androidx.test.ext:junit:${Versions.testExt}"
    const val testRule = "androidx.test:rules:1.5.0"
    const val testRunner = "androidx.test:runner:1.5.2"
    const val turbine = "app.cash.turbine:turbine:0.12.1"
}
