package com.example.hangwei.ui.home.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei.R;
import com.example.hangwei.app.AppFragment;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.base.FragmentPagerAdapter;
import com.example.hangwei.ui.home.activity.DishInfoActivity;
import com.example.hangwei.ui.home.activity.HomeActivity;
import com.example.hangwei.ui.home.activity.SearchActivity;
import com.example.hangwei.widget.layout.NestedViewPager;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;
import com.gyf.immersionbar.ImmersionBar;

/**
 * desc   : 首页 Fragment
 */
public final class HomeFragment extends TitleBarFragment<HomeActivity>
        implements AdapterView.OnItemSelectedListener,
        ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {

    /*-Toolbar 渐变伸缩---------------------------------------------------------*/
    private XCollapsingToolbarLayout mCollapsingToolbarLayout; // 渐变动态布局
    private Toolbar mToolbar; // 工具栏
    private TextView mAddressView; // 地址
    private TextView mSearchBox; // 搜索栏，点击下方图片空白收回键盘

    /*-中间选择栏---------------------------------------------------------*/
    private Spinner mSpinner;
    ArrayAdapter<CharSequence> mArrayAdapter;

    /*- 菜品展示列表 ---------------------------------------------------------*/
    DishFragment dishFragment;
    private NestedViewPager mViewPager; // 下方展示区，可左右切换，目前只有一个，可直接改为 StatusFragment
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter; // 下方展示区的适配器，目前只有一个

    /*----------------------------------------------------------*/
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        mCollapsingToolbarLayout = findViewById(R.id.ctl_home_bar);
        mToolbar = findViewById(R.id.tb_home_title);

        mAddressView = findViewById(R.id.home_address);
        mSearchBox = findViewById(R.id.home_searchBox);

        mSpinner = findViewById(R.id.home_spinner);
        mViewPager = findViewById(R.id.home_pager_lists);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        dishFragment = DishFragment.newInstance();
        mPagerAdapter.addFragment(dishFragment, "餐品列表");

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        // 点击 toolbar 的图片收回键盘
        findViewById(R.id.home_imageView).setOnClickListener(v -> {
            hideKeyboard(getView());
        });

        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(getAttachActivity(), mToolbar);

        //设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);

        mAddressView.setOnClickListener(v -> {
            if (mAddressView.getText().equals("学院路")) {
                mAddressView.setText("沙河");
                dishFragment.setCampus("沙河");
            } else {
                mAddressView.setText("学院路");
                dishFragment.setCampus("学院路");
            }
            dishFragment.refresh();
        });

        mSearchBox.setOnClickListener(v -> {
            // 跳转到SearchActivity
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // [0: 全部]，[1：早餐]，[2：正餐]，[3：饮料]
                dishFragment.setType(position);
                dishFragment.refresh();
            }

            //只有当patent中的资源没有时，调用此方法
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void initData() {
        mArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.home_spinner_array, R.layout.home_spinner_view);
        mArrayAdapter.setDropDownViewResource(R.layout.home_spinner_list_item);

        mSpinner.setAdapter(mArrayAdapter);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mViewPager.setCurrentItem(position);
        // 无需切换
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
        getStatusBarConfig().statusBarDarkFont(shown).init();
        mAddressView.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black : R.color.black));
        mSearchBox.setBackgroundResource(shown ? R.drawable.home_search_bar_gray_bg : R.drawable.home_search_bar_transparent_bg);
        mSearchBox.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black60 : R.color.black));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);

        mSpinner.setAdapter(null);
    }
}