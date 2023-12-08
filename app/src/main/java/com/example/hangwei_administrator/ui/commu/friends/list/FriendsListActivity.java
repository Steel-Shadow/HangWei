package com.example.hangwei_administrator.ui.commu.friends.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.base.BaseAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.commu.friends.detail.FriendDetailActivity;
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

public class FriendsListActivity extends AppActivity
        implements OnRefreshLoadMoreListener, BaseAdapter.OnItemClickListener {
    private String userName; // APP用户
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private List<Friend> allFriends = new ArrayList<>();
    private FriendAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friends_list;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.friends_layout);
        mRecyclerView = findViewById(R.id.friends_list);
        mAdapter = new FriendAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
        userName = prefs.getString("usedName", "小航兵");
        getFriends(() -> {});
    }

    public void getFriends(Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        AsyncHttpUtil.httpPost(Ports.friendsGetAll, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("服务器有一些小问题~", ToastConst.warnStyle);
                runOnUiThread(afterResponse);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        runOnUiThread(afterResponse);
                    } else {
                        if (!jsonObject.isNull("data")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            runOnUiThread(() -> {
                                try {
                                    setFriends(data);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                afterResponse.run();
                            });
                        } else {
                            runOnUiThread(() -> {
                                mAdapter.setData(new ArrayList<>());
                                runOnUiThread(afterResponse);
                            });
                        }
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

    private void setFriends(JSONArray jsonArray) throws JSONException {
        allFriends.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsObj = (JSONObject) jsonArray.get(i);
            Friend friend = new Friend(userName, jsObj.getString("userName"),
                    jsObj.getString("avatar"), true);
            allFriends.add(friend);
        }
        mAdapter.setData(allFriends);
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        Friend friend = mAdapter.getItem(position);
        Intent intent = new Intent(this, FriendDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user", userName);
        bundle.putString("userName", friend.userName);
        bundle.putString("avatar", String.valueOf(friend.avatar));
        bundle.putBoolean("isFollow", friend.isFollow);
        intent.putExtras(bundle);
        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK && data != null) {
                Boolean isFollow = data.getBooleanExtra("isFollow", false);
                friend.setFollow(isFollow);
                mAdapter.setItem(position, friend);
            }
        });
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getFriends(() -> mRefreshLayout.finishLoadMore());
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getFriends(() -> mRefreshLayout.finishRefresh());
    }
}