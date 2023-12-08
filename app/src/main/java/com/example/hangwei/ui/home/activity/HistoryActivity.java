package com.example.hangwei.ui.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.ui.home.element.History;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.adapter.HistoryAdapter;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.WrapRecyclerView;
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

/**
 * desc   : 历史展示页
 */
public final class HistoryActivity extends AppActivity
        implements OnRefreshLoadMoreListener, BaseAdapter.OnItemClickListener {

    private String id;
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;

    public static HistoryActivity newInstance() {
        return new HistoryActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.history_layout);
        mRecyclerView = findViewById(R.id.history_list);

        mAdapter = new HistoryAdapter(this);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        id = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null");
        getHistory(() -> {});
    }

    private void getHistory(Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", id);
        AsyncHttpUtil.httpPostForObject(Ports.dishHistoryGet, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get history http fail!", ToastConst.errorStyle);
                runOnUiThread(() -> afterResponse.run());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        runOnUiThread(() -> afterResponse.run());
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jsonHistories = data.getJSONArray("histories");
                        List<History> histories = new ArrayList<>(jsonHistories.length());
                        for (int i = 0; i < jsonHistories.length(); i++) {
                            JSONObject jsonHistory = jsonHistories.getJSONObject(i);

                            String time = jsonHistory.getString("queryTime");
                            JSONObject jsonDish = jsonHistory.getJSONObject("dish");

                            histories.add(new History(new Dish(jsonDish), time));
                        }
                        runOnUiThread(() -> {
                            mAdapter.setData(histories);
                            afterResponse.run();
                        });
                    }
                } catch (JSONException e) {
                    runOnUiThread(afterResponse);
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
        History history = mAdapter.getItem(position);

        Intent intent = new Intent(getActivity(), DishInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", history.dish.id);
        bundle.putString("name", history.dish.name);
        bundle.putString("price", history.dish.price);
        bundle.putString("picUrl", history.dish.foodPicUrl);
        bundle.putString("time", history.time);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getHistory(() -> mRefreshLayout.finishRefresh());
    }
}