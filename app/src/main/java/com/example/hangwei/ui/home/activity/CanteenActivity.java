package com.example.hangwei.ui.home.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.ui.home.element.Favorite;
import com.example.hangwei.ui.home.element.Window;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.adapter.WindowAdapter;
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
 * desc   : 食堂展示页
 */
public class CanteenActivity extends AppActivity implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private String mId;
    private TextView mName;
    private TextView mTime;
    private TextView mLocation;
    private Favorite mFavorite;
    private TextView mDetail;
    private ImageView mCanteenPic;
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private WindowAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_canteen;
    }

    @Override
    protected void initView() {
        mCanteenPic = findViewById(R.id.canteen_pic);
        mName = findViewById(R.id.canteen_name);

        mTime = findViewById(R.id.canteen_time);
        mLocation = findViewById(R.id.canteen_location);
        mDetail = findViewById(R.id.canteen_detail);

        mRefreshLayout = findViewById(R.id.canteen_refresh_layout);
        mRecyclerView = findViewById(R.id.canteen_recycler_view);

        mAdapter = new WindowAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        bundle.getString("id");
        mId = bundle.getString("id");
        mCanteenPic.setImageResource(bundle.getInt("picUrl"));
        mName.setText(bundle.getString("name"));
        mTime.setText("早餐   " + bundle.getString("breakfast") + "\n" +
                "午餐 " + bundle.getString("lunch") + "\n" +
                "晚餐 " + bundle.getString("dinner"));
        mLocation.setText(bundle.getString("location"));
        mDetail.setText(bundle.getString("detail"));
        mFavorite = new Favorite(this, findViewById(R.id.canteen_favorite), mId, Ports.canteenFavChange, Ports.canteenFavCheck);
        updateCanteen(true, () -> mRefreshLayout.finishRefresh());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
        mRefreshLayout.setOnRefreshLoadMoreListener(null);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updateCanteen(true, () -> mRefreshLayout.finishRefresh());
    }

    private void updateCanteen(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", mId);
        System.out.println(mId);
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
                            Dish dish2 = null;
                            if (dishes.length() == 2) {
                                JSONObject jsonDish2 = dishes.getJSONObject(1);
                                dish2 = new Dish(jsonDish2);
                            }
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

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        System.out.println("fffffuck");
        Window window = mAdapter.getItem(position);
        Intent intent = new Intent(this, WindowActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", window.id);
        bundle.putString("name", window.name);
        intent.putExtras(bundle);
        startActivityForResult(intent, (resultCode, data) -> {
            mAdapter.setItem(position, window);
            if (resultCode == RESULT_OK && data != null) {
                if (data.getBooleanExtra("isFavorite", false) && !mFavorite.isFavorite()) {
                    mFavorite.callFavorite();
                }
            }
        });
    }
}