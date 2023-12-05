package com.example.hangwei.dialog;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hangwei.base.BaseDialog;
import com.example.hangwei.R;
import com.example.hangwei.aop.SingleClick;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.CountdownView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *    desc   : 身份校验对话框
 */
public final class SafeDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder> {

        private String email, code = null;
        private final TextView mEmailView;
        private final EditText mCodeView;
        private final CountdownView mCountdownView;

        @Nullable
        private OnListener mListener;

        public Builder(Context context) {
            super(context);
            setTitle(R.string.safe_title);
            setCustomView(R.layout.dialog_safe);
            mEmailView = findViewById(R.id.tv_safe_email);
            mCodeView = findViewById(R.id.et_safe_code);
            mCountdownView = findViewById(R.id.cv_safe_countdown);
            setOnClickListener(mCountdownView);

            SharedPreferences prefs = getActivity().getSharedPreferences("BasePrefs", MODE_PRIVATE);
            email = prefs.getString("usedEmail", null);
            mEmailView.setText(email);
        }

        public Builder setCode(String code) {
            mCodeView.setText(code);
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.cv_safe_countdown) {
                // 生成验证码
                Random random = new Random();
                code = String.format("%6d", random.nextInt(1000000));
                sendCode();
            } else if (viewId == R.id.tv_ui_confirm) {
                if (code == null) {
                    ToastUtil.toast("请先获取验证码", ToastConst.warnStyle);
                    mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                    return;
                }
                String codeIn = mCodeView.getText().toString().trim();
                if (!codeIn.equals(code)) {
                    ToastUtil.toast("验证码错误", ToastConst.warnStyle);
                    mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                    return;
                }
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onConfirm(getDialog(), email, mCodeView.getText().toString());
            } else if (viewId == R.id.tv_ui_cancel) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
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
                            // 用于接口测试
                            // System.out.println(code);
                            // 开始倒计时动画
                            mCountdownView.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String email, String code);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}