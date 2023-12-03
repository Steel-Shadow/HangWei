package com.example.hangwei.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.Dish;
import com.example.hangwei.app.History;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.adapter.HistoryAdapter;
import com.example.hangwei.utils.ToastUtil;

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
public final class HistoryActivity extends BaseActivity
        implements BaseAdapter.OnItemClickListener {
    public static HistoryActivity newInstance() {
        return new HistoryActivity();
    }

    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.history_activity;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.history_list);

        mAdapter = new HistoryAdapter(this);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        HashMap<String, Object> params = new HashMap<>();
        String id = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null");
        params.put("userId", id);
        AsyncHttpUtil.httpPostForObject(Ports.dishHistoryGet, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get history http fail!", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
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
                        runOnUiThread(() -> mAdapter.setData(histories));
                    }
                } catch (JSONException e) {
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
        bundle.putInt("price", history.dish.price);
        bundle.putString("picUrl", history.dish.foodPicUrl);
        bundle.putString("time", history.time);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}