package com.example.hangwei.app;

public class Dish {
    public String name;
    public String location;
    public double price;
    public int likeCount;
    public int commentCount;
    public String foodPicUrl;

    public Dish(String name, String location, double price, int likeCount, int commentCount, String foodPicUrl) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.foodPicUrl = foodPicUrl;
    }
}
