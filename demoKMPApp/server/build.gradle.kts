plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.example.demokmpapp"
version = "1.0.0"
application {
    mainClass.set("com.example.demokmpapp.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)

    // Content Negotiation & Serialization
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.2")

    // CORS
    implementation("io.ktor:ktor-server-cors-jvm:3.2.2")

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}