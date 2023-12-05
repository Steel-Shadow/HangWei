package com.example.hangwei.ui.home.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.ui.commu.friends.detail.FriendDetailActivity;
import com.example.hangwei.ui.home.activity.CanteenActivity;
import com.example.hangwei.ui.home.activity.ImagePreviewActivity;

import java.util.ArrayList;

public class CtXylFragment extends TitleBarFragment<AppActivity> {
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ImageView imageView7;
    private ImageView imageView8;
    private ImageView imageView9;
    private ArrayList<Integer> drIDs;
    private ArrayList<String> uris;

    private View xyl_1;
    private View xyl_2;
    private View xyl_3;
    private View xyl_4;
    private View xyl_5;
    private View xyl_6;
    private View xyl_7;
    private View xyl_8;
    private View xyl_9;

    public static CtXylFragment newInstance() {
        return new CtXylFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ctxyl;
    }

    @Override
    protected void initView() {
        imageView1 = findViewById(R.id.ctxyl_image_ct1);
        imageView2 = findViewById(R.id.ctxyl_image_ct4);
        imageView3 = findViewById(R.id.ctxyl_image_qz);
        imageView4 = findViewById(R.id.ctxyl_image_hy);
        imageView5 = findViewById(R.id.ctxyl_image_ct2);
        imageView6 = findViewById(R.id.ctxyl_image_jg);
        imageView7 = findViewById(R.id.ctxyl_image_ct3);
        imageView8 = findViewById(R.id.ctxyl_image_ct5);
        imageView9 = findViewById(R.id.ctxyl_image_ct6);
        buildUris();
        imageView1.setOnClickListener(view -> previewImage(0));
        imageView2.setOnClickListener(view -> previewImage(1));
        imageView3.setOnClickListener(view -> previewImage(2));
        imageView4.setOnClickListener(view -> previewImage(3));
        imageView5.setOnClickListener(view -> previewImage(4));
        imageView6.setOnClickListener(view -> previewImage(5));
        imageView7.setOnClickListener(view -> previewImage(6));
        imageView8.setOnClickListener(view -> previewImage(7));
        imageView9.setOnClickListener(view -> previewImage(8));

        xyl_1 = findViewById(R.id.ctxyl_ct1);
        xyl_2 = findViewById(R.id.ctxyl_ct4);
        xyl_3 = findViewById(R.id.ctxyl_qz);
        xyl_4 = findViewById(R.id.ctxyl_hy);
        xyl_5 = findViewById(R.id.ctxyl_ct2);
        xyl_6 = findViewById(R.id.ctxyl_jg);
        xyl_7 = findViewById(R.id.ctxyl_ct3);
        xyl_8 = findViewById(R.id.ctxyl_ct5);
        xyl_9 = findViewById(R.id.ctxyl_ct6);

        xyl_1.setOnClickListener(view -> enterCanteen("xyl_1"));
        xyl_2.setOnClickListener(view -> enterCanteen("xyl_2"));
        xyl_3.setOnClickListener(view -> enterCanteen("xyl_3"));
        xyl_4.setOnClickListener(view -> enterCanteen("xyl_4"));
        xyl_5.setOnClickListener(view -> enterCanteen("xyl_5"));
        xyl_6.setOnClickListener(view -> enterCanteen("xyl_6"));
        xyl_7.setOnClickListener(view -> enterCanteen("xyl_7"));
        xyl_8.setOnClickListener(view -> enterCanteen("xyl_8"));
        xyl_9.setOnClickListener(view -> enterCanteen("xyl_9"));
    }

    @Override
    protected void initData() {

    }

    public void buildUris() {
        drIDs = new ArrayList<>();
        drIDs.add(R.drawable.xyl_ct1);
        drIDs.add(R.drawable.xyl_ct4);
        drIDs.add(R.drawable.xyl_qz);
        drIDs.add(R.drawable.xyl_hyt);
        drIDs.add(R.drawable.xyl_ct2);
        drIDs.add(R.drawable.xyl_jg);
        drIDs.add(R.drawable.xyl_ct3);
        drIDs.add(R.drawable.xyl_ct5);
        drIDs.add(R.drawable.xyl_ct6);

        uris = new ArrayList<>();
        Resources r = this.getResources();
        for (Integer id : drIDs) {
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + r.getResourcePackageName(id) + "/"
                    + r.getResourceTypeName(id) + "/"
                    + r.getResourceEntryName(id));
            uris.add(uri.toString());
        }
    }

    public void enterCanteen(String id) {
        Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void previewImage(int idx) {
        ImagePreviewActivity.start(getAttachActivity(), uris, idx);
    }
}
