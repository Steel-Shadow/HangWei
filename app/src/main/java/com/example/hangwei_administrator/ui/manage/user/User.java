package com.example.hangwei_administrator.ui.manage.user;

import android.net.Uri;

public class User {
    public String id;
    public String userName;
    public String email;
    public String pwd;
    public Uri avatar;
    public boolean isSilence;

    public User(String id, String userName, String email, String pwd, String avatar, boolean isSilence) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.pwd = pwd;
        this.avatar = Uri.parse(avatar);
        this.isSilence = isSilence;
    }

    public void setSilence(boolean silence) {
        isSilence = silence;
    }
}
