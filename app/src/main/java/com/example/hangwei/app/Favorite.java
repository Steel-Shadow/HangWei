package com.example.hangwei.app;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hangwei.R;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Favorite {
    private final String userId;
    private final String id;
    private final String urlFavChange;
    private final String urlCheck;
    private final TextView mFavorite; // 收藏
    private boolean mHasFavorite; // 是否收藏了
    private final Context context;

    public Favorite(Context context, TextView textView, String id, String urlFavChange, String urlCheck) {
        this.context = context;
        this.mFavorite = textView;
        this.id = id;
        mHasFavorite = false;
        textView.setTextColor(context.getResources().getColor(R.color.gray, null));
        textView.getCompoundDrawablesRelative()[1].setTint(context.getResources().getColor(R.color.gray, null));
        this.urlFavChange = urlFavChange;
        this.urlCheck = urlCheck;
        userId = context.getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null");
        textView.setOnClickListener(this::clickFavorite);
        updateFavorite();
    }

    public void updateFavorite() {
        HashMap<String, String> params = new HashMap<>(2);
        params.put("userId", userId);
        params.put("dishId", id);
        AsyncHttpUtil.httpPost(urlCheck, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("读取收藏信息失败", ToastConst.successStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 2) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        setFavorite(data.getBoolean("isFavorite"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void clickFavorite(View v) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("dishId", id);
        AsyncHttpUtil.httpPost(urlFavChange, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("收藏失败", ToastConst.successStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 2) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        changeFavorite();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setFavorite(boolean mHasFavorite) {
        if (mHasFavorite != this.mHasFavorite) {
            changeFavorite();
        }
    }

    private void changeFavorite() {
        mHasFavorite = !mHasFavorite;
        if (mHasFavorite) {
            mFavorite.setTextColor(context.getResources().getColor(R.color.gold, null));
            mFavorite.getCompoundDrawablesRelative()[1].setTint(context.getResources().getColor(R.color.gold, null));
        } else {
            mFavorite.setTextColor(context.getResources().getColor(R.color.gray, null));
            mFavorite.getCompoundDrawablesRelative()[1].setTint(context.getResources().getColor(R.color.gray, null));
        }
    }
}
