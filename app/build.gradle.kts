plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.bono.furniture"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bono.furniture"
        minSdk = 24
        targetSdk = 35
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.jbcrypt)
    implementation(libs.stripe.android)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
}

tasks.register("copyApkToRootDir") {
    doLast {
        val apkFile = file("build/outputs/apk/debug/app-debug.apk")
        val destinationDir = rootDir
        val newApkName = "furniture-store.apk"

        if (apkFile.exists()) {
            copy {
                from(apkFile)
                into(destinationDir)
                rename { newApkName }
            }
            delete(apkFile)
            println("APK copied to root folder and renamed to $newApkName")
        } else {
            println("APK file not found: $apkFile")
        }
    }
}

afterEvaluate {
    tasks.named("assembleDebug") {
        finalizedBy("copyApkToRootDir")
    }
}