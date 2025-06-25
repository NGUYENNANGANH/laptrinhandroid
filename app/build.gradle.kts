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
// Thay thế toàn bộ khối dependencies của bạn bằng khối này

dependencies {

    // Khai báo Firebase BoM (Bill of Materials) - CHỈ MỘT LẦN
    // Dòng này quản lý phiên bản cho tất cả các thư viện Firebase khác.
    implementation(platform("com.google.firebase:firebase-bom:33.1.1")) // Giữ lại phiên bản mới nhất

    // Các thư viện AndroidX cơ bản
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Các thư viện Firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    // DÒNG QUAN TRỌNG NHẤT ĐỂ SỬA LỖI: Thêm thư viện Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Các thư viện bên thứ ba
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Thư viện cho Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.play.services.auth)
}
