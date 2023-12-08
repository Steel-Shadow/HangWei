package com.example.hangwei_administrator.ui.commu.posts.detail;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppAdapter;

public class PostCommentAdapter extends AppAdapter<PostComment> {
    public PostCommentAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public PostCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostCommentAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private TextView commenter;
        private TextView content;
        private TextView time;

        private ViewHolder() {
            super(R.layout.item_post_comment);
            commenter = findViewById(R.id.post_comment_commenter);
            content = findViewById(R.id.post_comment_content);
            time = findViewById(R.id.post_comment_time);
        }

        @Override
        public void onBindView(int position) {
            PostComment postComment = getItem(position);
            commenter.setText(postComment.userName);
            content.setText(postComment.content);
            time.setText(postComment.time);
        }
    }
}
