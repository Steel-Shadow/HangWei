package com.example.hangwei.ui.home.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.ui.home.element.Dish;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.activity.DishInfoActivity;
import com.example.hangwei.ui.home.adapter.DishFavAdapter;
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

public class FavDishFragment extends BaseFragment<BaseActivity>
        implements BaseAdapter.OnItemClickListener {
    public static FavDishFragment newInstance() {
        return new FavDishFragment();
    }

    private RecyclerView mRecyclerView;
    private DishFavAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fav;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.fav_recycler_view);

        mAdapter = new DishFavAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        HashMap<String, String> params = new HashMap<>();
        String userId = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null");
        params.put("userId", userId);
        AsyncHttpUtil.httpPost(Ports.dishFavAll, params, new Callback() {
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
                                    jsonDish.getInt("price"),
                                    jsonDish.getInt("likeCount"),
                                    jsonDish.getInt("commentCount"),
                                    jsonDish.getString("picture"));
                            dishes.add(dish);
                        }
                        getAttachActivity().runOnUiThread(() -> {
                            mAdapter.setData(dishes);
                        });
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
}
