package com.example.hangwei_administrator.ui.manage.user;

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
import com.example.hangwei_administrator.dialog.MessageDialog;
import com.example.hangwei_administrator.ui.commu.friends.detail.DetailAdapter;
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

public class DetailActivity extends AppActivity
        implements XCollapsingToolbarLayout.OnScrimsListener, BaseAdapter.OnItemClickListener {
    private String user = "Administrator"; // APP用户
    private String userName; // 当前用户
    private Uri avatarUrl;
    private boolean isSilence;
    private List<PostItem> allPosts = new ArrayList<>();
    private ImageView avatar;
    private TextView detail_owner;
    private TextView detail_email;
    private TextView detail_pwd;
    private TextView detail_time;
    private Button btn_delete;
    private ToggleButton btn_silence;
    private XCollapsingToolbarLayout mCollapsingToolbarLayout;
    private WrapRecyclerView mRecyclerView;
    private DetailAdapter mAdapter;
    private Intent resIntent = new Intent();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initView() {
        avatar = findViewById(R.id.user_detail_avatar);
        detail_owner = findViewById(R.id.user_detail_userName);
        detail_email = findViewById(R.id.user_detail_email);
        detail_pwd = findViewById(R.id.user_detail_pwd);
        detail_time = findViewById(R.id.user_detail_time);
        btn_silence = findViewById(R.id.user_detail_silence);
        btn_delete = findViewById(R.id.user_detail_delete);
        mCollapsingToolbarLayout = findViewById(R.id.user_detail_bar);
        mRecyclerView = findViewById(R.id.user_detail_posts);

        mAdapter = new DetailAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        btn_silence.setOnClickListener(view -> {
            btn_silence.setChecked(!btn_silence.isChecked());
            if (isSilence) {
                new MessageDialog.Builder(this)
                        .setTitle("取消禁言")
                        .setMessage("即将对这个用户取消禁言")
                        .setConfirm("确定")
                        .setCancel("取消")
                        .setListener(dialog -> doSilence())
                        .show();
            } else {
                new MessageDialog.Builder(this)
                        .setTitle("禁言")
                        .setMessage("即将对这个用户进行禁言")
                        .setConfirm("确定")
                        .setCancel("取消")
                        .setListener(dialog -> doSilence())
                        .show();
            }
        });

        btn_delete.setOnClickListener(view -> {
            new MessageDialog.Builder(this)
                    .setTitle("删除用户")
                    .setMessage("即将永久删除这个用户，是否继续？")
                    .setConfirm("仍然删除")
                    .setCancel("取消")
                    .setListener(dialog -> doDelete())
                    .show();
        });

        // 设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("userName");
        isSilence = bundle.getBoolean("isSilence");
        avatarUrl = Uri.parse(bundle.getString("avatar"));

        GlideApp.with(getContext())
                .load(avatarUrl)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(avatar);

        btn_silence.setChecked(isSilence);
        if (isSilence) {
            btn_silence.setTextColor(Color.parseColor("#a0FF0000"));
        } else {
            btn_silence.setTextColor(Color.parseColor("#a01E90FF"));
        }

        detail_owner.setText(userName);
        detail_email.setText(bundle.getString("email"));
        detail_pwd.setText(bundle.getString("pwd"));
        detail_time.setText(bundle.getString("time"));
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
                                            avatarUrl, jsObj.getString("title"), jsObj.getString("tag"),
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
                }
            }
        });
    }

    public void doDelete() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        AsyncHttpUtil.httpPost(Ports.userDel, params, new Callback() {
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
                            resIntent.putExtra("isDelete", true);
                            ToastUtil.toast("删除成功", ToastConst.successStyle);
                            finish();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void doSilence() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("silence", String.valueOf(!isSilence));
        AsyncHttpUtil.httpPost(Ports.userSilence, params, new Callback() {
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
                            isSilence = !isSilence;
                            btn_silence.setChecked(isSilence);
                            if (isSilence) {
                                btn_silence.setTextColor(Color.parseColor("#a0FF0000"));
                                ToastUtil.toast("已对用户 <" + userName + "> 进行禁言", ToastConst.warnStyle);
                            } else {
                                btn_silence.setTextColor(Color.parseColor("#a01E90FF"));
                                ToastUtil.toast("用户 <" + userName + "> 已经解禁", ToastConst.successStyle);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

                // 点赞
                postItem.setThumbUps(data.getIntExtra("favorCnt", postItem.thumbUps));
                mAdapter.setItem(position, postItem);
            }
        });
    }

    @Override
    public void finish() {
        resIntent.putExtra("isSilence", isSilence);
        setResult(Activity.RESULT_OK, resIntent);
        super.finish();
    }
}
