plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.hangwei"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.hangwei"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // okhttp依赖
    implementation("com.squareup.okhttp3:okhttp:4.3.0")
    // retrofit依赖
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // RxJava依赖
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
    // RxAndroid依赖
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    // 解决kotlin冲突
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    // rx管理View的生命周期
    implementation("com.trello.rxlifecycle4:rxlifecycle:4.0.2")
    implementation("com.trello.rxlifecycle4:rxlifecycle-android:4.0.2")
    implementation("com.trello.rxlifecycle4:rxlifecycle-components:4.0.2")

    // 吐司框架：https://github.com/getActivity/Toaster
    implementation("com.github.getActivity:Toaster:12.5")
    // 定制化material-dialogs
    implementation("com.afollestad.material-dialogs:core:3.3.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}