package com.example.hangwei.ui.commu.posts.list;

public class PostItem {
    public String id;
    public String userName;
    public String title;
    public String tag;
    public String time;
    public Integer thumbUps;

    public PostItem(String id, String userName, String title,
                    String tag, String time, Integer thumbUps) {
        this.id = id;
        this.userName = userName;
        this.title = title;
        this.tag = tag;
        this.time = time;
        this.thumbUps = thumbUps;
    }

    public void setThumbUps(Integer thumbUps) {
        this.thumbUps = thumbUps;
    }

}
