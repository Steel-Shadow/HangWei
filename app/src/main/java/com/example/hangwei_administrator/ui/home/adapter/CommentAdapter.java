package com.example.hangwei_administrator.ui.home.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppAdapter;
import com.example.hangwei_administrator.data.glide.GlideApp;
import com.example.hangwei_administrator.ui.home.element.Comment;

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
            GlideApp.with(getContext())
                    .load(Uri.parse(comment.picUrl))
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mUserPic);
            mUserName.setText(comment.userName);
            mDate.setText(comment.date);
            mText.setText(comment.text);
        }
    }
}