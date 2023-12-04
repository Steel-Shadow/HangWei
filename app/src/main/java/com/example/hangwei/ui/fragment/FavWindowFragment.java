package com.example.hangwei.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.Window;
import com.example.hangwei.base.BaseActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.activity.WindowActivity;
import com.example.hangwei.ui.adapter.WindowFavAdapter;
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

public class FavWindowFragment extends BaseFragment<BaseActivity>
        implements BaseAdapter.OnItemClickListener {
    public static FavWindowFragment newInstance() {
        return new FavWindowFragment();
    }

    private RecyclerView mRecyclerView;
    private WindowFavAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fav_fragment;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.fav_recycler_view);

        mAdapter = new WindowFavAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        HashMap<String, Object> params = new HashMap<>();
        String userId = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedID", "null");
        params.put("userId", userId);
        AsyncHttpUtil.httpPostForObject(Ports.windowFavAll, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("Get dish data http fail!", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<Window> windows = new ArrayList<>();

                    if (jsonObject.getInt("code") == 2) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jsonWindows = data.getJSONArray("windows");
                        for (int i = 0; i < jsonWindows.length(); i++) {
                            JSONObject jsonWindow = jsonWindows.getJSONObject(i);
                            windows.add(new Window(
                                    jsonWindow.getString("windowId"),
                                    jsonWindow.getString("windowName"),
                                    null, null));
                        }
                        getAttachActivity().runOnUiThread(() -> mAdapter.setData(windows));
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
        Window window = mAdapter.getItem(position);

        Intent intent = new Intent(getActivity(), WindowActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", window.id);
        bundle.putString("name", window.name);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
