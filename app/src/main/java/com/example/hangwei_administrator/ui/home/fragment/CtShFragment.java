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

        sh_1.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "sh_1");
            bundle.putString("name", "沙河东区第一食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "10:30 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "沙河校区东区食堂一二层");
            bundle.putString("detail", "就餐环境整洁舒适，饭菜品种多样，价格合理，开设“称重自选”窗口，口味丰富，快捷方便。");
            bundle.putInt("picUrl", R.drawable.sh_east1);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        sh_2.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "sh_2");
            bundle.putString("name", "鼓瑟轩");
            bundle.putString("breakfast", "不提供早餐");
            bundle.putString("lunch", "10:30 — 13:30");
            bundle.putString("dinner", "14:30 — 22:00");
            bundle.putString("location", "沙河校区东区食堂三层南侧");
            bundle.putString("detail", "提供中餐、晚餐及夜宵服务，菜品以风味美食为主，种类繁多，并提供点餐服务。");
            bundle.putInt("picUrl", R.drawable.sh_gsx);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        sh_3.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "sh_3");
            bundle.putString("name", "沙河西区清真食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "11:00 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "沙河校区西区食堂B1层");
            bundle.putString("detail", "供早、中、晚餐服务。以民族餐为主，就餐环境优雅、明亮、宽敞，就餐体验良好。");
            bundle.putInt("picUrl", R.drawable.sh_qz);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        sh_4.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "sh_4");
            bundle.putString("name", "沙河西区第一食堂");
            bundle.putString("breakfast", "6:30 — 9:00");
            bundle.putString("lunch", "11:00 — 13:00");
            bundle.putString("dinner", "17:00 — 19:00");
            bundle.putString("location", "沙河校区西区食堂一层");
            bundle.putString("detail", "提供早、中、晚餐服务。基本伙食堂，菜品品种丰富，食美价廉，环境整洁干净，可就餐可自习。");
            bundle.putInt("picUrl", R.drawable.sh_west1);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        sh_5.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "sh_5");
            bundle.putString("name", "沙河西区第二食堂");
            bundle.putString("breakfast", "不提供早餐");
            bundle.putString("lunch", "10:30 — 13:30");
            bundle.putString("dinner", "14:30 — 22:00");
            bundle.putString("location", "沙河校区西区食堂二层");
            bundle.putString("detail", "提供中餐、晚餐及夜宵服务，风味餐饮型食堂，集合各类特色餐饮元素，有益补充基本伙食堂品类。");
            bundle.putInt("picUrl", R.drawable.sh_west2);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        sh_6.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), CanteenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", "sh_6");
            bundle.putString("name", "沙河西区第三食堂");
            bundle.putString("breakfast", "不提供早餐");
            bundle.putString("lunch", "10:00 — 14:00");
            bundle.putString("dinner", "15:00 — 23:00");
            bundle.putString("location", "沙河校区西区食堂三层");
            bundle.putString("detail", "以美食广场集合店的模式为师生提供中餐、晚餐及深夜食堂，用餐环境舒适优雅，菜品种类丰富多样。中庭休闲区绿意盎然，半开放包间为师生提供聚餐、会议、讨论的舒适场所。");
            bundle.putInt("picUrl", R.drawable.sh_west3);
            intent.putExtras(bundle);
            startActivity(intent);
        });
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

    /** 仅用于处理图片 */
    public void previewImage(int idx) {
        ImagePreviewActivity.start(getAttachActivity(), uris, idx);
    }
}
