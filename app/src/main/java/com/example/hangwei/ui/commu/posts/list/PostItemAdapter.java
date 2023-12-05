package com.example.hangwei.ui.commu.posts.list;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;

/**
 * desc   : 帖子列表
 */
public class PostItemAdapter extends AppAdapter<PostItem> {
    public PostItemAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public PostItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostItemAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private AppCompatImageView iv_pic_post;
        private TextView tv_post_owner;
        private TextView tv_post_title;
        private TextView tv_post_tag;
        private TextView tv_post_time;
        private TextView tv_favor_count;

        private ViewHolder() {
            super(R.layout.item_post);
            iv_pic_post = findViewById(R.id.post_image);
            tv_post_owner = findViewById(R.id.post_owner);
            tv_post_title = findViewById(R.id.tv_post_title);
            tv_post_tag = findViewById(R.id.tv_post_tag);
            tv_post_time = findViewById(R.id.tv_post_time);
            tv_favor_count = findViewById(R.id.favor_count);
        }

        @Override
        public void onBindView(int position) {
            PostItem postItem= getItem(position);

            tv_post_owner.setText(postItem.userName);
            tv_post_title.setText(postItem.title);
            tv_post_tag.setText(postItem.tag);
            tv_post_time.setText(postItem.time);
            tv_favor_count.setText(String.valueOf(postItem.thumbUps));
        }
    }
}
