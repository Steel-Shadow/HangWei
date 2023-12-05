package com.example.hangwei.ui.home.activity;

import android.content.SharedPreferences;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.hangwei.R;
import com.example.hangwei.aop.SingleClick;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.glide.GlideApp;
import com.example.hangwei.dialog.SafeDialog;
import com.example.hangwei.dialog.UpdateDialog;
import com.example.hangwei.manager.ActivityManager;
import com.example.hangwei.manager.CacheDataManager;
import com.example.hangwei.manager.ThreadPoolManager;
import com.example.hangwei.other.AppConfig;
import com.example.hangwei.ui.init.FindPwdActivity;
import com.example.hangwei.ui.init.LoginActivity;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.SettingBar;
import com.example.hangwei.widget.view.SwitchButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {
    private SwitchButton mNightSwitchView;
    private SettingBar mEmailView;
    private SettingBar mPasswordView;
    private SettingBar mCleanCacheView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        mNightSwitchView = findViewById(R.id.settings_switch);
        mEmailView = findViewById(R.id.settings_email);
        mPasswordView = findViewById(R.id.settings_password);
        mCleanCacheView = findViewById(R.id.settings_cache);

        // 设置切换按钮的监听
        mNightSwitchView .setOnCheckedChangeListener(this);

        setOnClickListener(R.id.settings_switch, R.id.settings_email, R.id.settings_password,
                R.id.settings_update, R.id.settings_agreement, R.id.settings_about,
                R.id.settings_cache, R.id.settings_exit);
    }

    @Override
    protected void initData() {
        // 获取应用缓存大小
        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(this));

        SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
        mEmailView.setRightText(prefs.getString("usedEmail", null));
        String strongRegex = "^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*$";
        Pattern pattern = Pattern.compile(strongRegex);
        Matcher matcher = pattern.matcher(prefs.getString("usedPwd", ""));
        if (matcher.matches()) {
            mPasswordView.setRightText("密码强度不错哦");
        } else {
            mPasswordView.setRightText("密码强度较低");
        }
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.settings_switch) {
            if (mNightSwitchView.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mNightSwitchView.setChecked(true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mNightSwitchView.setChecked(false);
            }
        } else if (viewId == R.id.settings_email) {

            new SafeDialog.Builder(this)
                    .setListener((dialog, email, code) -> startActivity(EmailResetActivity.class))
                    .show();

        } else if (viewId == R.id.settings_password) {

            startActivity(FindPwdActivity.class);

        } else if (viewId == R.id.settings_update) {
            // 本地的版本码和服务器的进行比较
            if (20 > AppConfig.getVersionCode()) {
                new UpdateDialog.Builder(this)
                        .setVersionName("2.0")
                        .setForceUpdate(false)
                        .setUpdateLog("修复Bug\n优化用户体验")
                        .setDownloadUrl("https://down.qq.com/qqweb/QQ_1/android_apk/Android_8.5.0.5025_537066738.apk")
                        .setFileMd5("560017dc94e8f9b65f4ca997c7feb326")
                        .show();
            } else {
                ToastUtil.toast("当前已是最新版本", ToastConst.successStyle);
            }
        } else if (viewId == R.id.settings_agreement) {

            BrowserActivity.start(this, "https://github.com/getActivity/Donate");

        } else if (viewId == R.id.settings_about) {

            startActivity(AboutActivity.class);

        } else if (viewId == R.id.settings_cache) {
            // 清除内存缓存（必须在主线程）
            GlideApp.get(getActivity()).clearMemory();
            ThreadPoolManager.getInstance().execute(() -> {
                CacheDataManager.clearAllCache(this);
                // 清除本地缓存（必须在子线程）
                GlideApp.get(getActivity()).clearDiskCache();
                post(() -> {
                    // 重新获取应用缓存大小
                    mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(getActivity()));
                });
            });

        } else if (viewId == R.id.settings_exit) {
            SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLogin", false);
            editor.commit();
            startActivity(LoginActivity.class);
            // 进行内存优化，销毁除登录页之外的所有界面
            ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
        }
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        ToastUtil.toast("切换成功", ToastConst.successStyle);
    }
}
