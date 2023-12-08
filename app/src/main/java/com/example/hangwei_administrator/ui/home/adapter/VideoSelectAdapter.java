package com.example.hangwei_administrator.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppAdapter;
import com.example.hangwei_administrator.data.glide.GlideApp;
import com.example.hangwei_administrator.manager.CacheDataManager;
import com.example.hangwei_administrator.ui.home.activity.VideoSelectActivity;
import com.example.hangwei_administrator.widget.view.PlayerView;

import java.util.List;

/**
 *    desc   : 视频选择适配器
 */
public final class VideoSelectAdapter extends AppAdapter<VideoSelectActivity.VideoBean> {

    private final List<VideoSelectActivity.VideoBean> mSelectVideo;

    public VideoSelectAdapter(Context context, List<VideoSelectActivity.VideoBean> images) {
        super(context);
        mSelectVideo = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final ImageView mImageView;
        private final CheckBox mCheckBox;
        private final TextView mDurationView;
        private final TextView mSizeView;

        private ViewHolder() {
            super(R.layout.video_select_item);
            mImageView = findViewById(R.id.iv_video_select_image);
            mCheckBox = findViewById(R.id.iv_video_select_check);
            mDurationView = findViewById(R.id.tv_video_select_duration);
            mSizeView = findViewById(R.id.tv_video_select_size);
        }

        @Override
        public void onBindView(int position) {
            VideoSelectActivity.VideoBean bean = getItem(position);
            GlideApp.with(getContext())
                    .load(bean.getVideoPath())
                    .into(mImageView);

            mCheckBox.setChecked(mSelectVideo.contains(getItem(position)));

            // 获取视频的总时长
            mDurationView.setText(PlayerView.conversionTime((int) bean.getVideoDuration()));

            // 获取视频文件大小
            mSizeView.setText(CacheDataManager.getFormatSize(bean.getVideoSize()));
        }
    }

    @Override
    protected RecyclerView.LayoutManager generateDefaultLayoutManager(Context context) {
        return new GridLayoutManager(context, 2);
    }
}