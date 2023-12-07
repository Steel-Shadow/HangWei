package com.example.hangwei.ui.commu.friends.list;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.data.AsyncHttpUtil;
import com.example.hangwei.data.Ports;
import com.example.hangwei.data.glide.GlideApp;
import com.example.hangwei.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FriendAdapter extends AppAdapter<Friend> {
    public FriendAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private ImageView avatar;
        private TextView userName;
        private ToggleButton btn_follow;
        private TextView follow_msg;

        private ViewHolder() {
            super(R.layout.item_friend);
            avatar = findViewById(R.id.friend_avatar);
            userName = findViewById(R.id.friend_user);
            btn_follow = findViewById(R.id.friend_follow);
            follow_msg = findViewById(R.id.friend_followdetail);
        }

        @Override
        public void onBindView(int position) {
            Friend friend = getItem(position);
            userName.setText(friend.userName);
            GlideApp.with(getContext())
                    .load(friend.avatar)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(avatar);

            btn_follow.setChecked(friend.isFollow);
            if (friend.isFollow) {
                follow_msg.setText("= 已关注");
                follow_msg.setTextColor(Color.GRAY);
                follow_msg.setBackground(getDrawable(R.drawable.bg_vary_corner));
            } else {
                follow_msg.setText("+关注");
                follow_msg.setTextColor(Color.parseColor("#FF83FA"));
                follow_msg.setBackground(getDrawable(R.drawable.bg_purple_corner));
            }
            follow_msg.setOnClickListener(view -> doFollow(friend));
        }

        public void doFollow(Friend friend) {
            HashMap<String, String> params = new HashMap<>();
            params.put("userName", friend.user);
            params.put("blogger", friend.userName);
            params.put("isFollow", String.valueOf(!friend.isFollow));
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
                            friend.setFollow();
                            btn_follow.setChecked(friend.isFollow);
                            if (friend.isFollow) {
                                follow_msg.setText("= 已关注");
                                follow_msg.setTextColor(Color.GRAY);
                                follow_msg.setBackground(getDrawable(R.drawable.bg_vary_corner));
                            } else {
                                follow_msg.setText("+关注");
                                follow_msg.setTextColor(Color.parseColor("#FF83FA"));
                                follow_msg.setBackground(getDrawable(R.drawable.bg_purple_corner));
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
    }
}
