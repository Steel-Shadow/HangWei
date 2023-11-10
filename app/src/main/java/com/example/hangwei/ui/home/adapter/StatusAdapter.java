package com.example.hangwei.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.app.Dish;

import java.util.Locale;

/**
 * desc   : 状态数据列表
 */
public final class StatusAdapter extends AppAdapter<Dish> {

    public StatusAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final LinearLayout mItem;
        private TextView mName;
        private TextView mLocation;
        private TextView mPrice;
        private TextView mLikeCount;
        private TextView mCommentCount;
        private ImageView mFoodPic;

        private ViewHolder() {
            super(R.layout.status_dish);
            mItem = findViewById(R.id.status_dish);
            mName = mItem.findViewById(R.id.status_dish_name);
            mLocation = mItem.findViewById(R.id.status_dish_location);
            mPrice = mItem.findViewById(R.id.status_dish_price);
            mLikeCount = mItem.findViewById(R.id.status_dish_like);
            mCommentCount = mItem.findViewById(R.id.status_dish_comment);
            mFoodPic = mItem.findViewById(R.id.status_dish_pic);
        }

        @Override
        public void onBindView(int position) {
            // todo: 显示菜品数据
            Dish dish = getItem(position);

            Glide.with(this.getItemView()).load(dish.foodPicUrl).into(mFoodPic);

            mName.setText(dish.name);

            if (dish.price == (int) dish.price) {
                mPrice.setText(String.format(Locale.CHINA, "%d", (int) dish.price));
            } else {
                mPrice.setText(String.format(Locale.CHINA, "%.1f", dish.price));
            }
        }
    }
}