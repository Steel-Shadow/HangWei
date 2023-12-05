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
import com.example.hangwei.ui.home.activity.CanteenActivity;
import com.example.hangwei.ui.home.activity.ImagePreviewActivity;

import java.util.ArrayList;

public class CtShFragment extends TitleBarFragment<AppActivity> {
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ArrayList<Integer> drIDs;
    private ArrayList<String> uris;

    private View sh_1;
    private View sh_2;
    private View sh_3;
    private View sh_4;
    private View sh_5;
    private View sh_6;


    public static CtShFragment newInstance() {
        return new CtShFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ctsh;
    }

    @Override
    protected void initView() {
        imageView1 = findViewById(R.id.ctsh_image_east1);
        imageView2 = findViewById(R.id.ctsh_image_gsx);
        imageView3 = findViewById(R.id.ctsh_image_qz);
        imageView4 = findViewById(R.id.ctsh_image_west1);
        imageView5 = findViewById(R.id.ctsh_image_west2);
        imageView6 = findViewById(R.id.ctsh_image_west3);
        buildUris();
        imageView1.setOnClickListener(view -> previewImage(0));
        imageView2.setOnClickListener(view -> previewImage(1));
        imageView3.setOnClickListener(view -> previewImage(2));
        imageView4.setOnClickListener(view -> previewImage(3));
        imageView5.setOnClickListener(view -> previewImage(4));
        imageView6.setOnClickListener(view -> previewImage(5));


        sh_1 = findViewById(R.id.ctsh_east1);
        sh_2 = findViewById(R.id.ctsh_gsx);
        sh_3 = findViewById(R.id.ctsh_qz);
        sh_4 = findViewById(R.id.ctsh_west1);
        sh_5 = findViewById(R.id.ctsh_west2);
        sh_6 = findViewById(R.id.ctsh_west3);

        sh_1.setOnClickListener(view -> enterCanteen("sh_1"));
        sh_2.setOnClickListener(view -> enterCanteen("sh_2"));
        sh_3.setOnClickListener(view -> enterCanteen("sh_3"));
        sh_4.setOnClickListener(view -> enterCanteen("sh_4"));
        sh_5.setOnClickListener(view -> enterCanteen("sh_5"));
        sh_6.setOnClickListener(view -> enterCanteen("sh_6"));
    }

    @Override
    protected void initData() {

    }

    public void buildUris() {
        drIDs = new ArrayList<>();
        drIDs.add(R.drawable.sh_east1);
        drIDs.add(R.drawable.sh_gsx);
        drIDs.add(R.drawable.sh_qz);
        drIDs.add(R.drawable.sh_west1);
        drIDs.add(R.drawable.sh_west2);
        drIDs.add(R.drawable.sh_west3);

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

    /** 仅用于处理图片 */
    public void previewImage(int idx) {
        ImagePreviewActivity.start(getAttachActivity(), uris, idx);
    }
}
