package com.example.hangwei.ui.commu.posts.list;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.commu.posts.add.AddPostActivity;
import com.example.hangwei.ui.commu.posts.detail.PostDetailActivity;
import com.example.hangwei.ui.home.activity.HomeActivity;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.layout.XCollapsingToolbarLayout;
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

public class PostsListFragment extends TitleBarFragment<HomeActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private SmartRefreshLayout mRefreshLayout;
    private ImageView tv_add_post;
    private WrapRecyclerView mRecyclerView;
    private PostItemAdapter mAdapter;
    private String userName;
    private String avatar;

    public static PostsListFragment newInstance() {
        return new PostsListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_posts_list;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.posts_refresh_layout);
        tv_add_post = findViewById(R.id.postList_add);

        mRecyclerView = findViewById(R.id.posts_list);
        mAdapter = new PostItemAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);

        tv_add_post.setOnClickListener(view -> fetchPer(() -> {
            startActivityForResult(AddPostActivity.class, (resultCode, data) -> {
                if (resultCode == RESULT_OK && data != null) {
                    PostItem postItem = new PostItem(data.getStringExtra("postID"), userName, avatar,
                            data.getStringExtra("title"), data.getStringExtra("tag"),
                            data.getStringExtra("time"), 0);
                    mAdapter.addItem(0, postItem);
                    mRefreshLayout.finishRefresh();
                }
            });
        }));
    }

    @Override
    protected void initData() {
        SharedPreferences prefs = getAttachActivity().getSharedPreferences("BasePrefs", MODE_PRIVATE);
        userName = prefs.getString("usedName", "小航兵");
        avatar = prefs.getString("usedAvatar", null);
        refreshPosts();
    }

    public void refreshPosts() {
        updatePosts(false, () -> mRefreshLayout.finishRefresh());
    }

    // 填充数据
    public void updatePosts(boolean addMore, Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        AsyncHttpUtil.httpPost(Ports.postsGetAll, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("服务器有一些小问题~", ToastConst.warnStyle);
                getAttachActivity().runOnUiThread(afterResponse);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        getAttachActivity().runOnUiThread(afterResponse);
                    } else {
                        if (jsonObject.isNull("data")) {
                            getAttachActivity().runOnUiThread(afterResponse);
                            return;
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<PostItem> posts = parsePosts(jsonArray);
                        getAttachActivity().runOnUiThread(() -> {
                            if (addMore) {
                                mAdapter.addData(posts);
                            } else {
                                mAdapter.setData(posts);
                            }
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

    private void fetchPer(Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        AsyncHttpUtil.httpPost(Ports.userIsSilence, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("服务器有一些小问题~", ToastConst.warnStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        if (jsonObject.getJSONObject("data").getBoolean("isSilence")) {
                            ToastUtil.toast("您已被禁言，无法发帖", ToastConst.warnStyle);
                        } else {
                            getAttachActivity().runOnUiThread(() -> afterResponse.run());
                        }
                    }
                } catch (JSONException e) {
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
        Intent intent = new Intent(getAttachActivity(), PostDetailActivity.class);
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
        refreshPosts();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {

    }
}