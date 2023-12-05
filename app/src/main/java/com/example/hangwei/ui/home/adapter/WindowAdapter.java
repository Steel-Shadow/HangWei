package com.example.hangwei.ui.home.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.ui.home.element.Favorite;
import com.example.hangwei.ui.home.element.Window;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.activity.DishInfoActivity;
import com.example.hangwei.ui.home.activity.WindowActivity;

import java.util.Locale;

public class WindowAdapter extends AppAdapter<Window> {
    public WindowAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final TextView mWindowName;
        private Favorite mFavorite;
        private LinearLayout layout1;
        private LinearLayout layout2;
        private final TextView mName1;
        private final TextView mName2;
        private final TextView mLocation1;
        private final TextView mLocation2;
        private final TextView mPrice1;
        private final TextView mPrice2;
        private final TextView mLikeCount1;
        private final TextView mLikeCount2;
        private final TextView mCommentCount1;
        private final TextView mCommentCount2;
        private final ImageView mFoodPic1;
        private final ImageView mFoodPic2;

        private ViewHolder() {
            super(R.layout.window_item);

            mWindowName = findViewById(R.id.window_name);

            layout1 = findViewById(R.id.window_dish1);
            mName1 = layout1.findViewById(R.id.name);
            mLocation1 = layout1.findViewById(R.id.canteen_location);
            mPrice1 = layout1.findViewById(R.id.price);
            mLikeCount1 = layout1.findViewById(R.id.like);
            mCommentCount1 = layout1.findViewById(R.id.comment);
            mFoodPic1 = layout1.findViewById(R.id.picUrl);

            layout2 = findViewById(R.id.window_dish2);
            mName2 = layout2.findViewById(R.id.name);
            mLocation2 = layout2.findViewById(R.id.canteen_location);
            mPrice2 = layout2.findViewById(R.id.price);
            mLikeCount2 = layout2.findViewById(R.id.like);
            mCommentCount2 = layout2.findViewById(R.id.comment);
            mFoodPic2 = layout2.findViewById(R.id.picUrl);
        }

        @Override
        public void onBindView(int position) {
            Window window = getItem(position);

            mWindowName.setText(window.name);
            mFavorite = new Favorite(getContext(), getItemView().findViewById(R.id.window_favorite), window.name, Ports.windowFavChange, Ports.windowFavCheck);

            Dish dish1 = window.dish1;
            mName1.setText(dish1.name);
            mLocation1.setText(dish1.location);
            mPrice1.setText(String.format(Locale.CHINA, "%d", dish1.price));
            mLikeCount1.setText(String.format(Locale.CHINA, "%d", dish1.likeCount));
            mCommentCount1.setText(String.format(Locale.CHINA, "%d", dish1.commentCount));
            Glide.with(this.getItemView()).load(dish1.foodPicUrl).into(mFoodPic1);

            Dish dish2 = window.dish2;
            mName2.setText(dish2.name);
            mLocation2.setText(dish2.location);
            mPrice2.setText(String.format(Locale.CHINA, "%d", dish2.price));
            mLikeCount2.setText(String.format(Locale.CHINA, "%d", dish2.likeCount));
            mCommentCount2.setText(String.format(Locale.CHINA, "%d", dish2.commentCount));
            Glide.with(this.getItemView()).load(dish2.foodPicUrl).into(mFoodPic2);

            //给layout1和layout2设置点击事件
            layout1.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), DishInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", dish1.id);
                bundle.putString("name", dish1.name);
                bundle.putInt("price", dish1.price);
                bundle.putString("picUrl", dish1.foodPicUrl);
                intent.putExtras(bundle);
                startActivity(getContext(), intent, null);
            });

            layout2.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), DishInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", dish2.id);
                bundle.putString("name", dish2.name);
                bundle.putInt("price", dish2.price);
                bundle.putString("picUrl", dish2.foodPicUrl);
                intent.putExtras(bundle);
                startActivity(getContext(), intent, null);
            });

            mWindowName.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), WindowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", window.id);
                bundle.putString("name", window.name);
                intent.putExtras(bundle);
                startActivity(getContext(), intent, null);
            });
        }
    }
}
