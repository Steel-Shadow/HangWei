package com.example.hangwei.ui.home.adapter;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.ui.home.element.Favorite;
import com.example.hangwei.ui.home.element.Window;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.activity.DishInfoActivity;
import com.example.hangwei.ui.home.activity.WindowActivity;
import com.example.hangwei.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WindowAdapter extends AppAdapter<Window> {
    public WindowAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public WindowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WindowAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private TextView mWindowName;
        private LinearLayout layout1;
        private LinearLayout layout2;
        private TextView mName1;
        private TextView mName2;
        private TextView mLocation1;
        private TextView mLocation2;
        private TextView mPrice1;
        private TextView mPrice2;
        private ImageView mFoodPic1;
        private ImageView mFoodPic2;

        private ViewHolder() {
            super(R.layout.window_item);

            mWindowName = findViewById(R.id.window_name);

            layout1 = findViewById(R.id.window_dish1);
            mName1 = layout1.findViewById(R.id.name);
            mLocation1 = layout1.findViewById(R.id.canteen_location);
            mPrice1 = layout1.findViewById(R.id.price);
            mFoodPic1 = layout1.findViewById(R.id.picUrl);

            layout2 = findViewById(R.id.window_dish2);
            mName2 = layout2.findViewById(R.id.name);
            mLocation2 = layout2.findViewById(R.id.canteen_location);
            mPrice2 = layout2.findViewById(R.id.price);
            mFoodPic2 = layout2.findViewById(R.id.picUrl);
        }

        @Override
        public void onBindView(int position) {
            Window window = getItem(position);

            mWindowName.setText(window.name);

            Dish dish1 = window.dish1;
            mName1.setText(dish1.name);
            mLocation1.setText(dish1.location);
            mPrice1.setText(dish1.price);
            Glide.with(this.getItemView()).load(dish1.foodPicUrl).into(mFoodPic1);

            updateFavorite(window);

            Dish dish2 = window.dish2;
            if (dish2 == null) {
                layout2.setVisibility(View.GONE);
            } else {
                mName2.setText(dish2.name);
                mLocation2.setText(dish2.location);
                mPrice2.setText(dish2.price);
                Glide.with(this.getItemView()).load(dish2.foodPicUrl).into(mFoodPic2);
            }

            //给layout1和layout2设置点击事件
            layout1.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), DishInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", dish1.id);
                bundle.putString("name", dish1.name);
                bundle.putString("price", dish1.price);
                bundle.putString("picUrl", dish1.foodPicUrl);
                bundle.putInt("favorCnt", dish1.likeCount);
                intent.putExtras(bundle);
                startActivity(getContext(), intent, null);
            });

            layout2.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), DishInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", dish2.id);
                bundle.putString("name", dish2.name);
                bundle.putString("price", dish2.price);
                bundle.putString("picUrl", dish2.foodPicUrl);
                bundle.putInt("favorCnt", dish2.likeCount);
                intent.putExtras(bundle);
                startActivity(getContext(), intent, null);
            });
        }

        public void updateFavorite(Window window) {
            HashMap<String, String> params = new HashMap<>(2);
            params.put("userId", getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", null));
            params.put("id", window.id);
            AsyncHttpUtil.httpPost(Ports.windowFavCheck, params, new Callback() {
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
                        if (jsonObject.getInt("code") == 0) {
                            ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        } else {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.getBoolean("isFavorite")) {
                                mWindowName.setTextColor(Color.parseColor("#FFFFD700"));
                            } else {
                                mWindowName.setTextColor(Color.BLACK);
                            }
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
}
