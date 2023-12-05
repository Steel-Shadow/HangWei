package com.example.hangwei.ui.home.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.ui.home.element.Favorite;
import com.example.hangwei.ui.home.element.Window;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.adapter.WindowAdapter;
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

/**
 * desc   : 食堂展示页
 */
public final class CanteenActivity extends BaseActivity
        implements OnRefreshLoadMoreListener {
    private static final int MAX_LIST_ITEM_NUM = Integer.MAX_VALUE;
    private static final int LIST_ITEM_ADD_NUM = 10;
    private String mId;
    private TextView mName;
    private TextView mTime;
    private TextView mLocation;
    private Favorite mFavorite;
    private ImageView mCanteenPic;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private WindowAdapter mAdapter;

    public static CanteenActivity newInstance() {
        return new CanteenActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_canteen;
    }

    @Override
    protected void initView() {
        mCanteenPic = findViewById(R.id.canteen_pic);
        mName = findViewById(R.id.canteen_name);
        mFavorite = new Favorite(this, findViewById(R.id.canteen_favorite), mName.getText().toString(), Ports.canteenFavChange, Ports.canteenFavCheck);

        mTime = findViewById(R.id.canteen_time);
        mLocation = findViewById(R.id.canteen_location);

        mRefreshLayout = findViewById(R.id.canteen_refresh_layout);
        mRecyclerView = findViewById(R.id.canteen_recycler_view);

        mAdapter = new WindowAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        bundle.getString("id");
        mId = bundle.getString("id");

        HashMap<String, String> params = new HashMap<>();
        params.put("id", mId);
        AsyncHttpUtil.httpPost(Ports.canteenInfo, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get canteen data http fail!", ToastConst.errorStyle);
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
                        String name = data.getString("name");

                        String time = "早餐 " + data.getString("breakfast") + "\n" +
                                "午餐 " + data.getString("lunch") + "\n" +
                                "晚餐 " + data.getString("dinner");

                        String location = data.getString("location");
                        String picUrl = data.getString("picUrl");

                        runOnUiThread(() -> {
                            mName.setText(name);
                            mTime.setText(time);
                            mLocation.setText(location);
                            Glide.with(getContext()).load(picUrl).into(mCanteenPic);
                            updateCanteen(true, () -> mRefreshLayout.finishRefresh());
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
        mRefreshLayout.setOnRefreshLoadMoreListener(null);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        updateCanteen(false, () -> {
            mRefreshLayout.finishLoadMore();
            mAdapter.setLastPage(mAdapter.getCount() >= MAX_LIST_ITEM_NUM);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updateCanteen(true, () -> mRefreshLayout.finishRefresh());
    }

    private void updateCanteen(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("canteen", mName);
        AsyncHttpUtil.httpPostForObject(Ports.canteenGetDishes, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Canteen get dish data http fail!", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<Window> windows = new ArrayList<>();

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        runOnUiThread(afterResponse);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");

                        JSONArray jsonWindows = data.getJSONArray("windows");
                        for (int i = 0; i < jsonWindows.length(); i++) {
                            JSONObject jsonWindow = jsonWindows.getJSONObject(i);
                            String id = jsonWindow.getString("windowId");
                            String name = jsonWindow.getString("windowName");
                            JSONArray dishes = jsonWindow.getJSONArray("dishes");
                            JSONObject jsonDish1 = dishes.getJSONObject(0);
                            Dish dish1 = new Dish(jsonDish1);
                            JSONObject jsonDish2 = dishes.getJSONObject(1);
                            Dish dish2 = new Dish(jsonDish2);
                            windows.add(new Window(id, name, dish1, dish2));
                        }

                        runOnUiThread(() -> {
                            if (setElseAdd) {
                                mAdapter.setData(windows);
                            } else {
                                mAdapter.addData(windows);
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
}