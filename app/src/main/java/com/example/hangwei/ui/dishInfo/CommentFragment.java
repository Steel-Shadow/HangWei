package com.example.hangwei.ui.dishInfo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.R;
import com.example.hangwei.app.AppActivity;
import com.example.hangwei.app.Comment;
import com.example.hangwei.app.Dish;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.ui.home.adapter.StatusAdapter;
import com.example.hangwei.utils.ToastUtil;
import com.example.hangwei.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

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
public final class CommentFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {
    private static final int MAX_LIST_ITEM_NUM = 20;
    private static final int LIST_ITEM_ADD_NUM = 5;

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private StatusAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dish_comment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.comment_refresh);
        mRecyclerView = findViewById(R.id.comment_list);

        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        TextView headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        headerView.setText("我是头部");
        headerView.setOnClickListener(v -> ToastUtil.toast("点击了头部", ToastConst.successStyle));

        TextView footerView = mRecyclerView.addFooterView(R.layout.picker_item);
        footerView.setText("我是尾部");
        footerView.setOnClickListener(v -> ToastUtil.toast("点击了尾部", ToastConst.errorStyle));

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
//        mAdapter.setData(getCommentData()); todo
    }

    /**
     * todo: 网络请求获取新评论
     */
    private List<Comment> getCommentData() {
        List<Comment> data = new ArrayList<>();

        HashMap<String, Object> params = new HashMap<>();
        params.put("campus", "学院路");
        params.put("type", 2); // [1：早餐]，[2：正餐]，[3：饮品]

        AsyncHttpUtil.httpPostForObject(Ports.dishComment, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("评论请求失败", ToastConst.errorStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        try {
                            ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        JSONObject object = jsonObject.getJSONObject("data");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return data;
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
        // todo: 点击具体条目，跳转到评论区
        Dish dish = mAdapter.getItem(position);
        ToastUtil.toast(dish.name + "跳转评论区", ToastConst.hintStyle);
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
//            mAdapter.setData(getCommentData());//todo:
            mRefreshLayout.finishRefresh();
        }, 1000);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
//            mAdapter.addData(getCommentData());todo
            mRefreshLayout.finishLoadMore();

            mAdapter.setLastPage(mAdapter.getCount() >= MAX_LIST_ITEM_NUM);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        }, 1000);
    }
}