package com.example.hangwei_administrator.ui.home.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.TitleBarFragment;
import com.example.hangwei_administrator.ui.home.activity.FavoriteActivity;
import com.example.hangwei_administrator.ui.home.element.Dish;
import com.example.hangwei_administrator.base.BaseAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.home.activity.DishInfoActivity;
import com.example.hangwei_administrator.ui.home.adapter.DishFavAdapter;
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

public class FavDishFragment extends TitleBarFragment<FavoriteActivity>
        implements OnRefreshLoadMoreListener, BaseAdapter.OnItemClickListener {
    public static FavDishFragment newInstance() {
        return new FavDishFragment();
    }

    private String userId;
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private DishFavAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fav;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.fav_layout);
        mRecyclerView = findViewById(R.id.fav_recycler_view);

        mAdapter = new DishFavAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        userId = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null");
        fresh(() -> {});
    }

    public void fresh(Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        AsyncHttpUtil.httpPost(Ports.dishFavAll, params, new Callback() {
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
                    System.out.println(jsonObject);
                    List<Dish> dishes = new ArrayList<>();

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        getAttachActivity().runOnUiThread(afterResponse);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jsonDishes = data.getJSONArray("dishes");

                        for (int dish_index = 0; dish_index < jsonDishes.length(); dish_index++) {
                            JSONObject jsonDish = jsonDishes.getJSONObject(dish_index);
                            Dish dish = new Dish(jsonDish);
                            dishes.add(dish);
                        }
                        getAttachActivity().runOnUiThread(() -> {
                            mAdapter.setData(dishes);
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
        bundle.putString("price", dish.price);
        bundle.putString("picUrl", dish.foodPicUrl);
        bundle.putInt("favorCnt", dish.likeCount);
        bundle.putString("location", dish.location);
        intent.putExtras(bundle);

        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK && data != null) {
                if (data.getBooleanExtra("isDelete", false)) {
                    mAdapter.removeItem(position);
                    return;
                }
                dish.setName(data.getStringExtra("dishName"));
                dish.setPrice(data.getStringExtra("price"));
                dish.setFoodPicUrl(data.getStringExtra("url"));
                dish.setLikeCount(data.getIntExtra("favorCnt", dish.likeCount));
                dish.setCommentCount(data.getIntExtra("commentCnt", dish.commentCount));
                mAdapter.setItem(position, dish);
            }
        });
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
