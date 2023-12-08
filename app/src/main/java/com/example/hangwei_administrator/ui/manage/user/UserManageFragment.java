package com.example.hangwei_administrator.ui.manage.user;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.TitleBarFragment;
import com.example.hangwei_administrator.base.BaseAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.home.activity.HomeActivity;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.layout.XCollapsingToolbarLayout;
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

public class UserManageFragment extends TitleBarFragment<HomeActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private List<User> allUsers = new ArrayList<>();
    private UserAdapter mAdapter;

    public static UserManageFragment newInstance() {
        return new UserManageFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_user;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.manage_user_layout);
        mRecyclerView = findViewById(R.id.manage_user_list);
        mAdapter = new UserAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        getUsers(() -> {});
    }

    public void getUsers(Runnable afterResponse) {
        HashMap<String, String> params = new HashMap<>();
        AsyncHttpUtil.httpPost(Ports.userGetAll, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("网络错误", ToastConst.warnStyle);
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
                        if (!jsonObject.isNull("data")) {
                            getAttachActivity().runOnUiThread(() -> {
                                try {
                                    setUsers(jsonObject.getJSONArray("data"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                afterResponse.run();
                            });
                        }
                    }
                } catch (JSONException e) {
                    getAttachActivity().runOnUiThread(afterResponse);
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUsers(JSONArray jsonArray) throws JSONException {
        allUsers.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsObj = (JSONObject) jsonArray.get(i);
            User user = new User(jsObj.getString("id"), jsObj.getString("userName"),
                    jsObj.getString("email"), jsObj.getString("password"),
                    jsObj.getString("avatar"), jsObj.getBoolean("isSilence"));
            allUsers.add(user);
        }
        mAdapter.setData(allUsers);
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        User user = mAdapter.getItem(position);
        Intent intent = new Intent(getAttachActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("avatar", String.valueOf(user.avatar));
        bundle.putString("userName", user.userName);
        bundle.putString("email", user.email);
        bundle.putString("pwd", user.pwd);
        bundle.putString("time", user.id);
        bundle.putBoolean("isSilence", user.isSilence);
        intent.putExtras(bundle);

        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK && data != null) {
                // 删除
                if (data.getBooleanExtra("isDelete", false)) {
                    mAdapter.removeItem(position);
                    return;
                }

                // 禁言
                user.setSilence(data.getBooleanExtra("isSilence", user.isSilence));
                mAdapter.setItem(position, user);
            }
        });
    }

    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getUsers(() -> mRefreshLayout.finishLoadMore());
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getUsers(() -> mRefreshLayout.finishRefresh());
    }
}
