import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinKapt)
//    alias(libs.plugins.kotlinKsp)
}

android {
    namespace = "com.cw.simple.example.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cw.simple.example.app"
        minSdk =  25
        targetSdk =  34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("keyStore") {
            keyAlias = "key0"
            keyPassword = "9876543210"
            storeFile = file("../key.jks")
            storePassword = "0123456789"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField(type = "String", name = "BASE_URL", value = "\"http://192.168.110.207:9010\"")
            signingConfig = signingConfigs.getByName("keyStore")
        }
        debug {
            buildConfigField(type = "String", name = "BASE_URL", value = "\"http://192.168.110.207:9010\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.all {
        outputs
            // default type don't have outputFileName field
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .all { output ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(Date())
                val fileName = "x-v${defaultConfig.versionCode}(${defaultConfig.versionName})_${formattedDate}_${buildType.name}.apk"
                output.outputFileName = fileName
                false
            }
    }
}

dependencies {
    implementation(files("libs/apachezip.jar"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.multidex)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.okhttp)
    implementation(libs.retrofit2)
    implementation(libs.converterGson)
    implementation(libs.mmkv)
    implementation(libs.eventBus)
    implementation(libs.glid)
    implementation(libs.adapterHelper)
    implementation(libs.xlog)
    implementation(libs.refreshKernel)
    implementation(libs.refreshMaterial)
    implementation(libs.refreshClassics)
    implementation(libs.pictureselector)
    implementation(libs.pdf)
    implementation(libs.filedownloader)
    implementation(libs.longan)

    implementation(libs.ijkplayer)
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.rtsp)
}