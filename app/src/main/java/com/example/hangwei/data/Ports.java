package com.example.hangwei.data;

public class Ports {
    //public static String api = "http://116.204.10.156:4523/";
    public static String api = "https://mock.apifox.cn/m1/3369925-0-default/";

    /*
     * 用户类
     * */
    public static String userUrl = api + "user";
    public static String userLoginUrl = api + "user/login";
    public static String userRegisterUrl = api + "user/register";

    /*
     * 餐品类
     * */
    public static String dish = api + "dish/";
    public static String dishUpdate = dish + "update";
    public static String dishChoose = dish + "choose";
    public static String dishComment = dish + "comment";
    public static String favorite = dish + "favorite";
    public static String sideDish = dish + "side";

    /*
     * 上传文件：POST
     * */
    public static String postFileUrl = api + "postfile";

    /*
     * 得到文件：GET
     * url
     * isArray: false
     * */
    public static String getFileUrl = api + "getfile";

    /*
     * 图像上传：POST
     * OCR
     * */
    public static String ocrUrl = api + "ocr";

    public static String verifyUrl = api + "verify";

}
