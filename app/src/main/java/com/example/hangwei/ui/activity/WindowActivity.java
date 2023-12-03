package com.example.hangwei.ui.activity;

import static java.lang.Math.min;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.Dish;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.adapter.DishAdapter;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.layout.WrapRecyclerView;
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
 * desc   : 窗口的菜品展示页
 */
public final class WindowActivity extends BaseActivity
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private static final int MAX_LIST_ITEM_NUM = Integer.MAX_VALUE;
    private static final int LIST_ITEM_ADD_NUM = 10;

    public static WindowActivity newInstance() {
        return new WindowActivity();
    }

    private String mId;
    private TextView mWindowName;
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private DishAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.window_activity;
    }

    @Override
    protected void initView() {
        mWindowName = findViewById(R.id.window_name);
        mRefreshLayout = findViewById(R.id.window_refresh_layout);
        mRecyclerView = findViewById(R.id.window_dish_list);

        mAdapter = new DishAdapter(this);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mId = bundle.getString("id");
        mWindowName.setText(bundle.getString("name"));
//        mRefreshLayout.autoRefresh(0);
        updateDishData(true, () -> {
        });
    }

    /**
     * 网络请求获取新信息
     * setOrAdd: false:set true:add
     */
    private void updateDishData(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        AsyncHttpUtil.httpPostForObject(Ports.dishChoose, params, new Callback() {
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
                        dishes.addAll(locationGetDishes(data.getJSONArray("学院路")));
                        dishes.addAll(locationGetDishes(data.getJSONArray("沙河")));
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

    private List<Dish> locationGetDishes(JSONArray location) throws JSONException {
        List<Dish> dishes = new ArrayList<>();
        for (int canteen_index = 0; canteen_index < location.length(); canteen_index++) {
            JSONArray canteen = location.getJSONArray(canteen_index);

            for (int dish_index = 0; dish_index < min(LIST_ITEM_ADD_NUM, canteen.length()); dish_index++) {
                JSONObject jsonDish = canteen.getJSONObject(dish_index);
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
        }
        return dishes;
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
        Dish dish = mAdapter.getItem(position);

        Intent intent = new Intent(getActivity(), DishInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", dish.id);
        bundle.putString("name", dish.name);
        bundle.putInt("price", dish.price);
        bundle.putString("picUrl", dish.foodPicUrl);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

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
            mAdapter.setLastPage(mAdapter.getCount() >= MAX_LIST_ITEM_NUM);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        });
    }
}