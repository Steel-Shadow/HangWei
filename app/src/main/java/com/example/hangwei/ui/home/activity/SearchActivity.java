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
import com.example.hangwei.ui.home.adapter.HotAdapter;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.ClearEditText;

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

public class SearchActivity extends AppActivity
        implements BaseAdapter.OnItemClickListener {
    private ClearEditText mSearchBox;
    private RecyclerView mRecyclerView;
    private HotAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.search_activity;
    }

    @Override
    protected void initView() {
        mSearchBox = findViewById(R.id.search_searchBox);

        mSearchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(getContentView());
                // 隐藏光标
                mSearchBox.setCursorVisible(false);
                Intent intent = new Intent(getContext(), SearchResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("search", mSearchBox.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
            return false;
        });
        mRecyclerView = findViewById(R.id.search_hot_list);

        mAdapter = new HotAdapter(this);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("search", mSearchBox.getText().toString());
        AsyncHttpUtil.httpPostForObject(Ports.dishHot, params, new Callback() {
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
                            mAdapter.setData(dishes);
                        });
                    }
                } catch (JSONException e) {
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

        startActivity(intent);
    }
}
