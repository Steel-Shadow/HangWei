package com.example.hangwei.ui.dishInfo;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.app.Comment;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.base.BaseFragment;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.activity.DishInfoActivity;
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
public final class CommentFragment extends BaseFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private static final int MAX_LIST_ITEM_NUM = 10;
    private static final int LIST_ITEM_ADD_NUM = 5;

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView commentCount;
    private CommentAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dish_comment;
    }

    @Override
    protected void initView() {
        commentCount = findViewById(R.id.comment_count);

        mRefreshLayout = findViewById(R.id.comment_refresh);
        mRecyclerView = findViewById(R.id.comment_list);

        mAdapter = new CommentAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        updateCommentData(true, () -> {
        });
    }

    /**
     * 网络请求获取新评论
     */
    private void updateCommentData(boolean setElseAdd, Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        Activity activity = getActivity();
        assert activity != null;
        params.put("dishId", ((DishInfoActivity) activity).getDishId());

        AsyncHttpUtil.httpPost(Ports.dishComment, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("餐品评论获取失败，请稍候再试", ToastConst.warnStyle);
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
                            commentCount.setText(String.format(getString(R.string.comment_count), count));
                            if (setElseAdd) {
                                mAdapter.setData(comments);
                            } else {
                                mAdapter.addData(comments);
                            }
                            afterResponse.run();
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
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        // todo: 点击评论目前无互动
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
        updateCommentData(false, () -> {
            mRefreshLayout.finishLoadMore();
            mAdapter.setLastPage(mAdapter.getCount() >= MAX_LIST_ITEM_NUM);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        });
    }
}