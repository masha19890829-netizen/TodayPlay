import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseKeystorePropertiesFile = rootProject.file("keystore.properties")
val releaseKeystoreProperties = Properties().apply {
    if (releaseKeystorePropertiesFile.exists()) {
        releaseKeystorePropertiesFile.inputStream().use { load(it) }
    }
}
val hasReleaseKeystore = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
    .all { key -> releaseKeystoreProperties.getProperty(key)?.isNotBlank() == true }
val releaseConfigPropertiesFile = rootProject.file("release_config.properties")
val releaseConfigProperties = Properties().apply {
    if (releaseConfigPropertiesFile.exists()) {
        releaseConfigPropertiesFile.inputStream().use { load(it) }
    }
}

fun releaseConfigValue(key: String): String =
    releaseConfigProperties.getProperty(key)?.trim().orEmpty()

fun buildConfigString(value: String): String =
    "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

android {
    namespace = "com.todayplay.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.todayplay.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 79
        versionName = "0.9.61"
        buildConfigField("String", "BILLING_VERIFY_ENDPOINT", buildConfigString(releaseConfigValue("BILLING_VERIFY_ENDPOINT")))
        buildConfigField("String", "TRAVEL_CONTENT_BASE_URL", buildConfigString(releaseConfigValue("TRAVEL_CONTENT_BASE_URL")))
        buildConfigField("String", "PRIVACY_POLICY_URL", buildConfigString(releaseConfigValue("PRIVACY_POLICY_URL")))
        buildConfigField("String", "SUPPORT_EMAIL", buildConfigString(releaseConfigValue("SUPPORT_EMAIL")))
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", buildConfigString(releaseConfigValue("GOOGLE_WEB_CLIENT_ID")))
        buildConfigField("String", "AUTH_VERIFY_ENDPOINT", buildConfigString(releaseConfigValue("AUTH_VERIFY_ENDPOINT")))
        buildConfigField("String", "AI_ROUTE_GATEWAY_URL", buildConfigString(releaseConfigValue("AI_ROUTE_GATEWAY_URL")))

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("releaseUpload") {
                storeFile = rootProject.file(releaseKeystoreProperties.getProperty("storeFile"))
                storePassword = releaseKeystoreProperties.getProperty("storePassword")
                keyAlias = releaseKeystoreProperties.getProperty("keyAlias")
                keyPassword = releaseKeystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("releaseUpload")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.android.billingclient:billing:8.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
