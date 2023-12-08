package com.example.hangwei_administrator.ui.home.activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.base.BaseFragment;
import com.example.hangwei_administrator.base.FragmentPagerAdapter;
import com.example.hangwei_administrator.ui.home.adapter.TabAdapter;
import com.example.hangwei_administrator.ui.home.fragment.FavCanteenFragment;
import com.example.hangwei_administrator.ui.home.fragment.FavDishFragment;

public class FavoriteActivity extends AppActivity
        implements TabAdapter.OnTabListener,
        ViewPager.OnPageChangeListener {
    private RecyclerView mTabView;
    private TabAdapter mTabAdapter;
    private ViewPager mViewPager;
    private FragmentPagerAdapter<BaseFragment<?>> mPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_favorite;
    }

    @Override
    protected void initView() {
        mTabView = findViewById(R.id.favorite_choose_tab);
        mViewPager = findViewById(R.id.favorite_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(FavCanteenFragment.newInstance(), "食堂");
        mPagerAdapter.addFragment(FavDishFragment.newInstance(), "菜品");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(this);
        mTabView.setAdapter(mTabAdapter);
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("食堂");
        mTabAdapter.addItem("餐品");
        mTabAdapter.setOnTabListener(this);
    }

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
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }
}
