package com.example.hangwei_administrator.ui.commu.posts.report;

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
import com.example.hangwei_administrator.ui.commu.posts.detail.PostDetailActivity;
import com.example.hangwei_administrator.ui.commu.posts.list.PostItem;
import com.example.hangwei_administrator.ui.commu.posts.list.PostItemAdapter;
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

public class PostsListActivity extends AppActivity
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private PostItemAdapter mAdapter;
    private String userName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bad_posts;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.bad_posts_refresh_layout);
        mRecyclerView = findViewById(R.id.bad_posts_list);
        mAdapter = new PostItemAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
        userName = prefs.getString("usedName", "小航兵");
        updatePosts(() -> {});
    }

    // 填充数据
    public void updatePosts(Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        AsyncHttpUtil.httpPost(Ports.postsGetBad, params, new Callback() {
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
                        if (jsonObject.isNull("data")) {
                            runOnUiThread(afterResponse);
                            return;
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<PostItem> posts = parsePosts(jsonArray);
                        runOnUiThread(() -> {
                            mAdapter.setData(posts);
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

    private List<PostItem> parsePosts(JSONArray jsonArray) throws JSONException {
        List<PostItem> posts = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsObj = (JSONObject) jsonArray.get(i);
            posts.add(new PostItem(jsObj.getString("id"), jsObj.getString("userName"), jsObj.getString("avatar"),
                    jsObj.getString("title"), jsObj.getString("tag"),
                    jsObj.getString("time"), jsObj.getInt("thumbUps")));
        }
        return posts;
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        PostItem postItem = mAdapter.getItem(position);
        Intent intent = new Intent(this, PostDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("postId", postItem.id);
        intent.putExtras(bundle);
        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK && data != null) {
                if (data.getBooleanExtra("isDelete", false)) {
                    mAdapter.removeItem(position);
                    return;
                }
                postItem.setThumbUps(data.getIntExtra("favorCnt", postItem.thumbUps));
                mAdapter.setItem(position, postItem);
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updatePosts(() -> mRefreshLayout.finishRefresh());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        updatePosts(() -> mRefreshLayout.finishLoadMore());
    }
}