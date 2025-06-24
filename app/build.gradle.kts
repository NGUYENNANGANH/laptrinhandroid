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
    // --- CÁC THƯ VIỆN HIỆN CÓ CỦA BẠN (GIỮ NGUYÊN) ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Dòng này đã bao gồm firestore rồi, nhưng hãy dùng libs.google.firebase.firestore bên dưới cho thống nhất
    implementation(libs.firebase.database) // Nếu bạn dùng Realtime Database thì giữ lại
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.glide)
    implementation(libs.fragment.ktx)
    implementation(libs.circleimageview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- PHẦN DỌN DẸP VÀ CHỈNH SỬA ---

    // 1. Import Firebase BoM (Bill of Materials) - Bạn đã làm đúng.
    // Dòng này sẽ quản lý phiên bản của tất cả thư viện Firebase khác.
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))

    // 2. Sử dụng alias từ `libs` để thêm thư viện.
    // Xóa các dòng khai báo bằng chuỗi "com.google.firebase:..." bị lặp lại.
    implementation(libs.firebase.auth)
    implementation(libs.google.firebase.firestore) // <- Đảm bảo dòng này có để sử dụng Firestore
    implementation(libs.play.services.auth)       // <- Đảm bảo dòng này có để sử dụng Google Sign-In

    // (Tùy chọn) Thêm Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Các thư viện khác
    implementation("com.facebook.android:facebook-login:latest.release")
}