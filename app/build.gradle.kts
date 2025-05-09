plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.roadbuddy"
    compileSdk = 34

    buildFeatures{
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.example.roadbuddy"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation (libs.lottie)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Google Maps
    implementation (libs.play.services.maps.v1810)

    // Location services
    implementation (libs.play.services.location)

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Firebase Storage
    implementation ("com.google.firebase:firebase-storage-ktx:20.3.0")

    // Image picker
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    implementation ("com.google.firebase:firebase-messaging:23.1.0")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    implementation ("com.google.mlkit:smart-reply:17.0.2")

}