package com.example.hangwei_administrator.ui.manage.user;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppAdapter;
import com.example.hangwei_administrator.ui.commu.posts.list.PostItem;

public class DetailAdapter extends AppAdapter<PostItem> {
    public DetailAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private TextView title;
        private TextView tag;
        private TextView time;
        private TextView favorCnt;

        private ViewHolder() {
            super(R.layout.item_post_simple);
            title = findViewById(R.id.post_simple_title);
            tag = findViewById(R.id.post_simple_tag);
            time = findViewById(R.id.post_simple_time);
            favorCnt = findViewById(R.id.post_simple_favorcnt);
        }

        @Override
        public void onBindView(int position) {
            PostItem postItem = getItem(position);
            title.setText(postItem.title);
            tag.setText(postItem.tag);
            time.setText(postItem.time);
            favorCnt.setText(String.valueOf(postItem.thumbUps));
        }
    }
}

