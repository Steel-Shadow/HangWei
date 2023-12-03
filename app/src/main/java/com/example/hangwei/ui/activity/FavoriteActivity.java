package com.example.hangwei.ui.activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei.R;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.ui.adapter.TabAdapter;
import com.example.hangwei.ui.fragment.FavoriteFragment;

public class FavoriteActivity extends BaseActivity
        implements TabAdapter.OnTabListener,
        ViewPager.OnPageChangeListener {
    private RecyclerView mTabView;
    private TabAdapter mTabAdapter;
    private ViewPager mViewPager;
    private FragmentPagerAdapter<BaseFragment<?>> mPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.favorite_activity;
    }

    @Override
    protected void initView() {
        mTabView = findViewById(R.id.favorite_choose_tab);
        mViewPager = findViewById(R.id.favorite_pager);


        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(FavoriteFragment.newInstance("食堂"), "食堂");
        mPagerAdapter.addFragment(FavoriteFragment.newInstance("窗口"), "窗口");
        mPagerAdapter.addFragment(FavoriteFragment.newInstance("菜品"), "菜品");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(this);
        mTabView.setAdapter(mTabAdapter);
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("食堂");
        mTabAdapter.addItem("窗口");
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
