package com.example.hangwei_administrator.ui.home.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.TitleBarFragment;
import com.example.hangwei_administrator.ui.home.activity.FavoriteActivity;
import com.example.hangwei_administrator.ui.home.element.Canteen;
import com.example.hangwei_administrator.base.BaseAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.home.activity.CanteenActivity;
import com.example.hangwei_administrator.ui.home.adapter.CanteenFavAdapter;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.view.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FavCanteenFragment extends TitleBarFragment<FavoriteActivity>
        implements OnRefreshLoadMoreListener, BaseAdapter.OnItemClickListener {
    public static FavCanteenFragment newInstance() {
        return new FavCanteenFragment();
    }

    private String userId;
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private CanteenFavAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fav;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.fav_layout);
        mRecyclerView = findViewById(R.id.fav_recycler_view);

        mAdapter = new CanteenFavAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        userId = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", null);
        fresh(() -> {});
    }

    public void fresh(Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        AsyncHttpUtil.httpPostForObject(Ports.canteenFavAll, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get dish data http fail!", ToastConst.errorStyle);
                getAttachActivity().runOnUiThread(afterResponse);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<Canteen> canteens = new ArrayList<>();

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        getAttachActivity().runOnUiThread(afterResponse);
                    } else {
                        if (jsonObject.isNull("data")) {
                            getAttachActivity().runOnUiThread(afterResponse);
                            return;
                        }
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jsonCanteens = data.getJSONArray("canteens");
                        for (int i = 0; i < jsonCanteens.length(); i++) {
                            JSONObject canteen = jsonCanteens.getJSONObject(i);
                            canteens.add(new Canteen(canteen.getString("id")));
                        }
                        getAttachActivity().runOnUiThread(() -> {
                            mAdapter.setData(canteens);
                            afterResponse.run();
                        });
                    }
                } catch (JSONException e) {
                    getAttachActivity().runOnUiThread(afterResponse);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView RecyclerView对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     * @apiNote 点击菜品跳转到菜品详情页(评论区 + 配菜)
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        Canteen canteen = mAdapter.getItem(position);

        // 创建一个 Intent 对象，指定当前的 Fragment 的上下文和要启动的 Activity 类
        Intent intent = new Intent(getActivity(), CanteenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", canteen.id);
        bundle.putString("name", canteen.name);
        bundle.putString("breakfast", canteen.breakfast);
        bundle.putString("lunch", canteen.lunch);
        bundle.putString("dinner", canteen.dinner);
        bundle.putString("location", canteen.location);
        bundle.putString("detail", canteen.detail);
        bundle.putInt("picUrl", canteen.picID);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        fresh(() -> mRefreshLayout.finishLoadMore());
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        fresh(() -> mRefreshLayout.finishRefresh());
    }
}
