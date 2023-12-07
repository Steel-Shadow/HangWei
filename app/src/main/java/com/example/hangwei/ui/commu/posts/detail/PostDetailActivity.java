package com.example.hangwei.ui.commu.posts.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
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
import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.data.glide.GlideApp;
import com.example.hangwei.dialog.InputDialog;
import com.example.hangwei.dialog.MessageDialog;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.view.WrapRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PostDetailActivity extends AppActivity implements BaseAdapter.OnItemClickListener {
    private String userName; // APP用户
    /** 帖子相关 */
    private String postId;
    private String owner;
    private int favorCnt;
    private boolean favored;
    private List<PostComment> allComments = new ArrayList<>();
    private List<PostComment> ownerComments = new ArrayList<>();
    private ImageView avatar;
    private TextView post_detail_owner;
    private TextView post_detail_time;
    private ToggleButton btn_follow;
    private TextView follow_msg;
    private boolean isFollow;
    private TextView post_detail_title;
    private TextView post_detail_tag;
    private TextView post_detail_content;
    private ToggleButton btn_favor;
    private TextView post_detail_favorCnt;
    private Button btn_report;
    private TextView commentView;
    private Button btn_comment;
    private TextView comments_all;
    private TextView comments_owner;
    private WrapRecyclerView mRecyclerView;
    private PostCommentAdapter mAdapter;
    private PostComment curComment;
    private Intent resIntent = new Intent();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void initView() {
        avatar = findViewById(R.id.post_detail_avatar);
        post_detail_owner = findViewById(R.id.post_detail_user);
        post_detail_time = findViewById(R.id.post_detail_time);
        btn_follow = findViewById(R.id.post_detail_follow);
        follow_msg = findViewById(R.id.post_detail_followdetail);
        post_detail_title = findViewById(R.id.post_detail_title);
        post_detail_tag = findViewById(R.id.post_detail_tag);
        post_detail_content = findViewById(R.id.post_detail_content);
        btn_favor = findViewById(R.id.post_detail_favor);
        post_detail_favorCnt = findViewById(R.id.post_detail_favor_count);
        btn_report = findViewById(R.id.post_detail_report);
        commentView = findViewById(R.id.post_detail_comment);
        btn_comment = findViewById(R.id.post_detail_btn_comment);
        comments_all = findViewById(R.id.post_detail_comments_all);
        comments_owner = findViewById(R.id.post_detail_comments_owner);

        comments_all.setTextColor(Color.BLACK);
        comments_owner.setTextColor(Color.GRAY);

        mRecyclerView = findViewById(R.id.post_detail_comments);
        mAdapter = new PostCommentAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        follow_msg.setOnClickListener(view -> doFollow());

        btn_favor.setOnClickListener(view -> doFavor());

        btn_report.setOnClickListener(view -> {
            new InputDialog.Builder(this)
                    .setTitle("举报")
                    .setHint("请详述举报原因")
                    .setConfirm("确定")
                    .setCancel("取消")
                    .setListener((dialog, content) -> doReport(content))
                    .show();
        });

        btn_comment.setOnClickListener(view -> {
            doComment();
            hideKeyboard(view);
        });

        comments_all.setOnClickListener(view -> {
            mAdapter.setData(allComments);
            if (comments_all.getCurrentTextColor() == Color.BLACK) {
                comments_all.setTextColor(Color.GRAY);
                comments_owner.setTextColor(Color.BLACK);
            } else {
                comments_all.setTextColor(Color.BLACK);
                comments_owner.setTextColor(Color.GRAY);
            }
        });

        comments_owner.setOnClickListener(view -> {
            mAdapter.setData(ownerComments);
            if (comments_owner.getCurrentTextColor() == Color.BLACK) {
                comments_owner.setTextColor(Color.GRAY);
                comments_all.setTextColor(Color.BLACK);
            } else {
                comments_owner.setTextColor(Color.BLACK);
                comments_all.setTextColor(Color.GRAY);
            }
        });
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("userName");
        postId = bundle.getString("postId");
        setDetail();
    }

    // 填充数据
    public void setDetail() {
        System.out.println(postId);
        HashMap<String, String> params = new HashMap<>();
        params.put("id", postId);
        params.put("userName", userName);
        AsyncHttpUtil.httpPost(Ports.postsGet, params, new Callback() {
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
                        JSONObject jsObj = jsonObject.getJSONObject("data");
                        runOnUiThread(() -> {
                            try {
                                favored = jsObj.getBoolean("isThumbUp");
                                favorCnt = jsObj.getInt("thumbUps");
                                owner = jsObj.getString("userName");
                                isFollow = jsObj.getBoolean("isFollow");
                                GlideApp.with(getContext())
                                        .load(Uri.parse(jsObj.getString("avatar")))
                                        .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                                        .into(avatar);
                                post_detail_owner.setText(owner);
                                post_detail_time.setText(jsObj.getString("time"));
                                post_detail_title.setText(jsObj.getString("title"));
                                post_detail_tag.setText(jsObj.getString("tag"));
                                post_detail_content.setText(jsObj.getString("content"));
                                post_detail_favorCnt.setText(String.valueOf(favorCnt));
                                if (owner.equals(userName)) {
                                    btn_follow.setVisibility(View.GONE);
                                    follow_msg.setVisibility(View.GONE);
                                    btn_report.setBackground(getDrawable(R.drawable.post_delete));
                                    btn_report.setOnClickListener(view -> {
                                        new MessageDialog.Builder(PostDetailActivity.this)
                                                .setTitle("删除帖子")
                                                .setMessage("确认删除这个帖子？")
                                                .setConfirm("仍然删除")
                                                .setCancel("取消")
                                                .setListener(dialog -> doDelete())
                                                .show();
                                    });
                                } else {
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
                                }
                                btn_favor.setChecked(favored);
                                if (!jsObj.isNull("comments")) {
                                    setComments(jsObj.getJSONArray("comments"));
                                }
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

    public void setComments(JSONArray jsArray) throws JSONException {
        for (int i = 0; i < jsArray.length(); i++) {
            JSONObject jsObj = (JSONObject) jsArray.get(i);
            String commenter = jsObj.getString("userName");
            PostComment postComment =
                    new PostComment(jsObj.getString("id"), commenter,
                            jsObj.getString("content"), jsObj.getString("time"));
            allComments.add(postComment);
            if (commenter.equals(userName)) {
                ownerComments.add(postComment);
            }
        }
        mAdapter.setData(allComments);
    }

    public void doComment() {
        boolean isForbidden = getSharedPreferences("BasePrefs", MODE_PRIVATE).getBoolean("isForbidden", false);
        if (isForbidden) {
            ToastUtil.toast("您已被禁言，无法评论", ToastConst.warnStyle);
            return;
        }
        String content = commentView.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.toast("请说点啥再发送哦", ToastConst.warnStyle);
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = dateFormat.format(new Date());

        HashMap<String, String> params = new HashMap<>();
        params.put("postId", postId);
        params.put("userName", userName);
        params.put("content", content);
        params.put("time", time);

        AsyncHttpUtil.httpPost(Ports.commentAdd, params, new Callback() {
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
                        JSONObject jsObj = jsonObject.getJSONObject("data");
                        String id = jsObj.getString("id");
                        runOnUiThread(() -> {
                            curComment = new PostComment("0", userName, content, time);
                            curComment.setId(id);
                            allComments.add(0, curComment);
                            if (userName.equals(owner)) {
                                ownerComments.add(0, curComment);
                            }
                            mAdapter.setData(allComments);
                            comments_all.setTextColor(Color.BLACK);
                            comments_owner.setTextColor(Color.GRAY);

                            ToastUtil.toast("评论成功", ToastConst.successStyle);
                            commentView.setText("");
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

    public void doFavor() {
        btn_favor.setChecked(favored);

        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("postId", postId);
        params.put("thumbUp", String.valueOf(!favored));
        System.out.println(params);

        AsyncHttpUtil.httpPost(Ports.postsUp, params, new Callback() {
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
                            favored = !favored;
                            btn_favor.setChecked(favored);
                            favorCnt = favored ? favorCnt + 1 : favorCnt - 1;
                            post_detail_favorCnt.setText(String.valueOf(favorCnt));
                            if (favored) {
                                ToastUtil.toast("已收到客官的赞啦~", ToastConst.successStyle);
                            } else {
                                ToastUtil.toast("已取赞，我们会继续努力~", ToastConst.warnStyle);
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
        params.put("userName", userName);
        params.put("blogger", owner);
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
        params.put("userName", userName);
        params.put("postID", postId);
        params.put("content", content);
        AsyncHttpUtil.httpPost(Ports.postsReport, params, new Callback() {
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

    public void doDelete() {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", postId);
        AsyncHttpUtil.httpPost(Ports.postsDel, params, new Callback() {
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
                            ToastUtil.toast("删除成功", ToastConst.hintStyle);
                            resIntent.putExtra("isDelete", true);
                            finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

    }

    @Override
    public void finish() {
        resIntent.putExtra("isFollow", isFollow);
        resIntent.putExtra("favorCnt", favorCnt);
        setResult(Activity.RESULT_OK, resIntent);
        super.finish();
    }
}