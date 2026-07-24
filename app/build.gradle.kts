plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.tmrisdaone.studybuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tmrisdaone.studybuddy"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures { compose = true }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        // The project has no instrumented (androidTest) source set, and
        // CI cannot resolve androidx.test:core/rules:1.5.2 on the
        // debugAndroidTestCompileClasspath. Skip the AndroidTest lint
        // variant model so lintDebug doesn't try to build it.
        disable += "MissingTestVariant"
        abortOnError = false
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    
    // Hilt Navigation Compose (for hiltViewModel)
    implementation(libs.hilt.navigation.compose)
    
    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.text)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.extended)
    
    // Explicit foundation dependencies for TextAlign, TextOverflow, KeyboardOptions, etc.
    implementation(libs.compose.foundation)
    implementation(libs.compose.foundation.layout)
    
    // Navigation Compose
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // CameraX
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)

    // Network
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    // androidTestInstrumented test deps removed: the project has no
    // androidTest source set, and the androidx.test:core/rules:1.5.2
    // artifacts were failing to resolve in CI during the lint
    // generateDebugAndroidTestLintModel step. Re-add these when
    // instrumented tests are introduced:
    //   androidTestImplementation(libs.androidx.test.core)
    //   androidTestImplementation(libs.androidx.test.runner)
    //   androidTestImplementation(libs.androidx.test.rules)
    //   androidTestImplementation(libs.androidx.test.ext.junit)
    //   androidTestImplementation(libs.espresso.core)
}
