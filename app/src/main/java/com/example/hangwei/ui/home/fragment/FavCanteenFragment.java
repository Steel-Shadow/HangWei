package com.example.hangwei.ui.home.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.ui.home.element.Canteen;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.activity.CanteenActivity;
import com.example.hangwei.ui.home.adapter.CanteenFavAdapter;
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

public class FavCanteenFragment extends BaseFragment<BaseActivity>
        implements BaseAdapter.OnItemClickListener {
    public static FavCanteenFragment newInstance() {
        return new FavCanteenFragment();
    }

    private RecyclerView mRecyclerView;
    private CanteenFavAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fav;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.fav_recycler_view);

        mAdapter = new CanteenFavAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        HashMap<String, Object> params = new HashMap<>();
        String userId = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null");
        params.put("userId", userId);
        AsyncHttpUtil.httpPostForObject(Ports.canteenFavAll, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get dish data http fail!", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<Canteen> canteens = new ArrayList<>();

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jsonCanteens = data.getJSONArray("canteens");
                        for (int i = 0; i < jsonCanteens.length(); i++) {
                            JSONObject canteen = jsonCanteens.getJSONObject(i);
                            canteens.add(new Canteen(
                                    canteen.getString("id"),
                                    canteen.getString("breakfast"),
                                    canteen.getString("dinner"),
                                    canteen.getString("location"),
                                    canteen.getString("lunch"),
                                    canteen.getString("name"),
                                    canteen.getString("picUrl")
                            ));
                        }
                        getAttachActivity().runOnUiThread(() -> {
                            mAdapter.setData(canteens);
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
        Canteen canteen = mAdapter.getItem(position);

        // 创建一个 Intent 对象，指定当前的 Fragment 的上下文和要启动的 Activity 类
        Intent intent = new Intent(getActivity(), CanteenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", canteen.id);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
