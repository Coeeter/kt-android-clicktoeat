buildscript {
    extra.apply {
        set("compose_version", "1.0.1")
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
        classpath("com.google.gms:google-services:4.3.13")
    }
}

plugins {
    id("com.android.application") version("7.1.1") apply(false)
    id("com.android.library") version("7.1.1") apply(false)
    id("org.jetbrains.kotlin.android") version("1.5.21") apply(false)
}

task("clean") {
    delete(rootProject.buildDir)
}