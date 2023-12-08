package com.example.hangwei_administrator.consts;

import android.view.Gravity;

import com.example.hangwei_administrator.R;
import com.hjq.toast.style.CustomToastStyle;

public class ToastConst {
    public static final CustomToastStyle hintStyle =
            new CustomToastStyle(R.layout.toast_hint, Gravity.BOTTOM, 0, 100);
    public static final CustomToastStyle warnStyle =
            new CustomToastStyle(R.layout.toast_warn, Gravity.BOTTOM, 0, 100);
    public static final CustomToastStyle errorStyle =
            new CustomToastStyle(R.layout.toast_error, Gravity.BOTTOM, 0, 100);
    public static final CustomToastStyle successStyle =
            new CustomToastStyle(R.layout.toast_success, Gravity.BOTTOM, 0, 100);
}
