plugins {
    id("java-library")
    alias(libs.plugins.kotlin.android)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin.coroutines)

    // Testing
    testImplementation(libs.bundles.testing)
}

