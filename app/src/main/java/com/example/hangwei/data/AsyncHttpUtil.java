package com.example.hangwei.data;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AsyncHttpUtil {
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(100000, TimeUnit.SECONDS)//设置连接超时时间
            .readTimeout(200000, TimeUnit.SECONDS)//设置读取超时时间
            .build();

    /*
     * GET
     * */
    public static void httpGet(String url, ArrayList<String> params, Callback callback) {
        // build url
        StringBuilder getUrl = new StringBuilder(url);
        for (String s : params) {
            getUrl.append(s).append('/');
        }
        String finalUrl = getUrl.toString();
        System.out.println(finalUrl);

        // build request
        Request request = new Request.Builder().get()
                .url(finalUrl)
                .build();

        // request
        okHttpClient.newCall(request).enqueue(callback);
    }

    /*
     * POST
     * */
    public static void httpPost(String url, HashMap<String, String> params, Callback callback) {
        // build url
        String finalUrl = url;
        System.out.println(finalUrl);
        JSONObject jsonBody = new JSONObject(params);

        // build request
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody.toString()))
                .build();

        // request
        okHttpClient.newCall(request).enqueue(callback);
    }

    /*
     * PUT
     * */
    public static void httpPut(String url, HashMap<String, String> params, Callback callback) {
        // build url
        String finalUrl = url;
        System.out.println(finalUrl);
        JSONObject jsonBody = new JSONObject(params);

        // build request
        Request request = new Request.Builder()
                .url(finalUrl)
                .put(RequestBody.create(MediaType.parse("application/json"), jsonBody.toString()))
                .build();

        // request
        okHttpClient.newCall(request).enqueue(callback);
    }

    /*
     * DELETE
     * */
    public static void httpDelete(String url, ArrayList<String> params, Callback callback) {
        // build url
        StringBuilder deleteUrl = new StringBuilder(url);
        for (String s : params) {
            deleteUrl.append(s).append('/');
        }
        String finalUrl = deleteUrl.toString();
        System.out.println(finalUrl);

        // build request
        Request request = new Request.Builder().delete()
                .url(finalUrl)
                .build();

        // request
        okHttpClient.newCall(request).enqueue(callback);
    }

    /*
     * 上传文件
     * */
    /*
     * POST
     * */
    public static void postFile(File file, String fileType, Callback callback) {
        // build url
        String finalUrl = Ports.postFileUrl + fileType + "/";
        System.out.println(finalUrl);

        // build request
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", "file", RequestBody.create(MediaType.parse("multipart/form-data"), file)).build();

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        // request
        okHttpClient.newCall(request).enqueue(callback);
    }

    /*
     * 得到文件
     * */
    /*
     * GET
     * */
    public static void getFile(String fileName, String fileType, Context context, Callback callback) {
        // build url
        String finalUrl = Ports.getFileUrl + fileName + "/" + fileType + "/";
        fileName += ".";
        fileName += fileType;

        // build request
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        // request
        okHttpClient.newCall(request).enqueue(callback);
    }
}
