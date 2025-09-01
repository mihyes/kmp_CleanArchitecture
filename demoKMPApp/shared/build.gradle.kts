import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-46"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm()
    
    js {
        outputModuleName = "shared"
        version = "1.0.0"
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // Ktor 클라이언트 (네트워킹)
            implementation("io.ktor:ktor-client-core:2.3.7")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
            implementation("io.ktor:ktor-client-logging:2.3.7")

            // 데이터베이스 (SQLDelight)
            implementation("app.cash.sqldelight:runtime:2.0.1")
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")

            // DI ( 의존성 역전으로 Domain 은 kotlin-inject 를 통해 Data 를 주입 )
            implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.8.0")

            // firebase
            implementation("dev.gitlive:firebase-analytics:2.1.0")
//            implementation("dev.gitlive:firebase-crashlytics:2.1.0") // web은 지원이 안되는 상황


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:2.3.7")
            implementation("app.cash.sqldelight:android-driver:2.0.1")

            implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.8.0")

//            implementation("dev.gitlive:firebase-analytics:2.1.0")
            implementation("dev.gitlive:firebase-crashlytics:2.1.0")

//            implementation("com.google.dagger:dagger-android:2.57.1")
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.7")
            implementation("app.cash.sqldelight:native-driver:2.0.1")

            implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.8.0")

//            implementation("dev.gitlive:firebase-analytics:2.1.0")
            implementation("dev.gitlive:firebase-crashlytics:2.1.0")
        }

        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:2.3.7")
            implementation("app.cash.sqldelight:web-worker-driver:2.0.1")

//            implementation("dev.gitlive:firebase-analytics:2.1.0")
        }
    }
}

android {
    namespace = "com.example.demokmpapp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
