package com.example.hangwei_administrator.ui.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.SyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.home.activity.HomeActivity;
import com.example.hangwei_administrator.utils.CheckUtil1;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.view.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppActivity {
    private EditText user_input, password_input; // 编辑框
    private String userName, password; // 获取的用户名，密码，加密密码
    private TextView findPassword; // 找回密码
    private SubmitButton btn_login; // 登录按钮

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        btn_login = findViewById(R.id.login_btn_login);
        findPassword = findViewById(R.id.login_find_password);

        user_input = findViewById(R.id.login_input_username);
        password_input = findViewById(R.id.login_input_password);

        btn_login.setOnClickListener(view -> {
            //开始登录，获取用户名和密码 getText().toString().trim();
            password = password_input.getText().toString().trim();

            btn_login.showProgress();
            // 格式检查
            if (!CheckUtil1.checkPwd(password)) {
                password_input.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_login.showError(3000);
                }, 500);
                return;
            }
            if (!SyncHttpUtil.checkConnectNetwork(getApplicationContext())) {
                ToastUtil.toast("请检查网络设置", ToastConst.warnStyle);
                postDelayed(() -> {
                    btn_login.showError(3000);
                }, 500);
                return;
            }

            doLogin();
        });

        findPassword.setOnClickListener(view -> {
            startActivity(FindPwdActivity.class);
        });
    }

    @Override
    protected void initData() {
        userName = "Administrator";
    }

    private void doLogin() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);

        AsyncHttpUtil.httpPost(Ports.userLoginUrl, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDelayed(() -> {
                    btn_login.showError(3000);
                }, 1000);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        postDelayed(() -> {
                            try {
                                ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            btn_login.showError(2000);
                        }, 1000);
                    } else {
                        runOnUiThread(() -> {
                            postDelayed(() -> {
                                btn_login.showSucceed();
                                postDelayed(() -> {
                                    SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    try {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        editor.putBoolean("isLogin", true);
                                        editor.putString("usedName", "Administrator");
                                        editor.putString("usedPwd", password);
                                        editor.putString("usedID", data.getString("id"));
                                        editor.putString("usedEmail", data.getString("email"));
                                        editor.putString("usedAvatar", data.getString("avatar"));
                                        editor.commit();
                                        // 登录成功后关闭此页面进入主页
                                        ToastUtil.toast("登录成功", ToastConst.successStyle);
                                        // 跳转到主界面，登录成功的状态传递到 HomeActivity 中
                                        startActivity(HomeActivity.class);
                                        // 销毁登录界面
                                        finish();
                                    } catch (JSONException e) {
                                        btn_login.reset();
                                        System.out.println(e);
                                        ToastUtil.toast("服务器出了一点小问题~稍候再试哦", ToastConst.warnStyle);
                                    }
                                }, 1000);
                            }, 1000);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

}
