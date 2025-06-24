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
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    implementation ("com.google.android.material:material:1.12.0")

    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.android.gms:play-services-auth:21.0.0")

    implementation ("com.facebook.android:facebook-login:latest.release")

    // Glide (dùng để tải và hiển thị ảnh từ URL)
    implementation(libs.glide)

    implementation(libs.fragment.ktx) // fragment
    implementation(libs.circleimageview)

    implementation ("com.google.android.gms:play-services-auth:21.0.0")

    implementation ("com.facebook.android:facebook-login:latest.release")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

}