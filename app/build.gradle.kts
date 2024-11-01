plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "labs.creative.dictornary_mvvm_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "labs.creative.dictornary_mvvm_app"
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
        release {
            buildConfigField("String", "WORD_SEARCH_URL", "\"https://api.datamuse.com/\"")
            buildConfigField("String", "DICTIONARY_SEARCH_URL", "\"https://api.dictionaryapi.dev/api/v2/entries/en/\"")
        }
        debug {
            buildConfigField("String", "WORD_SEARCH_URL", "\"https://api.datamuse.com/\"")
            buildConfigField("String", "DICTIONARY_SEARCH_URL", "\"https://api.dictionaryapi.dev/api/v2/entries/en/\"")
        }
    }
    buildFeatures{
         viewBinding = true
         buildConfig = true
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
    implementation(libs.timber)
    implementation(libs.hilt.android)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    kapt(libs.hilt.android.compiler)
    annotationProcessor(libs.hilt.android.compiler)
    annotationProcessor(libs.androidx.lifecycle.compiler)

}