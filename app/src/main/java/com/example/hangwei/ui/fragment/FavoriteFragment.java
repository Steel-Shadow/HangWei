package com.example.hangwei.ui.fragment;

import com.example.hangwei.R;
import com.example.hangwei.base.BaseFragment;

public class FavoriteFragment extends BaseFragment {
    private String type;

    public FavoriteFragment(String type) {
        this.type = type;
    }

    public static FavoriteFragment newInstance(String type) {
        return new FavoriteFragment(type);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.favorite_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
