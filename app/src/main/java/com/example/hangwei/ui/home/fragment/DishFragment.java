package com.example.hangwei.ui.home.fragment;

import static android.app.Activity.RESULT_OK;
import static java.lang.Math.min;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.activity.DishInfoActivity;
import com.example.hangwei.ui.home.adapter.DishAdapter;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.WrapRecyclerView;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;
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
 * desc   : 菜品列表 Fragment
 */
public final class DishFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private static final int MAX_LIST_ITEM_NUM = Integer.MAX_VALUE;
    private static final int LIST_ITEM_ADD_NUM = 10;

    public static DishFragment newInstance() {
        return new DishFragment();
    }

    private int type; // [0: 全部]，[1：早餐]，[2：中晚]，[3：早中晚]
    private String campus; // 学院路 沙河
    private CharSequence search; // 搜索词
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private DishAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dish;
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
        type = 0;
        campus = "学院路";
        search = "";
        updateDishData(true, () -> {
        });
    }

    /**
     * 网络请求获取新信息
     * campus 学院路 沙河
     * type [1：早餐]，[2：正餐]，[3：饮品]
     * search 搜索关键词
     * setOrAdd: false:set true:add
     */
    private void updateDishData(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("campus", campus);
        params.put("type", type);
        params.put("search", search);

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
                        getAttachActivity().runOnUiThread(afterResponse);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        dishes.addAll(locationGetDishes(data.getJSONArray(campus)));
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
                    getAttachActivity().runOnUiThread(afterResponse);
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    private List<Dish> locationGetDishes(JSONArray location) throws JSONException {
        List<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < location.length(); i++) {
            JSONObject jsonDish = location.getJSONObject(i);
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

        // 创建一个 Intent 对象，指定当前的 Fragment 的上下文和要启动的 Activity 类
        Intent intent = new Intent(getActivity(), DishInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", dish.id);
        bundle.putString("name", dish.name);
        bundle.putString("price", dish.price);
        bundle.putInt("favorCnt", dish.likeCount);
        bundle.putString("picUrl", dish.foodPicUrl);
        intent.putExtras(bundle);

        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK && data != null) {
                dish.setLikeCount(data.getIntExtra("favorCnt", dish.likeCount));
                mAdapter.setItem(position, dish);
            }
        });
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

    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {

    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getCampus() {
        return campus;
    }

    public void setSearch(CharSequence search) {
        this.search = search;
    }
}