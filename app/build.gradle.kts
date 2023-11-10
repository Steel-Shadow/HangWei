plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.hangwei"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hangwei"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        dataBinding = true
        compose = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // 上拉刷新下拉加载框架：https://github.com/scwang90/SmartRefreshLayout
    implementation("com.scwang.smart:refresh-layout-kernel:2.0.3")
    implementation("com.scwang.smart:refresh-header-material:2.0.3")

    // ShapeView：https://github.com/getActivity/ShapeView
    implementation("com.github.getActivity:ShapeView:9.0")

    // ShapeDrawable：https://github.com/getActivity/ShapeDrawable
    implementation("com.github.getActivity:ShapeDrawable:3.0")

    implementation("com.airbnb.android:lottie:4.1.0")

    // 标题栏框架：https://github.com/getActivity/TitleBar
    // do not update it
    implementation("com.github.getActivity:TitleBar:9.2")

    // 沉浸式框架：https://github.com/gyf-dev/ImmersionBar
    implementation("com.gyf.immersionbar:immersionbar:3.0.0")

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
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}