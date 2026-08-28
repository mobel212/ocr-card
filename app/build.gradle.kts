plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")

    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.ocr_v3"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.ocr_v3"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".devnfc"
        }
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/NOTICE.md"
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("org.bouncycastle:bcprov-jdk15to18:1.69")
        force("org.bouncycastle:bcutil-jdk15to18:1.69")
        force("org.bouncycastle:bcpkix-jdk15to18:1.69")
    }
    exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    exclude(group = "org.bouncycastle", module = "bcutil-jdk18on")
    exclude(group = "org.bouncycastle", module = "bcpkix-jdk18on")
    exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
    exclude(group = "org.bouncycastle", module = "bcutil-jdk15on")
    exclude(group = "org.bouncycastle", module = "bcpkix-jdk15on")
}

dependencies {

    //splash screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    //dagger hilt
    implementation("com.google.dagger:hilt-android:2.60.1")
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.geometry)
    ksp("com.google.dagger:hilt-android-compiler:2.60.1")


    // CameraX
    val cameraxVersion = "1.4.0"  // Updated to latest stable

    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")


    //navigation
    val nav_version = "2.9.8"
    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation(libs.androidx.hilt.navigation.compose)



    //icons
    implementation("androidx.compose.material:material-icons-core")

    // ML Kit
    implementation("com.google.mlkit:text-recognition:16.0.1")


    //room
    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //sqlcipher
    implementation( "net.zetetic:android-database-sqlcipher:4.5.3")
    implementation( "androidx.sqlite:sqlite:2.7.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")


    // Biometric Image Processing (Maven Central)
    implementation("com.github.mhshams:jnbis:2.1.2")
    implementation("org.jmrtd:jmrtd:0.8.7")
    implementation("net.sf.scuba:scuba-sc-android:0.0.26")
    implementation("io.github.michaldvorak-gemalto:jp2-android:1.1.0")
    
    // Consistent Bouncy Castle environment for JMRTD
    implementation("org.bouncycastle:bcprov-jdk15to18:1.69")
    implementation("org.bouncycastle:bcutil-jdk15to18:1.69")
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.69")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}