package com.example.hangwei.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.ui.home.element.Dish;

import java.util.Locale;

/**
 * desc   : 菜品数据列表
 */
public final class DishAdapter extends AppAdapter<Dish> {

    public DishAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final TextView mName;
        private final TextView mLocation;
        private final TextView mPrice;
        private final TextView mLikeCount;
        private final TextView mCommentCount;
        private final ImageView mFoodPic;

        private ViewHolder() {
            super(R.layout.dish_info_item);
            mName = findViewById(R.id.name);
            mLocation = findViewById(R.id.canteen_location);
            mPrice = findViewById(R.id.price);
            mLikeCount = findViewById(R.id.like);
            mCommentCount = findViewById(R.id.comment);
            mFoodPic = findViewById(R.id.picUrl);
        }

        @Override
        public void onBindView(int position) {
            Dish dish = getItem(position);

            mName.setText(dish.name);
            mLocation.setText(dish.location);
            mPrice.setText(dish.price);
            mLikeCount.setText(String.format(Locale.CHINA, "%d", dish.likeCount));
            mCommentCount.setText(String.format(Locale.CHINA, "%d", dish.commentCount));
            Glide.with(this.getItemView()).load(dish.foodPicUrl).into(mFoodPic);
        }
    }
}