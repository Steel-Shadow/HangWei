package com.example.hangwei_administrator.ui.commu.friends.list;

import android.net.Uri;

public class Friend {
    public String user;
    public String userName;
    public Uri avatar;
    public boolean isFollow;

    public Friend(String user, String userName, String avatar, boolean isFollow) {
        this.user = user;
        this.userName = userName;
        this.avatar = Uri.parse(avatar);
        this.isFollow = isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public void setFollow() {
        isFollow = !isFollow;
    }
}
