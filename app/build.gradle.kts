import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetpack.room)
}

android {
    namespace = "com.example.catalist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.catalist"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    hilt {
        enableAggregatingTask = true    // poboljsava performanse Gradle builda sa HILT-om
    }


    // ne radi??? nece da ga ucita
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    // Added
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.material3)
    // Added
    implementation(libs.androidx.ui.animation)
    implementation(libs.androidx.ui.material.icons.extended)


    // Navigation
    implementation(libs.navigation.compose)


    // DI
    implementation(libs.bundles.hilt)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate.android)
    implementation(libs.androidx.navigation.runtime.android)
    ksp(libs.bundles.hilt.compiler)


    // KotlinX Serialization
    implementation(libs.kotlinx.serialization.json)


    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.okhttp.mockwebserver)


    // Retrofit
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.kotlinx.serialization.converter)


    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Datastore preferences
    implementation(libs.androidx.datastore.preferences)
    // (Optional) if I add the core artifact too:
    implementation(libs.androidx.datastore.core)

    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.accompanist.swiperefresh)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}