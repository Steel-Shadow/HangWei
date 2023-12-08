package com.example.hangwei_administrator.ui.init;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppAdapter;

/**
 *    desc   : 引导页适配器
 */

public final class GuideAdapter extends AppAdapter<Integer> {

    public GuideAdapter(Context context) {
        super(context);
        addItem(R.drawable.guide_bg1);
        addItem(R.drawable.guide_bg2);
        addItem(R.drawable.guide_bg3);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final ImageView mImageView;

        private ViewHolder() {
            super(R.layout.guide_item);
            mImageView = (ImageView) getItemView();
        }

        @Override
        public void onBindView(int position) {
            mImageView.setImageResource(getItem(position));
        }
    }
}