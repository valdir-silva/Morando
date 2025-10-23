plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.alunando.morando.sdui"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = false
        }
    }

    flavorDimensions += "backend"
    productFlavors {
        create("mock") {
            dimension = "backend"
        }

        create("firebase") {
            dimension = "backend"
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core module
    implementation(project(":core"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)

    // Moshi para parsing de JSON
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
