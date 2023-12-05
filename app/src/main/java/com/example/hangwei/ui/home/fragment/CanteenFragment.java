package com.example.hangwei.ui.home.fragment;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei.R;
import com.example.hangwei.app.AppFragment;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.ui.home.activity.HomeActivity;
import com.example.hangwei.ui.home.adapter.TabAdapter;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;

public class CanteenFragment extends TitleBarFragment<HomeActivity>
        implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private XCollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppCompatImageView bgImage;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private ViewPager mViewPager;
    private RecyclerView mTabView;
    private TabAdapter mTabAdapter;

    public static CanteenFragment newInstance() {
        return new CanteenFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_canteen;
    }

    @Override
    protected void initView() {
        mCollapsingToolbarLayout = findViewById(R.id.canteen_bar);
        mTabView = findViewById(R.id.canteen_tab);
        mViewPager = findViewById(R.id.canteen_list);
        bgImage = findViewById(R.id.canteen_bg);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(CtXylFragment.newInstance(), "学院路");
        mPagerAdapter.addFragment(CtShFragment.newInstance(), "沙河");

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(getAttachActivity());
        mTabView.setAdapter(mTabAdapter);

        // 设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem("学院路");
        mTabAdapter.addItem("沙河");
        mTabAdapter.setOnTabListener(this);
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public boolean isStatusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        if (position == 0) {
            bgImage.setImageResource(R.drawable.xyl);
        } else {
            bgImage.setImageResource(R.drawable.sh);
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    /**
     * {@link TabAdapter.OnTabListener}
     */
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

    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {

    }
}
