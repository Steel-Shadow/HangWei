package com.example.hangwei_administrator.ui.home.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.app.TitleBarFragment;
import com.example.hangwei_administrator.ui.home.activity.CanteenActivity;
import com.example.hangwei_administrator.ui.home.activity.ImagePreviewActivity;

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

        xyl_1.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_1");
            bundle.putString("name", "学一食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "10:30 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "学院路校区合一楼二层");
            bundle.putString("detail", "担心菜多吃不下？二层的小碗菜帮你解决问题！排队时间短，各种精致小炒供你选择！");
            bundle.putInt("picUrl", R.drawable.xyl_ct1);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_2.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_2");
            bundle.putString("name", "学四食堂");
            bundle.putString("breakfast", "不提供早餐");
            bundle.putString("lunch", "10:30 — 14:00");
            bundle.putString("dinner", "17:00 — 21:00");
            bundle.putString("location", "学院路校区合一楼三层");
            bundle.putString("detail", "开放时间长，种类丰富，具有多种和风味菜系，好吃不贵！如果不知道该吃什么，就来学四吃一碗肉末蛋包饭吧！");
            bundle.putInt("picUrl", R.drawable.xyl_ct4);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_3.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_3");
            bundle.putString("name", "清真食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "10:30 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "学院路校区合一楼四层南侧");
            bundle.putString("detail", "牛羊肉菜品一绝, 尊重同学们不同的饮食习惯。");
            bundle.putInt("picUrl", R.drawable.xyl_qz);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_4.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_4");
            bundle.putString("name", "合一厅");
            bundle.putString("breakfast", "不提供早餐");
            bundle.putString("lunch", "10:30 — 13:30");
            bundle.putString("dinner", "17:00 — 20:00");
            bundle.putString("location", "学院路校区合一楼四层北侧");
            bundle.putString("detail", "就餐环境舒适幽雅，饭菜口味丰富，同时设有“称重自选”售饭模式，师生可按需购餐。");
            bundle.putInt("picUrl", R.drawable.xyl_hyt);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_5.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_5");
            bundle.putString("name", "学二食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "10:30 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "学院路校区东区食堂一层");
            bundle.putString("detail", "为师生提供早、中、晚餐服务，菜品价廉物美、靠近教学区，下课后来这里再方便不过了。常有“航味”出自这里！");
            bundle.putInt("picUrl", R.drawable.xyl_ct2);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_6.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_6");
            bundle.putString("name", "教工食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "11:00 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "学院路校区东区食堂二层");
            bundle.putString("detail", "环境优雅、服务热情。菜品种类丰富齐全。");
            bundle.putInt("picUrl", R.drawable.xyl_jg);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_7.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_7");
            bundle.putString("name", "学三食堂");
            bundle.putString("breakfast", "不提供早餐");
            bundle.putString("lunch", "10:00 — 15:00");
            bundle.putString("dinner", "17:00 — 23:00");
            bundle.putString("location", "学院路校区北区食堂B1层");
            bundle.putString("detail", "种类丰富，满足不同口味！深夜食堂，犒劳饥饿的自己！新北负一的手撕鸡永远可以一试！");
            bundle.putInt("picUrl", R.drawable.xyl_ct3);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_8.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_8");
            bundle.putString("name", "学五食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "10:30 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "学院路校区北区食堂一层");
            bundle.putString("detail", "用餐环境舒适优雅，菜品种类丰富多样，量足价优，电子屏幕滚动显示菜价。");
            bundle.putInt("picUrl", R.drawable.xyl_ct5);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        xyl_9.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "xyl_9");
            bundle.putString("name", "学六食堂");
            bundle.putString("breakfast", "不提供早餐");
            bundle.putString("lunch", "10:00 — 14:00");
            bundle.putString("dinner", "17:00 — 21:00");
            bundle.putString("location", "学院路校区北区食堂二层");
            bundle.putString("detail", "以自助餐厅的形式为师生提供中餐、晚餐及夜宵服务，用餐环境舒适优雅，菜品种类丰富多样。");
            bundle.putInt("picUrl", R.drawable.xyl_ct6);
            intent.putExtras(bundle);
            startActivity(intent);
        });
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

    public void previewImage(int idx) {
        ImagePreviewActivity.start(getAttachActivity(), uris, idx);
    }
}
