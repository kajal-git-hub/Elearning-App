plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.apollo.graph.ql)
    alias(libs.plugins.google.gms.google.services)
    id ("kotlin-parcelize")
    id ("kotlin-kapt")

}

android {
    namespace = "xyz.penpencil.competishun"
    compileSdk = 34
    defaultConfig {
        applicationId = "xyz.penpencil.competishun"
        minSdk = 26
        targetSdk = 34
        versionCode = 204
        versionName = "1.0.6"
        val razorpayKeyId = findProperty("RAZORPAY_KEY_ID") as String? ?: ""
        buildConfigField("String", "RAZORPAY_KEY_ID", "\"$razorpayKeyId\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    apollo {
        // useVersion2Compat()

        service("gatekeeper") {
            packageName.set("com.student.competishun.gatekeeper")
            schemaFile.set(file("src/main/graphql/com/student/competishun/gatekeeper/schema-gatekeeper.graphqls"))
            sourceFolder.set("com/student/competishun/gatekeeper")
            mapScalarToUpload("Upload")
            generateKotlinModels.set(true)
        }
        service("curator") {
            packageName.set("com.student.competishun.curator")
            schemaFile.set(file("src/main/graphql/com/student/competishun/curator/schema-curator.graphqls"))
            sourceFolder.set("com/student/competishun/curator")
            generateKotlinModels.set(true)
        }
        service("coinkeeper") {
            packageName.set("com.student.competishun.coinkeeper")
            schemaFile.set(file("src/main/graphql/com/student/competishun/coinkeeper/schema-coinkeeper.graphqls"))
            sourceFolder.set("com/student/competishun/coinkeeper")
            generateKotlinModels.set(true)
        }

    }
    buildTypes {
        release {
            isMinifyEnabled = true
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.googleid)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.drive)
    implementation(libs.androidx.credentials)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials.play.services.auth.v122)
    implementation(libs.googleid)
    implementation(libs.androidx.core.i18n)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Gson
    implementation(libs.convertor.gson)
    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.kapt)
    //Graph ql
    implementation(libs.apollo.runtime)
    implementation(libs.apollo.api)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //exoplayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.otaliastudios.zoomlayout)
    //gif
    implementation(libs.android.gif.drawable)
    //dotindicator
    implementation (libs.dotsindicator.v50)
    implementation(libs.razorpay.checkout)
    implementation(libs.play.services.wallet)
    implementation (libs.github.glide)

    implementation (libs.exoplayer)

    implementation (libs.pdf.viewer)

    implementation(libs.androidx.work.runtime)

    implementation(libs.circular.progress.indicator)

    implementation (libs.core)

    implementation (libs.play.services.auth.api.phone)

    implementation (libs.keyboardvisibilityevent)

    implementation (libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    implementation(libs.ketch)


}
