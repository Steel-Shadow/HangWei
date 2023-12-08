package com.example.hangwei_administrator.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.HttpUrl;
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

    public static boolean checkConnectNetwork(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

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

    public static void httpPostForObject(String url, HashMap<String, Object> params, Callback callback) {
        // build url
        System.out.println(url);
        JSONObject jsonBody = new JSONObject(params);

        // build request
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody.toString()))
                .build();

        // request
        okHttpClient.newCall(request).enqueue(callback);
    }

    /*
     * GET
     * */
    public static void httpGetForObject(String url, HashMap<String, Object> params, Callback callback) {
        // 构建请求URL，并添加参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
        }

        String finalUrl = urlBuilder.build().toString();

        // 构建GET请求
        Request request = new Request.Builder()
                .url(finalUrl)
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
     * 上传图片
     * */
    /*
     * POST
     * */
    public static void postImage(String filepath, Callback callback) {
        // build request
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", filepath,
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(filepath)))
                .build();

        Request request = new Request.Builder()
                .url("https://pro.helloimg.com/api/v1/upload")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer 48|hWOWwMdCLMkhrpZZLMeph1TLcu5oXWbjIY0LNrwu")
                .addHeader("Content-Type", "multipart/form-data")
                .method("POST", body)
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
