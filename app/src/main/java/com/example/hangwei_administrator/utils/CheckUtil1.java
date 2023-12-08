package com.example.hangwei_administrator.utils;

import android.text.TextUtils;

import com.example.hangwei_administrator.consts.ToastConst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil1 {
    public static boolean checkName(String username) {
        if (TextUtils.isEmpty(username)) {
            ToastUtil.toast("请输入用户名", ToastConst.hintStyle);
            return false;
        }
        return true;
    }

    public static boolean checkPwd(String password) {
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toast("请输入密码", ToastConst.hintStyle);
            return false;
        } else if (password.length() < 6 || password.length() > 18) {
            ToastUtil.toast("请输入6-18位密码(由字母、数字组成)", ToastConst.hintStyle);
            return false;
        } else {
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);
                if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9')) {
                    ToastUtil.toast("请输入6-18位密码(由字母、数字组成)", ToastConst.hintStyle);
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            ToastUtil.toast("请输入邮箱", ToastConst.hintStyle);
            return false;
        }

        // 邮箱格式正则表达式
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            ToastUtil.toast("邮箱格式不正确", ToastConst.errorStyle);
            return false;
        }
        return true;
    }

}
