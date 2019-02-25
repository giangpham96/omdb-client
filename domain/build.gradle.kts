import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
}

dependencies {
    implementation(project(":common"))
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.kotlinStdLib)

    testImplementation(TestDependencies.assertJ)
    testImplementation(TestDependencies.junit)
    testImplementation(TestDependencies.mockitoKotlin)
}

configure<AllOpenExtension> {
    annotation("leo.me.la.common.annotation.KotlinTestOpen")
}

java {
    sourceCompatibility = AndroidSettings.sourceCompatibility
    targetCompatibility = AndroidSettings.targetCompatibility
}
