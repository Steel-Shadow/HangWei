package com.example.hangwei_administrator.ui.home.activity;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.data.glide.GlideApp;

/**
 *    desc   : 关于界面
 */
public final class AboutActivity extends AppActivity {
    private ImageView author1;
    private ImageView author2;
    private ImageView author3;
    private ImageView author4;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        author1 = findViewById(R.id.author1);
        author2 = findViewById(R.id.author2);
        author3 = findViewById(R.id.author3);
        author4 = findViewById(R.id.author4);

        GlideApp.with(getActivity())
                .load(Uri.parse("https://vip.helloimg.com/i/2023/12/08/657213c23914a.jpeg"))
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(author1);
        GlideApp.with(getActivity())
                .load(Uri.parse("https://vip.helloimg.com/i/2023/12/07/657192c5b640f.jpeg"))
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(author2);
        GlideApp.with(getActivity())
                .load(Uri.parse("https://vip.helloimg.com/i/2023/12/07/6571e74be2e2a.webp"))
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(author3);
        GlideApp.with(getActivity())
                .load(Uri.parse("https://vip.helloimg.com/i/2023/12/08/6572cd8f409fa.png"))
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(author4);
    }

    @Override
    protected void initData() {}
}