plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myaeki"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myaeki"
        minSdk = 21
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.analytics)
    implementation(libs.kotlin.stdlib)
//    implementation(libs.kotlin.stdlib.v1924)
//    implementation(libs.firebase.database)
    // Retrofit
    implementation (libs.squareup.retrofit)
    implementation (libs.converter.gson)

    // OkHttp
    implementation (libs.okhttp)

    // OkHttp JavaNetCookieJar (bagian dari okhttp-urlconnection)
    implementation (libs.okhttp.urlconnection)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // OkHttp
    implementation (libs.okhttp)

    // OkHttp JavaNetCookieJar (bagian dari okhttp-urlconnection)
    implementation (libs.okhttp.urlconnection)
}