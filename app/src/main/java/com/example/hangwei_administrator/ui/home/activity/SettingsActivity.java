package com.example.hangwei_administrator.ui.home.activity;

import android.content.SharedPreferences;
import android.view.View;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.aop.SingleClick;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.glide.GlideApp;
import com.example.hangwei_administrator.dialog.SafeDialog;
import com.example.hangwei_administrator.manager.ActivityManager;
import com.example.hangwei_administrator.manager.CacheDataManager;
import com.example.hangwei_administrator.manager.ThreadPoolManager;
import com.example.hangwei_administrator.ui.init.FindPwdActivity;
import com.example.hangwei_administrator.ui.init.LoginActivity;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.view.SettingBar;
import com.example.hangwei_administrator.widget.view.SwitchButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {
    private SettingBar mEmailView;
    private SettingBar mPasswordView;
    private SettingBar mCleanCacheView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        mEmailView = findViewById(R.id.settings_email);
        mPasswordView = findViewById(R.id.settings_password);
        mCleanCacheView = findViewById(R.id.settings_cache);

        setOnClickListener(R.id.settings_email, R.id.settings_password,
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
        if (viewId == R.id.settings_email) {

            new SafeDialog.Builder(this)
                    .setListener((dialog, email, code) -> startActivity(EmailResetActivity.class))
                    .show();

        } else if (viewId == R.id.settings_password) {

            startActivity(FindPwdActivity.class);

        } else if (viewId == R.id.settings_update) {

            BrowserActivity.start(this, "https://github.com/Steel-Shadow/HangWei/Releases/latest");

        } else if (viewId == R.id.settings_agreement) {

            BrowserActivity.start(this, "https://github.com/Steel-Shadow/HangWei");

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
