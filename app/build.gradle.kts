plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.citycycle"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.citycycle"
        minSdk = 25
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

    // ✅ Keep only this one inside android {}
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.lottie)

    implementation("com.github.bumptech.glide:glide:4.13.0")

    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
}