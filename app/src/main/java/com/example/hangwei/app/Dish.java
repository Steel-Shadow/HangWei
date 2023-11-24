package com.example.hangwei.app;

public class Dish {
    public String id;
    public String name;
    public String location;
    public int price;
    public int likeCount;
    public int commentCount;
    public String foodPicUrl;

    public Dish(String id, String name, String location, int price, int likeCount, int commentCount, String foodPicUrl) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.foodPicUrl = foodPicUrl;
    }
}
