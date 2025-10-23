plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.alunando.morando.feature.tasks"
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
    // Project modules
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":sdui"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.lifecycle)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)

    // Koin
    implementation(libs.bundles.koin)

    // Coroutines
    implementation(libs.bundles.kotlin.coroutines)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
