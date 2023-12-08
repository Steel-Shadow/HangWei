package com.example.hangwei_administrator.ui.commu.friends.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.base.BaseAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.data.glide.GlideApp;
import com.example.hangwei_administrator.dialog.InputDialog;
import com.example.hangwei_administrator.ui.commu.posts.detail.PostDetailActivity;
import com.example.hangwei_administrator.ui.commu.posts.list.PostItem;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.layout.XCollapsingToolbarLayout;
import com.example.hangwei_administrator.widget.view.WrapRecyclerView;

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

public class FriendDetailActivity extends AppActivity
        implements XCollapsingToolbarLayout.OnScrimsListener, BaseAdapter.OnItemClickListener {
    private String user; // APP用户
    private String userName; // 当前用户
    private Boolean isFollow; // 是否关注
    private List<PostItem> allPosts = new ArrayList<>();
    private Uri ava;
    private ImageView avatar;
    private TextView detail_owner;
    private Button btn_report;
    private ToggleButton btn_follow;
    private TextView follow_msg;
    private XCollapsingToolbarLayout mCollapsingToolbarLayout;
    private WrapRecyclerView mRecyclerView;
    private DetailAdapter mAdapter;
    private Intent resIntent = new Intent();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_detail;
    }

    @Override
    protected void initView() {
        avatar = findViewById(R.id.friend_detail_avatar);
        detail_owner = findViewById(R.id.friend_detail_userName);
        btn_report = findViewById(R.id.friend_detail_report);
        btn_follow = findViewById(R.id.friend_detail_follow);
        follow_msg = findViewById(R.id.friend_detail_followdetail);
        mCollapsingToolbarLayout = findViewById(R.id.friend_detail_bar);
        mRecyclerView = findViewById(R.id.friend_detail_posts);

        mAdapter = new DetailAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        btn_report.setOnClickListener(view -> {
            new InputDialog.Builder(this)
                    .setTitle("举报")
                    .setHint("请详述举报原因")
                    .setConfirm("确定")
                    .setCancel("取消")
                    .setListener((dialog, content) -> doReport(content))
                    .show();
        });

        follow_msg.setOnClickListener(view -> doFollow());

        // 设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");
        userName = bundle.getString("userName");
        isFollow = bundle.getBoolean("isFollow");
        ava = Uri.parse(bundle.getString("avatar"));
        GlideApp.with(getContext())
                .load(ava)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(avatar);

        if (user.equals(userName)) {
            findViewById(R.id.friend_detail_gonelay).setVisibility(View.GONE);
        }

        detail_owner.setText(userName);
        btn_follow.setChecked(isFollow);
        if (isFollow) {
            follow_msg.setText("= 已关注");
            follow_msg.setTextColor(Color.GRAY);
            follow_msg.setBackground(getDrawable(R.drawable.bg_vary_corner));
        } else {
            follow_msg.setText("+关注");
            follow_msg.setTextColor(Color.parseColor("#FF83FA"));
            follow_msg.setBackground(getDrawable(R.drawable.bg_purple_corner));
        }
        setPosts();
    }

    // 填充数据
    public void setPosts() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        AsyncHttpUtil.httpPost(Ports.postsGetUser, params, new Callback() {
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
                        if (jsonObject.isNull("data")) {
                            return;
                        }
                        JSONArray jsArray = jsonObject.getJSONArray("data");
                        runOnUiThread(() -> {
                            try {
                                for (int i = 0; i < jsArray.length(); i++) {
                                    JSONObject jsObj = (JSONObject) jsArray.get(i);
                                    allPosts.add(new PostItem(jsObj.getString("id"), jsObj.getString("userName"),
                                            ava, jsObj.getString("title"), jsObj.getString("tag"),
                                            jsObj.getString("time"), jsObj.getInt("thumbUps")));
                                }
                                mAdapter.setData(allPosts);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    public void doFollow() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", user);
        params.put("blogger", userName);
        params.put("isFollow", String.valueOf(!isFollow));
        AsyncHttpUtil.httpPost(Ports.friendsFollow, params, new Callback() {
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
                        runOnUiThread(() -> {
                            isFollow = !isFollow;
                            btn_follow.setChecked(isFollow);
                            if (isFollow) {
                                follow_msg.setText("= 已关注");
                                follow_msg.setTextColor(Color.GRAY);
                                follow_msg.setBackground(getDrawable(R.drawable.bg_vary_corner));
                                ToastUtil.toast("关注成功", ToastConst.successStyle);
                            } else {
                                follow_msg.setText("+关注");
                                follow_msg.setTextColor(Color.parseColor("#FF83FA"));
                                follow_msg.setBackground(getDrawable(R.drawable.bg_purple_corner));
                                ToastUtil.toast("已取关", ToastConst.warnStyle);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    public void doReport(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.toast("请说明举报理由", ToastConst.warnStyle);
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", user);
        params.put("blogger", userName);
        params.put("content", content);
        AsyncHttpUtil.httpPost(Ports.friendsReport, params, new Callback() {
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
                        ToastUtil.toast("您的举报已在审核中", ToastConst.hintStyle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public boolean isStatusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }

    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {

    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        PostItem postItem = mAdapter.getItem(position);
        Intent intent = new Intent(this, PostDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", user);
        bundle.putString("postId", postItem.id);
        intent.putExtras(bundle);
        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK && data != null) {
                // 删除
                if (data.getBooleanExtra("isDelete", false)) {
                    mAdapter.removeItem(position);
                    return;
                }

                // 关注
                isFollow = data.getBooleanExtra("isFollow", isFollow);
                btn_follow.setChecked(isFollow);
                if (isFollow) {
                    follow_msg.setText("= 已关注");
                    follow_msg.setTextColor(Color.GRAY);
                    follow_msg.setBackground(getDrawable(R.drawable.bg_vary_corner));
                } else {
                    follow_msg.setText("+关注");
                    follow_msg.setTextColor(Color.parseColor("#FF83FA"));
                    follow_msg.setBackground(getDrawable(R.drawable.bg_purple_corner));
                }

                // 点赞
                postItem.setThumbUps(data.getIntExtra("favorCnt", postItem.thumbUps));
                mAdapter.setItem(position, postItem);
            }
        });
    }

    @Override
    public void finish() {
        resIntent.putExtra("isFollow", isFollow);
        setResult(Activity.RESULT_OK, resIntent);
        super.finish();
    }
}
