buildscript {
    extra.apply {
        set("compose_version", "1.2.0")
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
        classpath("com.google.gms:google-services:4.3.13")
    }
}

plugins {
    id("com.android.application") version("7.4.0") apply(false)
    id("com.android.library") version("7.4.0") apply(false)
    id("org.jetbrains.kotlin.android") version("1.7.0") apply(false)
}

task("clean") {
    delete(rootProject.buildDir)
}