package com.example.hangwei.ui.commu.posts.list;

import android.net.Uri;

public class PostItem {
    public String id;
    public String userName;
    public Uri avatar;
    public String title;
    public String tag;
    public String time;
    public Integer thumbUps;

    public PostItem(String id, String userName, String avatar, String title,
                    String tag, String time, Integer thumbUps) {
        this.id = id;
        this.userName = userName;
        this.avatar = Uri.parse(avatar);
        this.title = title;
        this.tag = tag;
        this.time = time;
        this.thumbUps = thumbUps;
    }

    public PostItem(String id, String userName, Uri avatar, String title,
                    String tag, String time, Integer thumbUps) {
        this.id = id;
        this.userName = userName;
        this.avatar = avatar;
        this.title = title;
        this.tag = tag;
        this.time = time;
        this.thumbUps = thumbUps;
    }

    public void setThumbUps(Integer thumbUps) {
        this.thumbUps = thumbUps;
    }

}
