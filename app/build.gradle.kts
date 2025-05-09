import org.gradle.internal.impldep.org.jsoup.nodes.Document.OutputSettings.Syntax.html
import org.gradle.internal.impldep.org.jsoup.nodes.Document.OutputSettings.Syntax.xml

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
    id ("jacoco")
}

android {
    namespace = "com.example.movietime"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.movietime"
        minSdk = 26
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
    implementation(libs.androidx.viewpager2)
    implementation(libs.glide)
    implementation(libs.retrofit2.retrofit)
    implementation (libs.gson)
    implementation(libs.volley)
    implementation(libs.androidx.room.ktx)
    implementation (libs.hilt.android)
    implementation(libs.mockito.core)
    implementation(libs.mockito.inline)
    implementation(libs.robolectric)
    testImplementation(libs.androidx.runner)
    testImplementation(libs.androidx.junit)
    debugImplementation(libs.leakcanary.android)
    kapt (libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

jacoco {
    toolVersion = "0.8.7"
}
