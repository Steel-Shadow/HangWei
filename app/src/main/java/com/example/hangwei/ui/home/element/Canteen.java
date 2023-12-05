package com.example.hangwei.ui.home.element;

public class Canteen {
    /**
     * 早餐时间，例如06:30-09:30
     */
    public String id;
    public String breakfast;
    /**
     * 晚餐时间
     */
    public String dinner;
    /**
     * 食堂地点
     */
    public String location;
    /**
     * 午餐时间
     */
    public String lunch;
    /**
     * 食堂名
     */
    public String name;
    /**
     * 食堂图片
     */
    public String picUrl;

    public Canteen(String id, String breakfast, String dinner, String location, String lunch, String name, String picUrl) {
        this.id = id;
        this.breakfast = breakfast;
        this.dinner = dinner;
        this.location = location;
        this.lunch = lunch;
        this.name = name;
        this.picUrl = picUrl;
    }
}
