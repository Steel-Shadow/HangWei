package com.example.hangwei.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.dishInfo.CommentFragment;
import com.example.hangwei.ui.dishInfo.SideDishFragment;
import com.example.hangwei.ui.home.adapter.TabAdapter;
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
public final class DishInfoActivity extends BaseActivity
        implements TabAdapter.OnTabListener,
        ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private String mDishId;
    private TextView mName;
    private TextView mPrice;
    private ImageView mPic;
    private boolean mHasFavorite; // 是否收藏了
    private TextView mFavorite; // 收藏

    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<BaseFragment<?>> mPagerAdapter;

    public static DishInfoActivity newInstance() {
        return new DishInfoActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dish_info_activity;
    }

    @Override
    protected void initView() {
        mName = findViewById(R.id.dish_info_name);
        mPrice = findViewById(R.id.dish_info_price);
        mFavorite = findViewById(R.id.dish_info_favorite);
        mPic = findViewById(R.id.dish_info_pic);

        mTabView = findViewById(R.id.dish_info_middle);
        mViewPager = findViewById(R.id.dish_info_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(CommentFragment.newInstance(), "评价");
        mPagerAdapter.addFragment(SideDishFragment.newInstance(), "餐品搭配");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(this.getContext());
        mTabView.setAdapter(mTabAdapter);

        mFavorite.setOnClickListener(this::clickFavorite);
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("评价");
        mTabAdapter.addItem("餐品搭配");
        mTabAdapter.setOnTabListener(this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mDishId = bundle.getString("id");
        mName.setText(bundle.getString("name"));
        mPrice.setText(String.format(Locale.CHINA, "%d", bundle.getInt("price")));
        Glide.with(this).load(bundle.getString("picUrl")).into(mPic);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", "todo: userIdShouldBeHere"); // todo: 与wyj协商确定userId获取
        params.put("dishId", mDishId);
        AsyncHttpUtil.httpPost(Ports.checkFavorite, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("读取收藏信息失败", ToastConst.successStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        mHasFavorite = data.getBoolean("isFavorite");
                        Drawable newStar;
                        if (mHasFavorite) {
                            newStar = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rating_star_fill, null);
                        } else {
                            newStar = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rating_star_off, null);
                        }
                        runOnUiThread(() -> mFavorite.setCompoundDrawablesWithIntrinsicBounds(null, newStar, null, null));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void clickFavorite(View v) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", "todo: userIdShouldBeHere"); // todo: 与wyj协商确定userId获取
        params.put("dishId", mDishId);
        AsyncHttpUtil.httpPost(Ports.favorite, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("收藏失败", ToastConst.successStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        Drawable newStar;
                        if (mHasFavorite) {
                            newStar = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rating_star_off, null);
                        } else {
                            newStar = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rating_star_fill, null);
                        }
                        mHasFavorite = !mHasFavorite;
                        runOnUiThread(() -> mFavorite.setCompoundDrawablesWithIntrinsicBounds(null, newStar, null, null));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}