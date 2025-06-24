plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.truyenchu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.truyenchu"
        minSdk = 28
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    dependencies {
        // Core Android UI components - Rất cần thiết cho giao diện và hoạt động của app
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.recyclerview:recyclerview:1.3.2")

        // Firebase - Sử dụng Bill of Materials (BOM) để quản lý phiên bản
        // Cú pháp platform(...) đảm bảo các thư viện Firebase khác tương thích với nhau
        implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
        implementation("com.google.firebase:firebase-database")
        implementation("com.google.firebase:firebase-analytics") // Thêm analytics theo gợi ý, rất hữu ích
        implementation("com.google.firebase:firebase-auth")     // Thêm auth để chuẩn bị cho tính năng đăng nhập

        // Glide for image loading
        implementation("com.github.bumptech.glide:glide:4.16.0")
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation ("com.google.firebase:firebase-database")
        implementation ("com.google.firebase:firebase-auth")
        implementation ("androidx.recyclerview:recyclerview:1.3.2")
        implementation ("com.github.bumptech.glide:glide:4.16.0")
    }


}