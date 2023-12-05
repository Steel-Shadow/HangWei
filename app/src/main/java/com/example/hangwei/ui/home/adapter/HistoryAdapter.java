package com.example.hangwei.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.ui.home.element.History;

import java.util.Locale;

public class HistoryAdapter extends AppAdapter<History> {
    public HistoryAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final TextView mName;
        private final TextView mTime;
        private final ImageView mFoodPic;

        private ViewHolder() {
            super(R.layout.history_item);
            mName = findViewById(R.id.name);
            mTime = findViewById(R.id.time);
            mFoodPic = findViewById(R.id.picUrl);
        }

        @Override
        public void onBindView(int position) {
            History history = getItem(position);

            mName.setText(history.dish.name);
            mTime.setText(String.format(Locale.CHINA, "%s", history.time));
            Glide.with(this.getItemView()).load(history.dish.foodPicUrl).into(mFoodPic);
        }
    }
}
