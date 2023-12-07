package com.example.hangwei.ui.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.adapter.DishAdapter;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.ClearEditText;
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

public class SearchResultActivity extends AppActivity
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private ClearEditText mSearchBox;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private DishAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.search_result_activity;
    }

    @Override
    protected void initView() {
        mSearchBox = findViewById(R.id.search_result_searchBox);

        mSearchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(getContentView());
                // 隐藏光标
                mSearchBox.setCursorVisible(false);
                refresh();
            }
            return false;
        });
        mRefreshLayout = findViewById(R.id.dish_refresh_layout);
        mRecyclerView = findViewById(R.id.dish_list);

        mAdapter = new DishAdapter(this);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mSearchBox.setText(bundle.getString("search"));
        updateDishData(true, () -> {
        });
    }

    private void updateDishData(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("search", mSearchBox.getText().toString());
        AsyncHttpUtil.httpPostForObject(Ports.dishSearch, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get dish data http fail!", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<Dish> dishes = new ArrayList<>();

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        runOnUiThread(afterResponse);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jsonDishes = data.getJSONArray("dishes");

                        for (int dish_index = 0; dish_index < jsonDishes.length(); dish_index++) {
                            JSONObject jsonDish = jsonDishes.getJSONObject(dish_index);
                            Dish dish = new Dish(
                                    jsonDish.getString("dishId"),
                                    jsonDish.getString("dishName"),
                                    jsonDish.getString("campus"),
                                    jsonDish.getString("price"),
                                    jsonDish.getInt("likeCount"),
                                    jsonDish.getInt("commentCount"),
                                    jsonDish.getString("picture"));
                            dishes.add(dish);
                        }
                        runOnUiThread(() -> {
                            if (setElseAdd) {
                                mAdapter.setData(dishes);
                            } else {
                                mAdapter.addData(dishes);
                            }
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

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        Dish dish = mAdapter.getItem(position);

        // 创建一个 Intent 对象，指定当前的 Fragment 的上下文和要启动的 Activity 类
        Intent intent = new Intent(getActivity(), DishInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", dish.id);
        bundle.putString("name", dish.name);
        bundle.putString("price", dish.price);
        bundle.putString("picUrl", dish.foodPicUrl);
        intent.putExtras(bundle);

        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK && data != null) {
                dish.setLikeCount(data.getIntExtra("favorCnt", dish.likeCount));
                mAdapter.setItem(position, dish);
            }
        });
    }

    public void refresh() {
        updateDishData(true, () -> mRefreshLayout.finishRefresh());
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        updateDishData(false, () -> {
            mRefreshLayout.finishLoadMore();
//            mAdapter.setLastPage(mAdapter.getCount() >= MAX_LIST_ITEM_NUM);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        });
    }
}
