package com.example.hangwei.ui.home.dishInfo;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.Dish;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.activity.DishInfoActivity;
import com.example.hangwei.ui.home.adapter.DishAdapter;
import com.example.hangwei.utils.ToastUtil;
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

public class SideDishFragment extends BaseFragment<BaseActivity> implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private static final int MAX_LIST_ITEM_NUM = 5;
    private static final int LIST_ITEM_ADD_NUM = 1;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private DishAdapter mAdapter;

    public static SideDishFragment newInstance() {
        return new SideDishFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dish_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.dish_refresh_layout);
        mRecyclerView = findViewById(R.id.dish_list);

        mAdapter = new DishAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        updateDishData(true, () -> {
        });
    }

    private void updateDishData(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        AsyncHttpUtil.httpPostForObject(Ports.sideDish, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Post sideDish http fail!", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        getAttachActivity().runOnUiThread(afterResponse);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray sideDishes = data.getJSONArray("sideDishes");
                        List<Dish> dishes = new ArrayList<>();

                        for (int i = 0; i < sideDishes.length(); i++) {
                            JSONObject jsonDish = sideDishes.getJSONObject(i);
                            Dish dish = new Dish(
                                    jsonDish.getString("dishId"),
                                    jsonDish.getString("dishName"),
                                    jsonDish.getString("campus"),
                                    jsonDish.getInt("price"),
                                    jsonDish.getInt("likeCount"),
                                    jsonDish.getInt("commentCount"),
                                    jsonDish.getString("picture"));
                            dishes.add(dish);
                        }

                        getAttachActivity().runOnUiThread(() -> {
                            if (setElseAdd) {
                                mAdapter.setData(dishes);
                            } else {
                                mAdapter.addData(dishes);
                            }
                            afterResponse.run();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updateDishData(true, refreshLayout::finishRefresh);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        updateDishData(false, () -> {
            mAdapter.setLastPage(mAdapter.getCount() >= MAX_LIST_ITEM_NUM);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
            mRefreshLayout.finishLoadMore();
        });
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        Dish dish = mAdapter.getItem(position);

        DishInfoActivity activity = (DishInfoActivity) getAttachActivity();
        activity.setDishId(dish.id);
        activity.setName(dish.name);
        activity.setPrice(dish.price);
        activity.setPic(dish.foodPicUrl);
        activity.updateFavorite();

        updateDishData(true, () -> {
        });
    }
}
