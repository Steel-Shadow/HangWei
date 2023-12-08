package com.example.hangwei_administrator.ui.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.ui.home.element.Dish;
import com.example.hangwei_administrator.base.BaseAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.home.adapter.DishAdapter;
import com.example.hangwei_administrator.ui.home.element.Favorite;
import com.example.hangwei_administrator.ui.manage.dish.DishAddActivity;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.view.WrapRecyclerView;
import com.hjq.bar.TitleBar;
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
public final class WindowActivity extends AppActivity
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    public static WindowActivity newInstance() {
        return new WindowActivity();
    }

    private String mId;
    private Favorite mFavorite;
    private TitleBar mWindowName;
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private DishAdapter mAdapter;
    private ImageView add_dish;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_window;
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

        add_dish = findViewById(R.id.window_add_dish);
        add_dish.setOnClickListener(view -> {
            Intent intent = new Intent(this, DishAddActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("windowId", mId);
            intent.putExtras(bundle);
            startActivityForResult(intent, (resultCode, data) -> {
                if (resultCode == RESULT_OK && data != null) {
                    Dish dish = new Dish(data.getStringExtra("id"), data.getStringExtra("name"),
                            data.getStringExtra("location"), data.getStringExtra("price"),
                            0, 0, data.getStringExtra("foodPicUrl"));
                    mAdapter.addItem(0, dish);
                    mRefreshLayout.finishRefresh();
                }
            });
        });
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mId = bundle.getString("id");
        mWindowName.setTitle(bundle.getString("name"));
        mFavorite = new Favorite(getContext(), findViewById(R.id.window_favorite), mId, Ports.windowFavChange, Ports.windowFavCheck);
//        mRefreshLayout.autoRefresh(0);
        updateDishData(true, () -> {
        });
    }

    /**
     * 网络请求获取新信息
     */
    private void updateDishData(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        System.out.println(mId);
        params.put("windowId",mId);
        AsyncHttpUtil.httpPostForObject(Ports.windowDishes, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get dish data http fail!", ToastConst.errorStyle);
                runOnUiThread(afterResponse);
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
                            Dish dish = new Dish(jsonDish);
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
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void finish() {
        Intent resIntent = new Intent();
        resIntent.putExtra("isFavorite", mFavorite.isFavorite());
        setResult(Activity.RESULT_OK, resIntent);
        super.finish();
    }
}