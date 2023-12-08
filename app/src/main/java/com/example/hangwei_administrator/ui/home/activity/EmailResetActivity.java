package com.example.hangwei_administrator.ui.home.activity;

import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.aop.SingleClick;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.manager.InputTextManager;
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

/**
 *    desc   : 设置邮箱
 */
public final class EmailResetActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private EditText mEmailView;
    private EditText mCodeView;
    private CountdownView code_send;
    private SubmitButton mCommitView;

    /** 邮箱，验证码 */
    private String email, code;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_reset;
    }

    @Override
    protected void initView() {
        mEmailView = findViewById(R.id.et_email_reset_email);
        mCodeView = findViewById(R.id.et_email_reset_code);
        code_send = findViewById(R.id.cv_email_reset_countdown);
        mCommitView = findViewById(R.id.btn_email_reset_commit);

        setOnClickListener(code_send, mCommitView);

        mCodeView.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mEmailView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == code_send) {
            email = mEmailView.getText().toString().trim();
            if (!CheckUtil1.checkEmail(email)) {
                mEmailView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                return;
            }
            // 生成验证码
            Random random = new Random();
            code = String.format("%6d", random.nextInt(1000000));
            sendCode();
        } else if (view == mCommitView) {
            if (code == null) {
                ToastUtil.toast("请先获取验证码", ToastConst.warnStyle);
                code_send.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                return;
            }
            String codeIn = mCodeView.getText().toString().trim();
            if (!codeIn.equals(code)) {
                ToastUtil.toast("验证码错误", ToastConst.warnStyle);
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());
            // 邮箱
            refreshEmail();
        }
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

    public void refreshEmail() {
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
        params.put("id", prefs.getString("usedID", null));
        params.put("email", email);

        AsyncHttpUtil.httpPost(Ports.userModifyUrl, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDelayed(() -> {
                    mCommitView.showError(3000);
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
                            mCommitView.showError(3000);
                        }, 1000);
                    } else {
                        postDelayed(() -> {
                            mCommitView.showSucceed();
                            postDelayed(() -> {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("usedEmail", email);
                                editor.commit();

                                ToastUtil.toast("绑定成功", ToastConst.successStyle);
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

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击提交按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}