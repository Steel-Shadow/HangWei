package com.example.hangwei.ui.home.element;

public class Comment {
    public String userName;
    public String picUrl;
    public String date;
    public String text; // 评论内容

    public Comment(String userName, String picUrl, String date, String text) {
        this.picUrl = picUrl;
        this.userName = userName;
        this.date = date;
        this.text = text;
    }
}
