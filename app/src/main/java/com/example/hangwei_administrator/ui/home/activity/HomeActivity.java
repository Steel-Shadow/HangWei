package com.example.hangwei_administrator.ui.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.app.AppFragment;
import com.example.hangwei_administrator.base.BaseFragment;
import com.example.hangwei_administrator.base.FragmentPagerAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.other.DoubleClickHelper;
import com.example.hangwei_administrator.ui.commu.posts.list.PostsListFragment;
import com.example.hangwei_administrator.ui.home.adapter.NavigationAdapter;
import com.example.hangwei_administrator.ui.home.fragment.CanteenFragment;
import com.example.hangwei_administrator.ui.home.fragment.HomeFragment;
import com.example.hangwei_administrator.ui.home.fragment.MineFragment;
import com.example.hangwei_administrator.ui.manage.user.UserManageFragment;
import com.example.hangwei_administrator.utils.ToastUtil;

/**
 * desc   : 首页界面
 */
public final class HomeActivity extends AppActivity
        implements NavigationAdapter.OnNavigationListener {

    private static final String INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex";
    private static final String INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass";

    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private RecyclerView mNavigationView;
    private NavigationAdapter mNavigationAdapter;

    public static void start(Context context) {
        start(context, HomeFragment.class);
    }

    public static void start(Context context, Class<? extends BaseFragment<?>> fragmentClass) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.home_page);
        mNavigationView = findViewById(R.id.rv_home_navigation);

        mNavigationAdapter = new NavigationAdapter(this);
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem("用户管理",
                ContextCompat.getDrawable(this, R.drawable.user_manage)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem("餐品管理",
                ContextCompat.getDrawable(this, R.drawable.dish_manage)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem("社区管理",
                ContextCompat.getDrawable(this, R.drawable.commu_manage)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_me),
                ContextCompat.getDrawable(this, R.drawable.mine_manage)));
        mNavigationAdapter.setOnNavigationListener(this);
        mNavigationView.setAdapter(mNavigationAdapter);
    }

    @Override
    protected void initData() {
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(UserManageFragment.newInstance());
        mPagerAdapter.addFragment(CanteenFragment.newInstance());
        mPagerAdapter.addFragment(PostsListFragment.newInstance());
        mPagerAdapter.addFragment(MineFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);

        // 创建一个 OnBackPressedCallback 对象来处理后退按钮事件
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 如果要执行后退操作，可以调用finish()或者navigateUp()等方法
                if (!DoubleClickHelper.isOnDoubleClick()) {
                    ToastUtil.toast("再按一次退出", ToastConst.hintStyle);
                    return;
                }

                // 移动到上一个任务栈，避免侧滑引起的不良反应
                moveTaskToBack(false);
                postDelayed(() -> {
                    // 返回桌面，不关闭程序
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(homeIntent);
                }, 300);
            }
        };
        // 将回调添加到 OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchFragment(mPagerAdapter.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前 Fragment 索引位置
        outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX));
    }

    private void switchFragment(int fragmentIndex) {
        if (fragmentIndex == -1) {
            return;
        }

        switch (fragmentIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(fragmentIndex);
                mNavigationAdapter.setSelectedPosition(fragmentIndex);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(position);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mNavigationView.setAdapter(null);
        mNavigationAdapter.setOnNavigationListener(null);
    }
}