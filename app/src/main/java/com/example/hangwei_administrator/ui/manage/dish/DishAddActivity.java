package com.example.hangwei_administrator.ui.manage.dish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.base.BaseActivity;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.data.glide.GlideApp;
import com.example.hangwei_administrator.ui.home.activity.ImageCropActivity;
import com.example.hangwei_administrator.ui.home.activity.ImageSelectActivity;
import com.example.hangwei_administrator.ui.home.element.Dish;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.view.SubmitButton;
import com.hjq.http.model.FileContentResolver;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DishAddActivity extends AppActivity {
    private String windowId;

    private String dishName;
    private String campus;
    private String canteen;
    private String window;
    private String price;
    private int time;
    private Uri imageUrl;
    public boolean isDrink;

    private TextView mDrink;
    private TextView mRice;
    private TextView mName;
    private TextView mPrice;
    private RadioGroup group;
    private ImageView mImage;
    private SubmitButton btn_add;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_dish;
    }

    @Override
    protected void initView() {
        mDrink = findViewById(R.id.dish_add_drink);
        mRice = findViewById(R.id.dish_add_rice);
        mName = findViewById(R.id.dish_add_name);
        mPrice = findViewById(R.id.dish_add_price);
        group = findViewById(R.id.dish_add_radioGroup);
        mImage = findViewById(R.id.dish_add_image);
        btn_add = findViewById(R.id.dish_add_post);

        isDrink = false;
        mDrink.setTextColor(Color.parseColor("#00CD00"));
        mRice.setTextColor(Color.parseColor("#FFA500"));
        mDrink.setBackground(getDrawable(R.drawable.tag));
        mRice.setBackground(getDrawable(R.drawable.tag2));

        mDrink.setOnClickListener(view -> {
            isDrink = true;
            mDrink.setTextColor(Color.parseColor("#FFA500"));
            mDrink.setBackground(getDrawable(R.drawable.tag2));
            mRice.setTextColor(Color.parseColor("#00CD00"));
            mRice.setBackground(getDrawable(R.drawable.tag));
        });

        mRice.setOnClickListener(view -> {
            isDrink = false;
            mDrink.setTextColor(Color.parseColor("#00CD00"));
            mDrink.setBackground(getDrawable(R.drawable.tag));
            mRice.setTextColor(Color.parseColor("#FFA500"));
            mRice.setBackground(getDrawable(R.drawable.tag2));
        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.dish_add_type1) {
                    time = 1;
                } else if (checkedId == R.id.dish_add_type2) {
                    time = 2;
                } else if (checkedId == R.id.dish_add_type3) {
                    time = 3;
                }
            }
        });

        mImage.setOnClickListener(view -> {
            XXPermissions.with(this)
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (allGranted) {
                                ImageSelectActivity.start((BaseActivity) getActivity(),
                                        data -> updateImage(data.get(0), false));
                            }
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            OnPermissionCallback.super.onDenied(permissions, doNotAskAgain);
                            if (doNotAskAgain) {
                                ToastUtil.toast("被永久拒绝授权，请手动授权", ToastConst.errorStyle);
                                XXPermissions.startPermissionActivity(getContext(), permissions);
                            } else {
                                ToastUtil.toast("存取权限获取失败", ToastConst.warnStyle);
                            }
                        }
                    });
        });

        btn_add.setOnClickListener(view -> getWindowInf(() -> addDish()));
    }

    @Override
    protected void initData() {
        time = 0;
        imageUrl = null;
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        windowId = bundle.getString("windowId");
    }

    private void getWindowInf(Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("windowId", windowId);
        AsyncHttpUtil.httpPostForObject(Ports.windowInf, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("窗口信息获取失败", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        runOnUiThread(() -> {
                            try {
                                JSONObject data = jsonObject.getJSONObject("data");
                                campus = data.getString("campus");
                                canteen = data.getString("canteenName");
                                window = data.getString("windowName");
                                afterResponse.run();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    /** 上传裁剪后的图片 */
    private void updateImage(String filepath, boolean deleteFile) {
        uploadImage(filepath);
    }

    private void uploadImage(String filepath) {
        AsyncHttpUtil.postImage(filepath, new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getBoolean("status")) {
                        runOnUiThread(() -> {
                            try {
                                String url = jsonObject.getJSONObject("data").getJSONObject("links").getString("url");
                                imageUrl = Uri.parse(url);
                                GlideApp.with(getActivity())
                                        .load(imageUrl)
                                        .into(mImage);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        response.close();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("图片上传失败", ToastConst.warnStyle);
            }
        });
    }

    private void addDish() {
        dishName = mName.getText().toString();
        if (TextUtils.isEmpty(dishName)) {
            ToastUtil.toast("请输入餐品名称", ToastConst.warnStyle);
            return;
        }
        price = mPrice.getText().toString();
        if (TextUtils.isEmpty(price)) {
            ToastUtil.toast("请输入价格", ToastConst.warnStyle);
            return;
        }
        if (time == 0) {
            ToastUtil.toast("请选择供应时段", ToastConst.warnStyle);
            return;
        }
        if (imageUrl == null) {
            ToastUtil.toast("请提供餐品图片", ToastConst.warnStyle);
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("dishName", dishName);
        params.put("campus", campus);
        params.put("canteen", canteen);
        params.put("window", window);
        params.put("price", price);
        params.put("time", String.valueOf(time));
        params.put("picture", String.valueOf(imageUrl));
        params.put("isDrink", String.valueOf(isDrink));
        System.out.println(params);

        AsyncHttpUtil.httpPost(Ports.dishAdd, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDelayed(() -> {
                    btn_add.showError(3000);
                    ToastUtil.toast("服务器有一些小问题~", ToastConst.warnStyle);
                }, 1000);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        postDelayed(() -> {
                            ToastUtil.toast("当前服务器繁忙", ToastConst.errorStyle);
                            btn_add.showError(2000);
                        }, 1000);
                    } else {
                        JSONObject jsObj = jsonObject.getJSONObject("data");
                        runOnUiThread(() -> {
                            postDelayed(() -> {
                                btn_add.showSucceed();
                                postDelayed(() -> {
                                    ToastUtil.toast("增加餐品成功", ToastConst.successStyle);
                                    Intent resIntent = new Intent();
                                    try {
                                        resIntent.putExtra("id", jsObj.getString("dishId"));
                                        System.out.println(jsObj.getString("dishId"));
                                        resIntent.putExtra("name", dishName);
                                        resIntent.putExtra("location", canteen + " " + window);
                                        resIntent.putExtra("price", price);
                                        resIntent.putExtra("foodPicUrl", String.valueOf(imageUrl));
                                        setResult(Activity.RESULT_OK, resIntent);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    finish();
                                }, 1000);
                            }, 1000);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }
}
