import org.gradle.api.JavaVersion

object AndroidSettings {

    const val minSdkVersion = 21
    const val compileSdkVersion = 33
    const val targetSdkVersion = 33

    val sourceCompatibility = JavaVersion.VERSION_17
    val targetCompatibility = JavaVersion.VERSION_17

    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}
