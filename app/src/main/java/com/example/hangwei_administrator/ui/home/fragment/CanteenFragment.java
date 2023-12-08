package com.example.hangwei_administrator.ui.home.fragment;

import android.content.Intent;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppFragment;
import com.example.hangwei_administrator.app.TitleBarFragment;
import com.example.hangwei_administrator.base.FragmentPagerAdapter;
import com.example.hangwei_administrator.ui.home.activity.HomeActivity;
import com.example.hangwei_administrator.ui.home.activity.SearchActivity;
import com.example.hangwei_administrator.ui.home.adapter.TabAdapter;
import com.example.hangwei_administrator.widget.layout.XCollapsingToolbarLayout;
import com.gyf.immersionbar.ImmersionBar;

public class CanteenFragment extends TitleBarFragment<HomeActivity>
        implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private XCollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppCompatImageView bgImage;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private ViewPager mViewPager;
    private RecyclerView mTabView;
    private TabAdapter mTabAdapter;
    private Toolbar mToolbar;
    private TextView mSearchBox;

    public static CanteenFragment newInstance() {
        return new CanteenFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_canteen;
    }

    @Override
    protected void initView() {
        mSearchBox = findViewById(R.id.canteen_searchBox);
        mToolbar = findViewById(R.id.tb_canteen_title);
        // 点击 toolbar 的图片收回键盘
        findViewById(R.id.canteen_bg).setOnClickListener(v -> {
            hideKeyboard(getView());
        });
        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(getAttachActivity(), mToolbar);

        mSearchBox.setOnClickListener(v -> {
            // 跳转到SearchActivity
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

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
        getStatusBarConfig().statusBarDarkFont(shown).init();
        mSearchBox.setBackgroundResource(shown ? R.drawable.home_search_bar_gray_bg : R.drawable.home_search_bar_transparent_bg);
        mSearchBox.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black60 : R.color.black));
    }
}
