plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.screenshot)
    alias(libs.plugins.screenshotTestGen)
}

android {
    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    namespace = "com.kitakkun.screenshottestgen"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    screenshotTestImplementation(libs.androidx.ui.tooling)
}
