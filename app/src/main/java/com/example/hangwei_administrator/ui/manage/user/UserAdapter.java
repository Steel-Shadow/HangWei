package com.example.hangwei_administrator.ui.manage.user;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.data.glide.GlideApp;
import com.example.hangwei_administrator.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserAdapter extends AppAdapter<User> {
    public UserAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private ImageView avatar;
        private TextView userName;
        private TextView email;
        private TextView pwd;
        private TextView time;
        private ToggleButton btn_silence;

        private ViewHolder() {
            super(R.layout.item_user);
            avatar = findViewById(R.id.item_user_avatar);
            userName = findViewById(R.id.item_user_name);
            email = findViewById(R.id.item_user_email);
            pwd = findViewById(R.id.item_user_pwd);
            time = findViewById(R.id.item_user_time);
            btn_silence = findViewById(R.id.item_user_silence);
        }

        @Override
        public void onBindView(int position) {
            User user = getItem(position);
            userName.setText(user.userName);
            email.setText(user.email);
            pwd.setText(user.pwd);
            time.setText(user.id);
            GlideApp.with(getContext())
                    .load(user.avatar)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(avatar);

            btn_silence.setChecked(user.isSilence);
            if (user.isSilence) {
                btn_silence.setTextColor(Color.parseColor("#a0FF0000"));
            } else {
                btn_silence.setTextColor(Color.parseColor("#a01E90FF"));
            }
            btn_silence.setOnClickListener(view -> {
                btn_silence.setChecked(!btn_silence.isChecked());
                doSilence(user);
            });
        }

        public void doSilence(User user) {
            HashMap<String, String> params = new HashMap<>();
            params.put("userName", user.userName);
            params.put("silence", String.valueOf(!user.isSilence));
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
                            user.setSilence(!user.isSilence);
                            btn_silence.setChecked(user.isSilence);
                            if (user.isSilence) {
                                btn_silence.setTextColor(Color.parseColor("#a0FF0000"));
                                ToastUtil.toast("已对用户 <" + user.userName + "> 进行禁言", ToastConst.warnStyle);
                            } else {
                                btn_silence.setTextColor(Color.parseColor("#a01E90FF"));
                                ToastUtil.toast("用户 <" + user.userName + "> 已经解禁", ToastConst.successStyle);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
