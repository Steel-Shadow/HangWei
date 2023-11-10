package com.example.hangwei.ui.home.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.app.Dish;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.adapter.StatusAdapter;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.layout.WrapRecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * desc   : 加载案例 Fragment
 */
public final class StatusFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private static final int MAX_LIST_ITEM_NUM = 20;
    private static final int LIST_ITEM_ADD_NUM = 5;

    public static StatusFragment newInstance() {
        return new StatusFragment();
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private StatusAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);

        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        TextView headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        headerView.setText("我是头部");
        headerView.setOnClickListener(v -> ToastUtil.toast("点击了头部", ToastConst.successStyle));

        TextView footerView = mRecyclerView.addFooterView(R.layout.picker_item);
        footerView.setText("我是尾部");
        footerView.setOnClickListener(v -> ToastUtil.toast("点击了尾部", ToastConst.errorStyle));

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        mAdapter.setData(getDishData());
    }

    /**
     * TODO:
     * 网络请求获取新信息
     */
    private List<Dish> getDishData() {
        List<Dish> data = new ArrayList<>();
        for (int i = mAdapter.getCount(); i < mAdapter.getCount() + LIST_ITEM_ADD_NUM; i++) {
            HashMap<String, String> params = new HashMap<>();
//            params.put("userName", userName);
//            params.put("password", password);
            AsyncHttpUtil.httpPost(Ports.userLoginUrl, params, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                }
            });

            Dish dish = new Dish("菜品" + i, "location", 1.0, 2, 3, "https://goo.gl/gEgYUd");
            data.add(dish);
        }
        return data;
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView RecyclerView对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        // todo: 点击具体条目，跳转到评论区
        Dish dish = mAdapter.getItem(position);
        ToastUtil.toast(dish.name + "跳转评论区", ToastConst.hintStyle);
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(getDishData());
            mRefreshLayout.finishRefresh();
        }, 1000);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.addData(getDishData());
            mRefreshLayout.finishLoadMore();

            mAdapter.setLastPage(mAdapter.getCount() >= MAX_LIST_ITEM_NUM);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        }, 1000);
    }
}