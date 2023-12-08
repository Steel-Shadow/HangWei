package com.example.hangwei.ui.home.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.ui.home.element.Comment;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.activity.DishInfoActivity;
import com.example.hangwei.ui.home.adapter.CommentAdapter;
import com.example.hangwei.utils.ToastUtil;
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

/**
 * desc   : 餐品详情页 的 评论区
 */
public final class CommentFragment extends TitleBarFragment<DishInfoActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private static final int MAX_LIST_ITEM_NUM = Integer.MAX_VALUE;
    private static final int LIST_ITEM_ADD_NUM = 10;

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    private EditText mAddComment;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mCommentCountTextView;
    private int mCommentCount;
    private CommentAdapter mAdapter;

    private String userId;
    private String userPicUrl;
    private String userName;

    @Override
    protected int getLayoutId() {
        return R.layout.dish_comment;
    }

    @Override
    protected void initView() {
        mCommentCountTextView = findViewById(R.id.comment_count);
        mAddComment = findViewById(R.id.add_comment);

        mRefreshLayout = findViewById(R.id.comment_refresh);
        mRecyclerView = findViewById(R.id.comment_list);

        mAdapter = new CommentAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);

        mAddComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(getView());
                // 隐藏光标
                mAddComment.setCursorVisible(false);

                fetchPer(() -> {
                    if (!TextUtils.isEmpty(mAddComment.getText().toString())) {
                        addComment(userId, mAddComment.getText().toString());
                    }
                });
            }
            return false;
        });
    }

    @Override
    protected void initData() {
        SharedPreferences base = getContext().getSharedPreferences("BasePrefs", MODE_PRIVATE);
        userId = base.getString("usedID", "null");
        userPicUrl = base.getString("usedAvatar", "null");
        userName = base.getString("usedName", "我");
        updateCommentData(true, () -> {
        });
    }

    public int getmCommentCount() {
        return mCommentCount;
    }

    /**
     * 网络请求获取新评论
     */
    private void updateCommentData(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        Activity activity = getActivity();
        assert activity != null;
        params.put("dishId", ((DishInfoActivity) activity).getDishId());

        AsyncHttpUtil.httpPost(Ports.dishCommentGet, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("餐品评论获取失败，请稍候再试", ToastConst.warnStyle);
                getAttachActivity().runOnUiThread(afterResponse);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<Comment> comments = new ArrayList<>();

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        activity.runOnUiThread(afterResponse);
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        int count = data.getInt("count");
                        JSONArray JSONComments = data.getJSONArray("comments");
                        for (int i = 0; i < JSONComments.length(); i++) {
                            JSONObject o = JSONComments.getJSONObject(i);
                            String userName = o.getString("userName");
                            String picUrl = o.getString("picUrl");
                            String date = o.getString("date");
                            String text = o.getString("text");
                            comments.add(new Comment(userName, picUrl, date, text));
                        }
                        activity.runOnUiThread(() -> {
                            mCommentCount = count;
                            mCommentCountTextView.setText(String.format(getString(R.string.comment_count), count));
                            if (setElseAdd) {
                                mAdapter.setData(comments);
                            } else {
                                mAdapter.addData(comments);
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
                            ToastUtil.toast("您已被禁言，无法评论", ToastConst.warnStyle);
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

    private void addComment(String userId, String comment) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("dishId", ((DishInfoActivity) getActivity()).getDishId());
        params.put("comment", comment);

        AsyncHttpUtil.httpPost(Ports.dishCommentAdd, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("餐品添加获取失败", ToastConst.warnStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        getAttachActivity().runOnUiThread(() -> {
                            mCommentCount += 1;
                            mCommentCountTextView.setText(String.format(getString(R.string.comment_count), mCommentCount));
                            mAdapter.addItem(0, new Comment(userName, userPicUrl, "刚刚", mAddComment.getText().toString()));
                            mAddComment.setText("");
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

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView RecyclerView对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
//        Comment comment = mAdapter.getItem(position);
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updateCommentData(true, () -> mRefreshLayout.finishRefresh());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mRefreshLayout.finishLoadMore();
    }
}