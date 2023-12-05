package com.example.hangwei.ui.commu;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.example.hangwei.R;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.ui.commu.friends.list.FriendsListActivity;
import com.example.hangwei.ui.commu.posts.list.PostsListActivity;
import com.example.hangwei.ui.home.activity.HomeActivity;

public class CommuFragment extends TitleBarFragment<HomeActivity> {

    private CommunicateViewModel mViewModel;
    AppCompatButton btn_comu_friends;
    AppCompatButton btn_comu_posts;

    public static CommuFragment newInstance() {
        return new CommuFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_communicate;
    }

    @Override
    protected void initView() {
        btn_comu_friends = findViewById(R.id.btn_comu_friends);
        btn_comu_posts = findViewById(R.id.btn_comu_posts);

        btn_comu_friends.setOnClickListener(view1 -> {
            // 聊天系统
            startActivity(FriendsListActivity.class);
        });

        btn_comu_posts.setOnClickListener(view2 -> {
            // 帖子系统
            startActivity(PostsListActivity.class);
        });
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommunicateViewModel.class);
        // TODO: Use the ViewModel
    }

}