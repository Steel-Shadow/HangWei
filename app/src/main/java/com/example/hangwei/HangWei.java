package com.example.hangwei;

import android.app.Application;

import com.hjq.toast.Toaster;

public class HangWei extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 在应用程序创建时执行的初始化操作
        // 可以在这里初始化全局的变量、配置、第三方库等

        // 初始化 Toast 框架
        Toaster.init(this);
    }

    // 可以在这里添加其他需要覆盖的方法，以便在应用程序的不同生命周期中执行相应的操作
}
