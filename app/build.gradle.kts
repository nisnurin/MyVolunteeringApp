plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
}


android {
    namespace = "com.example.ict602my_vol" // ← wajib
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ict602my_vol"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Tukar dari 1.8 ke 17
        targetCompatibility = JavaVersion.VERSION_17 // Tukar dari 1.8 ke 17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}


dependencies {
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics")

    // Google Sign-In (Pastikan baris ini ada untuk hilangkan ralat Unresolved Reference)
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Compose & UI
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
}