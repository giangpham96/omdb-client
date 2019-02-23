object Versions {
    const val androidGradlePlugin = "3.3.1"
    const val androidX = "1.0.0"
    const val assertJ = "3.11.1"
    const val constraintlayout = "1.1.3"
    const val junit = "4.12"
    const val kotlin = "1.3.21"
    const val mockitoKotlin = "1.6.0"
    const val mockWebServer = "3.11.0"
    const val moshi = "1.8.0"
    const val okHttp = "3.11.0"
    const val retrofit = "2.5.0"
    const val retrofitCoroutinesAdapter = "0.9.2"
}

object Dependencies {
    const val appCompat = "androidx.appcompat:appcompat:${Versions.androidX}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.androidX}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshiCodeGen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitCoroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofitCoroutinesAdapter}"
    const val retrofitConverterMoshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
}


object TestDependencies {
    const val assertJ = "org.assertj:assertj-core:${Versions.assertJ}"
    const val junit = "junit:junit:${Versions.junit}"
    const val mockitoKotlin = "com.nhaarman:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
}
