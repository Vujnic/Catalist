// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.jetpack.room) apply false
}

buildscript {
    dependencies {
        //noinspection UseTomlInstead
        classpath("com.squareup:javapoet:1.13.0") // Required for dagger
    }
}