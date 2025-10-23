plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Google Services plugin aplicado condicionalmente no final do arquivo
}

android {
    namespace = "com.alunando.morando"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alunando.morando"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    flavorDimensions += "backend"
    productFlavors {
        create("mock") {
            dimension = "backend"
            applicationIdSuffix = ".mock"
            buildConfigField("String", "BACKEND_TYPE", "\"MOCK\"")
        }

        create("firebase") {
            dimension = "backend"
            buildConfigField("String", "BACKEND_TYPE", "\"FIREBASE\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Project modules
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":sdui"))
    implementation(project(":feature:feature-tasks"))
    implementation(project(":feature:feature-inventory"))
    implementation(project(":feature:feature-shopping"))
    implementation(project(":feature:feature-barcode"))
    implementation(project(":feature:feature-cooking"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.lifecycle)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)

    // Koin
    implementation(libs.bundles.koin)

    // Retrofit & Moshi
    implementation(libs.bundles.retrofit)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    androidTestImplementation("tools.fastlane:screengrab:2.1.1")

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// Aplicar plugin do Google Services apenas se o arquivo google-services.json existir
// Com product flavors, o Firebase só é necessário para o flavor "firebase"
if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
}

// Desabilitar o processamento do Google Services para o flavor "mock"
tasks.configureEach {
    if (name.contains("MockDebug") || name.contains("MockRelease")) {
        if (name.contains("GoogleServices")) {
            enabled = false
        }
    }
}
