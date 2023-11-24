package com.example.hangwei.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.app.Comment;

/**
 * desc   : 菜品数据列表
 */
public final class CommentAdapter extends AppAdapter<Comment> {

    public CommentAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final LinearLayout mComment;
        public String mUserId;
        public String mDate;
        public String mText; // 评论内容

        private ViewHolder() {
            super(R.layout.status_dish);
            mComment = findViewById(R.id.status_dish);
//            mUserId = mComment.findViewById(s)
        }

        @Override
        public void onBindView(int position) {
            // todo: 显示评论数据
//            Comment dish = getItem(position);
//
//            Glide.with(this.getItemView()).load(dish.foodPicUrl).into(mFoodPic);
//
//            mName.setText(dish.name);
//
//            mPrice.setText(String.format(Locale.CHINA, "%d", dish.price));
        }
    }
}