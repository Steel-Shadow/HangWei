package com.example.hangwei.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.ui.home.element.Comment;

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
        private TextView mUserName;
        private ImageView mUserPic;
        private TextView mDate;
        private TextView mText; // 评论内容

        private ViewHolder() {
            super(R.layout.comment_item);
            mUserName = findViewById(R.id.userName);
            mUserPic = findViewById(R.id.picUrl);
            mDate = findViewById(R.id.date);
            mText = findViewById(R.id.comment);
        }

        @Override
        public void onBindView(int position) {
            Comment comment = getItem(position);
            Glide.with(this.getItemView()).load(comment.picUrl).into(mUserPic);
            mUserName.setText(comment.userName);
            mDate.setText(comment.date);
            mText.setText(comment.text);
        }
    }
}