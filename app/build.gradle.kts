plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android") version "2.42" apply false
}

android {
    namespace = "com.student.competishun"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.student.competishun"
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
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
     // Core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // UI components
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    // Apollo Android GraphQL client
    implementation(libs.apollo3.apollo.runtime)
    implementation(libs.apollo3.apollo.coroutines.support)

    // Dagger and Hilt for dependency injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.androidx.junit.ktx)
    annotationProcessor("com.google.dagger:hilt-compiler:2.51.1")

    // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    androidTestAnnotationProcessor("com.google.dagger:hilt-compiler:2.51.1")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    testAnnotationProcessor("com.google.dagger:hilt-compiler:2.51.1")

    // logging interceptor
    implementation(libs.logging.interceptor)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx) // or the latest version
    implementation(libs.androidx.navigation.ui.ktx)

    //Glide
    implementation(libs.glide)
    kapt("com.github.bumptech.glide:compiler:4.15.1")
}

kapt {
    correctErrorTypes = true
}