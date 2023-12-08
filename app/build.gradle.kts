plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.hangwei_administrator"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.hangwei_administrator"
        minSdk = 24
        targetSdk = 32
        versionCode = 4
        versionName = "4.0"

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
    // 指示器框架：https://github.com/ongakuer/CircleIndicator
    implementation("me.relex:circleindicator:2.1.6")
    // 动画解析库：https://github.com/airbnb/lottie-android
    // 动画资源：https://lottiefiles.com、https://icons8.com/animated-icons
    implementation("com.airbnb.android:lottie:6.1.0")
    // 标题栏框架
    implementation("com.github.getActivity:TitleBar:10.5")
    // 沉浸式框架：https://github.com/gyf-dev/ImmersionBar
    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")
    // 腾讯Bugly异常捕捉
    implementation("com.tencent.bugly:crashreport:latest.release")
    // 日志打印框架：https://github.com/JakeWharton/timber
    implementation("com.jakewharton.timber:timber:4.7.1")
    // AOP 插件库：https://mvnrepository.com/artifact/org.aspectj/aspectjrt
    implementation("org.aspectj:aspectjrt:1.9.6")
    // 权限请求框架
    implementation("com.github.getActivity:XXPermissions:18.5")
    // ShapeView：https://github.com/getActivity/ShapeView
    implementation("com.github.getActivity:ShapeView:9.0")
    // ShapeDrawable：https://github.com/getActivity/ShapeDrawable
    implementation("com.github.getActivity:ShapeDrawable:3.0")
    // 图片加载框架：https://github.com/bumptech/glide
    // 官方使用文档：https://github.com/Muyangmin/glide-docs-cn
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    // photoView
    implementation("com.github.chrisbanes:PhotoView:latest.release")
    // 网络请求框架：https://github.com/getActivity/EasyHttp
    implementation("com.github.getActivity:EasyHttp:12.6")
    // 上拉刷新下拉加载框架：https://github.com/scwang90/SmartRefreshLayout
    implementation("io.github.scwang90:refresh-layout-kernel:2.1.0")
    implementation("io.github.scwang90:refresh-header-material:2.0.6")
    // GSon
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}