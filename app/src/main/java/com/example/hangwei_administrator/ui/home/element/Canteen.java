package com.example.hangwei_administrator.ui.home.element;

import com.example.hangwei_administrator.consts.CanteenConst;

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
     * detail
     */
    public String detail;
    /**
     * 食堂图片
     */
    public int picID;

    public Canteen(String id) {
        this.id = id;
        this.breakfast = CanteenConst.canteenBreakfast.get(id);
        this.lunch = CanteenConst.canteenLunch.get(id);
        this.dinner = CanteenConst.canteenDinner.get(id);
        this.location = CanteenConst.canteenPos.get(id);
        this.name = CanteenConst.canteenName.get(id);
        this.detail = CanteenConst.canteenDetail.get(id);
        this.picID = CanteenConst.canteenPic.get(id);
    }
}
