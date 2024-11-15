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

    compileSdk = 34
    defaultConfig {
        minSdk = 26
        targetSdk = 34
        versionCode = 207
        versionName = "1.0.9"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    apollo {
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

    signingConfigs {
        create("release") {
            storeFile = file("C:\\Users\\GCV\\Documents\\Checking\\Competishun\\xyz.penpencil.competishun\\android_app_competishun-key.keystore")
            storePassword = "competishun"
            keyAlias = "competishun"
            keyPassword = "competishun"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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

    flavorDimensions += "environment"

    productFlavors {
        create("development") {
            namespace = "xyz.penpencil.competishun"
            dimension = "environment"
            applicationId = "xyz.penpencil.competishun.dev"
            buildConfigField("String", "BASE_URL_GATEKEEPER", "\"https://dev-ant.antino.ca/cm-gatekeeper/graphql\"")
            buildConfigField("String", "BASE_URL_CURATOR", "\"https://dev-ant.antino.ca/cm-curator/graphql\"")
            buildConfigField("String", "BASE_URL_COINKEEPER", "\"https://dev-ant.antino.ca/cm-coinkeeper/graphql\"")
            buildConfigField("String", "APP_VERSION", "\"1.0.8-dev\"")
            buildConfigField("String", "FIREBASE_CONFIG_FILE", "\"google-services.json\"")
            buildConfigField("String", "RAZORPAY_KEY_ID", "\"rzp_test_DcVrk6NysFj71r\"")
            buildConfigField("String", "GOOGLE_CLIENT_ID", "\"887693153546-mv6cfeppj49al2c2bdpainrh6begq6bi.apps.googleusercontent.com\"")
        }

        create("production") {
            namespace = "xyz.penpencil.competishun"
            dimension = "environment"
            applicationId = "xyz.penpencil.competishun"
            buildConfigField("String", "BASE_URL_GATEKEEPER", "\"https://api.competishun.com/cm-gatekeeper/graphql\"")
            buildConfigField("String", "BASE_URL_CURATOR", "\"https://api.competishun.com/cm-curator/graphql\"")
            buildConfigField("String", "BASE_URL_COINKEEPER", "\"https://api.competishun.com/cm-coinkeeper/graphql\"")
            buildConfigField("String", "APP_VERSION", "\"1.0.9\"")
            buildConfigField("String", "FIREBASE_CONFIG_FILE", "\"google-services.json\"")
            buildConfigField("String", "RAZORPAY_KEY_ID", "\"rzp_live_7Hx1eP9SZPlJYE\"")
            buildConfigField("String", "GOOGLE_CLIENT_ID", "\"484629070442-4pcbl6i1289mhm9shaac4bf85b8ad0eg.apps.googleusercontent.com\"")
        }
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

    implementation (libs.mpandroidchart)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)


}
