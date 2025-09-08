import app.cash.sqldelight.gradle.SqlDelightDatabase
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-46"
    alias(libs.plugins.sqlDelight)

}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true  // 🔥 정적 프레임워크로 설정
        }
    }
    
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
        all {
            languageSettings {
                optIn("kotlin.experimental.ExperimentalObjCName")
            }
        }
        commonMain.dependencies {
            // Ktor 클라이언트 (네트워킹)
            implementation("io.ktor:ktor-client-core:2.3.8")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
            implementation("io.ktor:ktor-client-logging:2.3.8")

            // 데이터베이스 (SQLDelight)
//            implementation(libs.sqldelight.coroutines)
            implementation("app.cash.sqldelight:runtime:2.1.0")
            implementation("app.cash.sqldelight:coroutines-extensions:2.1.0")

            // DI ( 의존성 역전으로 Domain 은 kotlin-inject 를 통해 Data 를 주입 )
            implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.8.0")

            // firebase
            implementation("dev.gitlive:firebase-analytics:2.1.0")
//            implementation("dev.gitlive:firebase-crashlytics:2.1.0") // web은 지원이 안되는 상황

            // Kotlinx Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

            implementation("io.insert-koin:koin-core:3.5.0")

            // ✅ DateTime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {

            // ✅ Android용 Ktor 엔진
//            implementation("io.ktor:ktor-client-android:2.3.8")
            implementation("io.ktor:ktor-client-okhttp:2.3.8")

            // ✅ SQLDelight Android 드라이버
            implementation("app.cash.sqldelight:android-driver:2.1.0")



            implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.8.0")
//            implementation(libs.sqldelight.android)
//            implementation("dev.gitlive:firebase-analytics:2.1.0")
            implementation("dev.gitlive:firebase-crashlytics:2.1.0")
            implementation("io.insert-koin:koin-android:3.5.0")

        }

        iosMain.dependencies {
            // ✅ iOS용 Ktor 엔진
            implementation("io.ktor:ktor-client-darwin:2.3.8")

            // ✅ SQLDelight iOS 드라이버
            implementation("app.cash.sqldelight:native-driver:2.1.0")

            implementation("me.tatarka.inject:kotlin-inject-runtime-kmp:0.8.0")
//            implementation("dev.gitlive:firebase-analytics:2.1.0")
            implementation("dev.gitlive:firebase-crashlytics:2.1.0")
        }

        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:2.3.8")
            implementation("app.cash.sqldelight:web-worker-driver:2.1.0")

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

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.example.demokmpapp.database")  // 🎯 패키지명
            schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
            verifyMigrations.set(true)
        }
    }
}
