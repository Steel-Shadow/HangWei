package com.example.hangwei;

import android.app.Application;

import androidx.core.content.ContextCompat;

import com.example.hangwei.manager.ActivityManager;
import com.example.hangwei.other.SmartBallPulseFooter;
import com.example.hangwei.other.TitleBarStyle;
import com.hjq.bar.TitleBar;
import com.hjq.toast.Toaster;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

public class HangWei extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSdk(this);
    }

    /**
     * 在应用程序创建时执行的初始化操作
     * 在这里初始化全局的变量、配置、第三方库等
     */
    public void initSdk(Application application) {
        // 初始化 Toast 框架
        Toaster.init(application);

        // Activity 栈管理初始化
        ActivityManager.getInstance().init(application);

        // 设置标题栏初始化器
        TitleBar.setDefaultStyle(new TitleBarStyle());

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((cx, layout) ->
                new MaterialHeader(this).setColorSchemeColors(ContextCompat.getColor(this, R.color.common_accent_color)));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((cx, layout) -> new SmartBallPulseFooter(this));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((cx, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false);
        });
    }

    // 可以在这里添加其他需要覆盖的方法，以便在应用程序的不同生命周期中执行相应的操作
}
