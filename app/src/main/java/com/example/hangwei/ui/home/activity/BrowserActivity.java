package com.example.hangwei.ui.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.hangwei.R;
import com.example.hangwei.action.StatusAction;
import com.example.hangwei.aop.CheckNet;
import com.example.hangwei.aop.Log;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.widget.view.BrowserView;
import com.example.hangwei.widget.layout.StatusLayout;
import com.hjq.bar.TitleBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

/**
 *    desc   : 浏览器界面
 */
public final class BrowserActivity extends AppActivity
        implements StatusAction, OnRefreshListener {

    private static final String INTENT_KEY_IN_URL = "url";

    @CheckNet
    @Log
    public static void start(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(INTENT_KEY_IN_URL, url);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private StatusLayout mStatusLayout;
    private ProgressBar mProgressBar;
    private SmartRefreshLayout mRefreshLayout;
    private BrowserView mBrowserView;
    private FrameLayout mLayout; /** 全屏播放容器 */
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mCustomView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.hl_browser_hint);
        mProgressBar = findViewById(R.id.pb_browser_progress);
        mRefreshLayout = findViewById(R.id.sl_browser_refresh);
        mBrowserView = findViewById(R.id.wv_browser_view);
        mLayout = findViewById(R.id.fl_video);
        mCustomView = null;

        // 设置 WebView 生命管控
        mBrowserView.setLifecycleOwner(this);
        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        showLoading();

        initWebView(mBrowserView);
        mBrowserView.setBrowserViewClient(new AppBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new AppBrowserChromeClient(mBrowserView));
        mBrowserView.loadUrl(getString(INTENT_KEY_IN_URL));
    }

    /**
     * 设置webView 相关属性
     */
    private void initWebView(WebView webview) {
        WebSettings setting= webview.getSettings();
        setting.setJavaScriptEnabled(true); // 设置支持javascript脚本
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置缓存模式
        webview.setVerticalScrollBarEnabled(false); // 取消Vertical ScrollBar显示
        webview.setHorizontalScrollBarEnabled(false); // 取消Horizontal ScrollBar显示
        // 设置自适应屏幕，两者合用
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

        setting.setAllowFileAccess(true); // 允许访问文件
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webview.setFocusable(false); // 去掉超链接的外边框
        setting.setDefaultTextEncodingName("GBK"); // 设置文本编码（根据页面要求设置）
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public void onLeftClick(TitleBar titleBar) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCustomView != null) {
                hideCustomView();
            } else if (mBrowserView.canGoBack()) {
                // 后退网页并且拦截该事件
                mBrowserView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重新加载当前页
     */
    @CheckNet
    private void reload() {
        mBrowserView.reload();
    }

    /**
     * {@link OnRefreshListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        reload();
    }

    private class AppBrowserViewClient extends BrowserView.BrowserViewClient {

        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            post(() -> showError(listener -> reload()));
        }

        /**
         * 开始加载网页
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * 完成加载网页
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.finishRefresh();
            showComplete();
        }
    }

    private class AppBrowserChromeClient extends BrowserView.BrowserChromeClient {

        private AppBrowserChromeClient(BrowserView view) {
            super(view);
        }

        /**
         * 收到网页标题
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title == null) {
                return;
            }
            setTitle(title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (icon == null) {
                return;
            }
            setRightIcon(new BitmapDrawable(getResources(), icon));
        }

        /**
         * 收到加载进度变化
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            showCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null) {
                return;
            }
            hideCustomView();
        }
    }

    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        mCustomView = view;
        mCustomView.setVisibility(View.VISIBLE);
        mCustomViewCallback = callback;
        mLayout.addView(mCustomView);
        mLayout.setVisibility(View.VISIBLE);
        mLayout.bringToFront();
        getTitleBar().setVisibility(View.GONE); // 隐藏标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置横屏
    }

    public void hideCustomView() {
        mCustomView.setVisibility(View.GONE);
        mLayout.removeView(mCustomView);
        mCustomView = null;
        mLayout.setVisibility(View.GONE);
        try {
            mCustomViewCallback.onCustomViewHidden();
        } catch (Exception e) {
        }
        getTitleBar().setVisibility(View.VISIBLE); // 显示标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBrowserView.destroy();
        mBrowserView = null;
    }
}