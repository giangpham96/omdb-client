import org.gradle.api.JavaVersion

object AndroidSettings {

    const val minSdkVersion = 21
    const val compileSdkVersion = 28
    const val targetSdkVersion = 28

    val sourceCompatibility = JavaVersion.VERSION_1_8
    val targetCompatibility = JavaVersion.VERSION_1_8

    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}
