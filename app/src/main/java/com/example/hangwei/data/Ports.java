package com.example.hangwei.data;

public class Ports {
    public static String api = "http://116.204.10.156:4523/";
    // public static String api = "https://mock.apifox.cn/m1/3369925-0-default/";

    /**
     * 用户类
     */
    public static String userUrl = api + "user/";
    public static String userLoginUrl = userUrl + "login";
    public static String userRegisterUrl = userUrl + "register";
    public static String userModifyUrl = userUrl + "modify";
    public static String userFeedback = userUrl + "feedback";
    public static String userIsSilence = userUrl + "isSilence";
    public static String userCheck = userUrl + "check";

    /*
     * 餐品类
     * */
    public static String dish = api + "dish/";
    public static String dishUpdate = dish + "update";
    public static String dishChoose = dish + "choose";
    public static String dishCommentGet = dish + "comment/get";
    public static String dishCommentAdd = dish + "comment/add";
    public static String sideDish = dish + "sideDish";
    public static String dishFavChange = dish + "favorite/change";
    public static String dishFavCheck = dish + "favorite/check";
    public static String dishHistoryGet = dish + "history/get";
    public static String dishHistoryAdd = dish + "history/add";
    public static String dishUp = dish + "thumbUp";
    public static String dishIsUp = dish + "isThumbUp";
    public static String dishFavAll = dish + "favorite/all";
    public static String dishSearch = dish + "search";
    public static String dishHot = dish + "hot";


    /*
     * 食堂
     */
    public static String canteenFavChange = api + "canteen/favorite/change";
    public static String canteenFavCheck = api + "canteen/favorite/check";
    public static String canteenGetDishes = api + "canteen/getDishes";
    public static String canteenInfo = api + "canteen/info";
    public static String canteenFavAll = api + "canteen/favorite/all";

    /*
     * 窗口
     */
    public static String windowDishes = api + "window/dishes";
    public static String windowFavChange = api + "window/favorite/change";
    public static String windowFavCheck = api + "window/favorite/check";
    public static String windowFavAll = api + "window/favorite/all";

    /**
     * 用户社区：帖子
     */
    public static String postsUrl = api + "posts/";
    public static String postsGet = postsUrl + "get";
    public static String postsGetAll = postsUrl + "getAll";
    public static String postsAddPost = postsUrl + "add";
    public static String postsModify = postsUrl + "modify";
    public static String postsDel = postsUrl + "delete";
    public static String postsGetComments = postsUrl + "getComments";
    public static String postsUp = postsUrl + "thumbUp";
    public static String postsGetUser = postsUrl + "getUser";
    public static String postsReport = postsUrl + "report";

    /**
     * 用户社区：评论
     */
    public static String commentUrl = api + "comment/";
    public static String commentAdd = commentUrl + "add";
    public static String commentDel = commentUrl + "delete";

    /**
     * 用户社区：交友
     */
    public static String friendsUrl = api + "friends/";
    public static String friendsDel = friendsUrl + "delete";
    public static String friendsGetAll = friendsUrl + "getAll";
    public static String friendsFollow = friendsUrl + "follow";
    public static String friendsReport = friendsUrl + "report";
    public static String friendsFind = friendsUrl + "find";

    /**
     * 上传文件：POST
     */
    public static String postFileUrl = api + "postfile";

    /**
     * 得到文件：GET
     * url
     * isArray: false
     */
    public static String getFileUrl = api + "getfile";

    /**
     * 图像上传：POST
     * OCR
     */
    public static String ocrUrl = "https://pro.helloimg.com/api/v1";

    public static String verifyUrl = api + "verify";

}
