package com.example.hangwei.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.app.AppFragment;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.dishInfo.CommentFragment;
import com.example.hangwei.ui.dishInfo.MatchDishFragment;
import com.example.hangwei.ui.home.adapter.TabAdapter;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

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
    private XCollapsingToolbarLayout mCollapsingToolbarLayout; // 渐变动态布局
    private String mDishId;
    private TextView mName;
    private TextView mPrice;
    private boolean mHasFavorite; // 是否收藏了
    private TextView mFavorite; // 收藏

    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    public static DishInfoActivity newInstance() {
        return new DishInfoActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dish_info_activity;
    }

    @Override
    protected void initView() {
        mCollapsingToolbarLayout = findViewById(R.id.dish_info_bar);

        mName = findViewById(R.id.dish_info_name);
        mPrice = findViewById(R.id.dish_info_price);
        mFavorite = findViewById(R.id.dish_info_favorite);

        mTabView = findViewById(R.id.dish_info_middle);
        mViewPager = findViewById(R.id.dish_info_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(CommentFragment.newInstance(), "评价");
        mPagerAdapter.addFragment(MatchDishFragment.newInstance(), "餐品搭配");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(this.getContext());
        mTabView.setAdapter(mTabAdapter);

        //设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);

        mFavorite.setOnClickListener(this::clickFavorite);
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("评价");
        mTabAdapter.addItem("餐品搭配");
        mTabAdapter.setOnTabListener(this);

        Intent intent = getIntent();
        String dishId = intent.getStringExtra("dishId");

        HashMap<String, Object> params = new HashMap<>();
        params.put("dishId", dishId);
        AsyncHttpUtil.httpPostForObject(Ports.dishComment, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("餐品详情获取失败，请稍候再试", ToastConst.warnStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    Fragment fragment = mPagerAdapter.getShowFragment();
                    runOnUiThread(() -> {

                    });
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
                        Drawable star;
                        if (mHasFavorite) {
                            star = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rating_star_off, null);
                        } else {
                            star = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rating_star_fill, null);
                        }
                        mHasFavorite = !mHasFavorite;
                        assert star != null;
                        runOnUiThread(() -> mFavorite.setCompoundDrawablesWithIntrinsicBounds(null, star, null, null));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}