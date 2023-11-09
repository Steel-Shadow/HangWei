package com.example.hangwei.ui.home.fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei.app.AppFragment;
import com.example.hangwei.widget.view.ClearEditText;
import com.gyf.immersionbar.ImmersionBar;
import com.example.hangwei.R;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;

import com.example.hangwei.ui.home.HomeActivity;

/**
 * desc   : 首页 Fragment
 */
public final class HomeFragment extends TitleBarFragment<HomeActivity>
        implements  AdapterView.OnItemSelectedListener,
        ViewPager.OnPageChangeListener,
//        TabAdapter.OnTabListener,
        XCollapsingToolbarLayout.OnScrimsListener {

    /*-Toolbar 渐变伸缩---------------------------------------------------------*/
    private XCollapsingToolbarLayout mCollapsingToolbarLayout; // 渐变动态布局
    private Toolbar mToolbar; // 工具栏
    private TextView mAddressView; // 地址
    private ClearEditText mSearchBox; // 搜索栏 todo:如何收起键盘
    private AppCompatImageButton mFilterButton; // todo:筛选功能

    /*-中间选择栏---------------------------------------------------------*/
    private Spinner mSpinner;
    ArrayAdapter<CharSequence> mArrayAdapter;

    /*- 菜品展示列表 ---------------------------------------------------------*/
    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    /*----------------------------------------------------------*/
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {
        mCollapsingToolbarLayout = findViewById(R.id.ctl_home_bar);
        mToolbar = findViewById(R.id.tb_home_title);

        mAddressView = findViewById(R.id.home_address);
        mSearchBox = findViewById(R.id.home_searchBox);
        mFilterButton = findViewById(R.id.iv_home_search);

        mSpinner = findViewById(R.id.home_spinner);
        mViewPager = findViewById(R.id.home_pager_lists);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(StatusFragment.newInstance(), "餐品列表"); // todo:切换早中晚餐

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(getAttachActivity(), mToolbar);

        //设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);
    }

    @Override
    protected void initData() {
        mArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.home_spinner_array, R.layout.home_spinner_item);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(mArrayAdapter);
        mSpinner.setOnItemSelectedListener(this);
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

    //设置 mViewPager 的页数
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
//        if (mTabAdapter == null) {
//            return;
//        }
//        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * CollapsingToolbarLayout 渐变回调
     * {@link XCollapsingToolbarLayout.OnScrimsListener}
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {
        // todo: 修改下拉、回弹颜色修改
        getStatusBarConfig().statusBarDarkFont(shown).init();
        mAddressView.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black : R.color.black));
        mSearchBox.setBackgroundResource(shown ? R.drawable.home_search_bar_gray_bg : R.drawable.home_search_bar_transparent_bg);
        mSearchBox.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black60 : R.color.black));
        mFilterButton.setSupportImageTintList(ColorStateList.valueOf(getColor(shown ? R.color.common_icon_color : R.color.black)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);

        mSpinner.setAdapter(null);
    }
}