plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safe.args)
}

android {
    namespace = "labs.creative.dictornarymvvmapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "labs.creative.dictornarymvvmapp"
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
    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    implementation(libs.androidx.legacy.support.v4)
    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.hilt.navigation.fragment)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    //dateKT
    detektPlugins(libs.detekt.formatting)
}

// Force-pin core-ktx to avoid transitive bumps to 1.16.0 (needs compileSdk 35 + AGP 8.6)
configurations.all {
    resolutionStrategy {
        force("androidx.core:core:1.13.1")
        force("androidx.core:core-ktx:1.13.1")
    }
}

detekt {
    autoCorrect = true
}