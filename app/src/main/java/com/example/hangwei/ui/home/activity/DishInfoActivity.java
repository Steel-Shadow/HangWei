package com.example.hangwei.ui.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.ui.home.element.Favorite;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.adapter.TabAdapter;
import com.example.hangwei.ui.home.fragment.CommentFragment;
import com.example.hangwei.ui.home.fragment.SideDishFragment;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * desc   : 餐品详情页
 */
public final class DishInfoActivity extends AppActivity
        implements TabAdapter.OnTabListener,
        ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private String userName;
    private boolean favored;
    private int favorCnt;

    private String mDishId;
    private TextView mName;
    private TextView mPrice;
    private ImageView mPic;
    public Favorite mFavorite;
    private ToggleButton btn_favor;
    private TextView dish_favorCnt;

    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<BaseFragment<?>> mPagerAdapter;
    private Intent resIntent = new Intent();

    public static DishInfoActivity newInstance() {
        return new DishInfoActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dish_info;
    }

    @Override
    protected void initView() {
        mName = findViewById(R.id.dish_info_name);
        mPrice = findViewById(R.id.dish_info_price);
        mPic = findViewById(R.id.dish_info_pic);

        mTabView = findViewById(R.id.dish_info_middle);
        mViewPager = findViewById(R.id.dish_info_pager);

        btn_favor = findViewById(R.id.dish_info_favor);
        dish_favorCnt = findViewById(R.id.dish_info_favor_count);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(CommentFragment.newInstance(), "评价");
        mPagerAdapter.addFragment(SideDishFragment.newInstance(), "餐品搭配");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(this.getContext());
        mTabView.setAdapter(mTabAdapter);

        btn_favor.setOnClickListener(view -> doFavor());
    }

    @Override
    protected void initData() {
        userName = getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedName", null);

        mTabAdapter.addItem("评价");
        mTabAdapter.addItem("餐品搭配");
        mTabAdapter.setOnTabListener(this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mDishId = bundle.getString("id");
        mName.setText(bundle.getString("name"));
        mPrice.setText(bundle.getString("price"));
        setFavor();
        favorCnt = bundle.getInt("favorCnt");
        dish_favorCnt.setText(String.valueOf(favorCnt));
        Glide.with(this).load(bundle.getString("picUrl")).into(mPic);
        mFavorite = new Favorite(this, findViewById(R.id.dish_info_favorite), mDishId, Ports.dishFavChange, Ports.dishFavCheck);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null"));
        params.put("dishId", mDishId);
        AsyncHttpUtil.httpPost(Ports.dishHistoryAdd, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Add history http fail!", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            }
        });
    }

    /**
     * {@link TabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }

    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {

    }

    public String getDishId() {
        return mDishId;
    }

    public void setFavor() {
        System.out.println(mDishId);
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("dishID", mDishId);

        AsyncHttpUtil.httpPost(Ports.dishIsUp, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("服务器有一些小问题~", ToastConst.warnStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        runOnUiThread(() -> {
                            try {
                                favored = jsonObject.getJSONObject("data").getBoolean("isThumbUp");
                                btn_favor.setChecked(favored);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        response.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    public void doFavor() {
        btn_favor.setChecked(favored);

        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("dishID", mDishId);
        params.put("isThumbUp", String.valueOf(!favored));
        System.out.println(params);

        AsyncHttpUtil.httpPost(Ports.dishUp, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("服务器有一些小问题~", ToastConst.warnStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        runOnUiThread(() -> {
                            favored = !favored;
                            btn_favor.setChecked(favored);
                            favorCnt = favored ? favorCnt + 1 : favorCnt - 1;
                            dish_favorCnt.setText(String.valueOf(favorCnt));
                            if (favored) {
                                ToastUtil.toast("已收到客官的赞啦~", ToastConst.successStyle);
                            } else {
                                ToastUtil.toast("已取赞，我们会继续努力~", ToastConst.warnStyle);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    @Override
    public void finish() {
        resIntent.putExtra("favorCnt", favorCnt);
        setResult(Activity.RESULT_OK, resIntent);
        super.finish();
    }
}