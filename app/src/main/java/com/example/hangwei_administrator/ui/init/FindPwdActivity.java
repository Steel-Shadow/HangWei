package com.example.hangwei_administrator.ui.init;

import android.content.SharedPreferences;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.home.activity.HomeActivity;
import com.example.hangwei_administrator.utils.CheckUtil1;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.view.CountdownView;
import com.example.hangwei_administrator.widget.view.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FindPwdActivity extends AppActivity {
    private EditText password_input1, password_input2; // 密码、确认密码、验证码
    private EditText email_input, code_input; // 邮箱、验证码
    private String email, password1, password2;
    private String codeIn, code = null;
    private CountdownView code_send; // 发送验证码
    private SubmitButton btn_finish; // 完成按钮

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pwd;
    }

    @Override
    protected void initView() {
        email_input = findViewById(R.id.findPwd_email_input);
        SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
        email = prefs.getString("usedEmail", null);
        if (email != null) {
            email_input.setText(email);
        }

        password_input1 = findViewById(R.id.findPwd_password_input);
        password_input2 = findViewById(R.id.findPwd_password_confirm);
        code_input= findViewById(R.id.findPwd_code_input);

        code_send = findViewById(R.id.findPwd_code_send);
        btn_finish = findViewById(R.id.btn_findPwd_finish);

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

        btn_finish.setOnClickListener(view -> {
            password1 = password_input1.getText().toString().trim();
            password2 = password_input2.getText().toString().trim();
            codeIn = code_input.getText().toString().trim();

            btn_finish.showProgress();

            if (!CheckUtil1.checkPwd(password1)) {
                password_input1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_finish.showError(3000);
                }, 500);
                return;
            }
            if (!password1.equals(password2)) {
                ToastUtil.toast("密码输入不一致", ToastConst.warnStyle);
                password_input2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_finish.showError(3000);
                }, 500);
                return;
            }
            if (code == null) {
                ToastUtil.toast("请先获取验证码", ToastConst.warnStyle);
                code_send.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_finish.showError(3000);
                }, 500);
                return;
            }
            if (!codeIn.equals(code)) {
                ToastUtil.toast("验证码错误", ToastConst.warnStyle);
                code_input.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                postDelayed(() -> {
                    btn_finish.showError(3000);
                }, 500);
                return;
            }

            doRefreshPwd();
        });

    }

    @Override
    protected void initData() {

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
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }


    private void doRefreshPwd() {
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
        params.put("id", prefs.getString("usedID", null));
        params.put("password", password1);

        AsyncHttpUtil.httpPost(Ports.userModifyUrl, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDelayed(() -> {
                    btn_finish.showError(3000);
                    ToastUtil.toast("服务器有一点小问题~，请稍候再试", ToastConst.warnStyle);
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
                            btn_finish.showError(3000);
                        }, 1000);
                    } else {
                        postDelayed(() -> {
                            btn_finish.showSucceed();
                            postDelayed(() -> {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("usedPwd", password1);
                                editor.commit();

                                ToastUtil.toast("密码重置成功，请妥善保存", ToastConst.successStyle);
                                finish();
                                startActivity(HomeActivity.class);
                            }, 1000);
                        }, 1000);
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
