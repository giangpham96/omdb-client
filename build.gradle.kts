
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.netty.util.concurrent.RejectedExecutionHandlers.reject
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath(libs.agp)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.gradle.versions.plugin)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.versions)
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    tasks.withType(Test::class.java) {
        testLogging {
            events = setOf(FAILED, PASSED, SKIPPED, STANDARD_ERROR, STANDARD_OUT)
            exceptionFormat = FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
}

task<Delete>("clean") {
    delete = setOf(rootProject.buildDir)
}

tasks.named("dependencyUpdates").configure {
    this as DependencyUpdatesTask
    resolutionStrategy {
        componentSelection {
            all {
                val rejected = arrayOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea")
                        .map { qualifier -> "(?i).*[.-]${qualifier}[.\\d-+]*".toRegex() }
                    .any { matcher -> matcher.matches(candidate.version) }
                if (rejected) {
                    reject("Release candidate")
                }
            }
        }
    }
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}
