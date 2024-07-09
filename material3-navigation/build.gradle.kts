import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
}

android {
    namespace = "com.stefanoq21.material3.navigation"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)

}

//./gradlew.publish
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.stefanoq21"
            artifactId = "material3-navigation"
            version = "0.0.5"
            artifact("$buildDir/outputs/aar/material3-navigation-release.aar")
        }

        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/stefanoq21/BottomSheetNavigator3")
                credentials {
                    val properties = Properties()
                    properties.load(project.rootProject.file("local.properties").reader())
                    username = properties.getProperty("githubUserName") as String
                    password = properties.getProperty("githubToken") as String
                }
            }
        }
    }
}
