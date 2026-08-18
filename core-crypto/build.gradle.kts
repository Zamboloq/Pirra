plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.pirra.chat.core.crypto"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.clear()
            abiFilters.addAll(setOf("arm64-v8a", "x86_64"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

//    sourceSets {
//        getByName("main") {
//            // Explicitly tells Gradle where to look for .so files
////            jniLibs.srcDirs("src/main/jniLibs")
//            jniLibs.srcDirs("jniLibs")
//        }
//    }

    packaging {
        jniLibs {
            // Re-enforces unpacking extraction mechanics
            useLegacyPackaging = true
        }
        resources {
            // Stop gradle from matching conflicting JNA metadata files
            excludes += "/META-INF/*"
            // Explicitly force the bundle task to retain the matching JNA binaries
            pickFirsts += "**/libjnidispatch.so"
        }

    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.jna) {
        artifact {
            type = "aar"
        }
    }
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}