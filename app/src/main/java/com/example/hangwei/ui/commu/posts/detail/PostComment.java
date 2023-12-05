package com.example.hangwei.ui.commu.posts.detail;

public class PostComment {
    public String id;
    public String userName;
    public String content;
    public String time;

    public PostComment(String id, String userName, String content, String time) {
        this.id = id;
        this.userName = userName;
        this.content = content;
        this.time = time;
    }

    public void setId(String id) {
        this.id = id;
    }
}
