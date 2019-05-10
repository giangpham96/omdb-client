// Top-level build file where you can add configuration options common to all sub-projects/modules.
apply(plugin = "com.github.ben-manes.versions")

buildscript {
    val updatePluginVersion = "0.21.0"
    repositories {
        google()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePlugin}")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath("com.github.ben-manes:gradle-versions-plugin:$updatePluginVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx/")
    }
}

task<Delete>("clean") {
    delete = setOf(rootProject.buildDir)
}
