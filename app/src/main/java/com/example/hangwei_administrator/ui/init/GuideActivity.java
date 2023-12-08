package com.example.hangwei_administrator.ui.init;

import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import me.relex.circleindicator.CircleIndicator3;

public class GuideActivity extends AppActivity {
    private ViewPager2 mViewPager;
    private CircleIndicator3 mIndicatorView;
    private View mCompleteView;

    private GuideAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_guide_pager);
        mIndicatorView = findViewById(R.id.cv_guide_indicator);
        mCompleteView = findViewById(R.id.btn_guide_complete);

        mCompleteView.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(GuideActivity.this, LoginActivity.class));
        });
    }

    @Override
    protected void initData() {
        mAdapter = new GuideAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.registerOnPageChangeCallback(mCallback);
        mIndicatorView.setViewPager(mViewPager);
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig().hideBar(BarHide.FLAG_HIDE_BAR);
    }

    private final ViewPager2.OnPageChangeCallback mCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mViewPager.getCurrentItem() != mAdapter.getCount() - 1 || positionOffsetPixels <= 0) {
                return;
            }

            mIndicatorView.setVisibility(View.VISIBLE);
            mCompleteView.setVisibility(View.INVISIBLE);
            mCompleteView.clearAnimation();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return;
            }

            boolean lastItem = mViewPager.getCurrentItem() == mAdapter.getCount() - 1;
            mIndicatorView.setVisibility(lastItem ? View.INVISIBLE : View.VISIBLE);
            mCompleteView.setVisibility(lastItem ? View.VISIBLE : View.INVISIBLE);
            if (lastItem) {
                // 按钮呼吸动效
                ScaleAnimation animation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(350);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(Animation.INFINITE);
                mCompleteView.startAnimation(animation);
            }
        }
    };
}
