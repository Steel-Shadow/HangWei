package com.example.hangwei.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.example.hangwei.R;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.utils.CheckUtil1;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.CountdownView;
import com.example.hangwei.widget.view.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {
    private EditText userName_input, password_input1, password_input2; // 用户名、密码、确认密码
    private String userName, password1, password2;
    private EditText email_input, code_input; // 邮箱、验证码
    private String email, codeIn, code = null;
    private CountdownView code_send; // 发送验证码
    private SubmitButton btn_register; // 注册按钮
    private AppCompatButton btn_backTo_login; // 返回登录按钮

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        userName_input = findViewById(R.id.register_username_input);
        password_input1 = findViewById(R.id.register_password_input);
        password_input2 = findViewById(R.id.register_password_confirm);

        email_input = findViewById(R.id.register_email_input);
        code_input = findViewById(R.id.register_code_input);

        code_send = findViewById(R.id.register_code_send);
        btn_register = findViewById(R.id.register_btn_register);
        btn_backTo_login = findViewById(R.id.register_btn_login);

        code_send.setOnClickListener(view -> {
            email = email_input.getText().toString().trim();
            if (!CheckUtil1.checkEmail(email)) {
                email_input.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                return;
            }
            // 生成验证码
            Random random = new Random();
            code = String.format("%6d", random.nextInt(1000000));

            sendCode();
        });

        btn_register.setOnClickListener(view -> {
            userName = userName_input.getText().toString().trim();
            password1 = password_input1.getText().toString().trim();
            password2 = password_input2.getText().toString().trim();
            codeIn = code_input.getText().toString().trim();

            btn_register.showProgress();

            if (!CheckUtil1.checkName(userName)) {
                userName_input.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_register.showError(3000);
                }, 500);
                return;
            }
            if (!CheckUtil1.checkPwd(password1)) {
                password_input1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_register.showError(3000);
                }, 500);
                return;
            }
            if (!password1.equals(password2)) {
                ToastUtil.toast("密码输入不一致", ToastConst.warnStyle);
                password_input2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_register.showError(3000);
                }, 500);
                return;
            }
            if (code == null) {
                ToastUtil.toast("请先获取验证码", ToastConst.warnStyle);
                code_send.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_register.showError(3000);
                }, 500);
                return;
            }
            if (!codeIn.equals(code)) {
                ToastUtil.toast("验证码错误", ToastConst.warnStyle);
                code_input.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_register.showError(3000);
                }, 500);
                return;
            }

            doRegister();
        });

        btn_backTo_login.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        initAnim();
    }

    @Override
    protected void initData() {

    }

    private void initAnim() {
        // 欢迎词
        TextView aniTitle = findViewById(R.id.tv_register_title);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        aniTitle.startAnimation(animation);
    }

    private void sendCode() {
        HashMap<String, String> params = new HashMap<>();
        params.put("address", email);
        params.put("code", code);

        AsyncHttpUtil.httpPost(Ports.verifyUrl, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("验证码发送失败，请稍候再试", ToastConst.warnStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        try {
                            ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.toast("验证码已发送，请注意查收", ToastConst.successStyle);
                        // 用于接口测试
                        // System.out.println(code);
                        // 开始倒计时动画
                        code_send.start();
                        // 隐藏软键盘
                        hideKeyboard(getCurrentFocus());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doRegister() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("email", email);
        params.put("password", password1);

        AsyncHttpUtil.httpPost(Ports.userRegisterUrl, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDelayed(() -> {
                    btn_register.showError(3000);
                    ToastUtil.toast("注册失败，请稍候再试", ToastConst.warnStyle);
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
                            btn_register.showError(3000);
                        }, 1000);
                    } else {
                        postDelayed(() -> {
                            btn_register.showSucceed();
                            postDelayed(() -> {
                                // 注册成功后关闭此页面进入主页
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("username", userName);
                                bundle.putString("password", password1);
                                intent.putExtras(bundle);
                                ToastUtil.toast("注册成功", ToastConst.successStyle);
                                // 销毁注册界面
                                finish();
                                // 跳转到主界面，登录成功的状态传递到 MainActivity 中
                                startActivity(intent);
                            }, 1000);
                        }, 1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
