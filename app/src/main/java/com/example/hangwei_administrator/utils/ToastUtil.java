package com.example.hangwei_administrator.utils;

import com.hjq.toast.ToastParams;
import com.hjq.toast.Toaster;
import com.hjq.toast.style.CustomToastStyle;

public class ToastUtil {
    public static void toast(String str, CustomToastStyle style) {
        ToastParams params = new ToastParams();
        params.text = str;
        params.style = style;
        Toaster.show(params);
    }
}
