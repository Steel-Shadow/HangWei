package com.example.hangwei.ui.home.element;

import org.json.JSONObject;

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

    public Dish(JSONObject jsonDish) {
        try {
            this.id = jsonDish.getString("dishId");
            this.name = jsonDish.getString("dishName");
            this.location = jsonDish.getString("campus");
            this.price = jsonDish.getInt("price");
            this.likeCount = jsonDish.getInt("likeCount");
            this.commentCount = jsonDish.getInt("commentCount");
            this.foodPicUrl = jsonDish.getString("picture");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
