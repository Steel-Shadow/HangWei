package com.example.hangwei.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.ui.home.adapter.TabAdapter;
import com.example.hangwei.ui.home.dishInfo.CommentFragment;
import com.example.hangwei.ui.home.dishInfo.SideDishFragment;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;

/**
 * desc   : 食堂展示页
 */
public final class CanteenActivity extends BaseActivity
        implements TabAdapter.OnTabListener,
        ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private String mCanteen;
    private ImageView mCanteenPic; //todo: 确定食堂图片
    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<BaseFragment<?>> mPagerAdapter;

    public static CanteenActivity newInstance() {
        return new CanteenActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dish_info_activity;
    }

    @Override
    protected void initView() {
        mTabView = findViewById(R.id.dish_info_middle);
        mViewPager = findViewById(R.id.dish_info_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(CommentFragment.newInstance(), "评价");
        mPagerAdapter.addFragment(SideDishFragment.newInstance(), "餐品搭配");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(this.getContext());
        mTabView.setAdapter(mTabAdapter);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        mCanteen = bundle.getString("canteen");
        Glide.with(this).load(bundle.getString("canteen")).into(mCanteenPic); //todo:王艺杰传过来食堂信息

//        mTabAdapter.addItem("评价");
//        mTabAdapter.addItem("餐品搭配");
//        mTabAdapter.setOnTabListener(this);
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
}