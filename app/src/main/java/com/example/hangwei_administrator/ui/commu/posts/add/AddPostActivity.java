package com.example.hangwei_administrator.ui.commu.posts.add;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.view.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class AddPostActivity extends AppActivity {
    String userName;
    String sTitle;
    String sTag;
    String sContent;

    EditText title;
    EditText content;
    private SubmitButton upload;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_post;
    }

    @Override
    protected void initView() {
        SharedPreferences prefs = getSharedPreferences("BasePrefs", MODE_PRIVATE);
        userName = prefs.getString("usedName", "小航兵");
        sTag = "";

        title = findViewById(R.id.post_title_input);
        content = findViewById(R.id.post_content_input);
        upload = findViewById(R.id.btn_send_post);

        TextView helpTag = findViewById(R.id.addpost_help);
        TextView recommendTag = findViewById(R.id.addpost_recommend);
        TextView hateTag = findViewById(R.id.addpost_hate);
        TextView otherTag = findViewById(R.id.addpost_other);

        helpTag.setOnClickListener(view -> {
            helpTag.setTextColor(Color.parseColor("#FFA500"));
            helpTag.setBackground(getDrawable(R.drawable.tag2));
            recommendTag.setTextColor(Color.parseColor("#FFC0CB"));
            recommendTag.setBackground(getDrawable(R.drawable.tag));
            hateTag.setTextColor(Color.parseColor("#FFC0CB"));
            hateTag.setBackground(getDrawable(R.drawable.tag));
            otherTag.setTextColor(Color.parseColor("#FFC0CB"));
            otherTag.setBackground(getDrawable(R.drawable.tag));
            sTag = "求助";
        });

        recommendTag.setOnClickListener(view -> {
            helpTag.setTextColor(Color.parseColor("#FFC0CB"));
            helpTag.setBackground(getDrawable(R.drawable.tag));
            recommendTag.setTextColor(Color.parseColor("#FFA500"));
            recommendTag.setBackground(getDrawable(R.drawable.tag2));
            hateTag.setTextColor(Color.parseColor("#FFC0CB"));
            hateTag.setBackground(getDrawable(R.drawable.tag));
            otherTag.setTextColor(Color.parseColor("#FFC0CB"));
            otherTag.setBackground(getDrawable(R.drawable.tag));
            sTag = "安利";
        });

        hateTag.setOnClickListener(view -> {
            helpTag.setTextColor(Color.parseColor("#FFC0CB"));
            helpTag.setBackground(getDrawable(R.drawable.tag));
            recommendTag.setTextColor(Color.parseColor("#FFC0CB"));
            recommendTag.setBackground(getDrawable(R.drawable.tag));
            hateTag.setTextColor(Color.parseColor("#FFA500"));
            hateTag.setBackground(getDrawable(R.drawable.tag2));
            otherTag.setTextColor(Color.parseColor("#FFC0CB"));
            otherTag.setBackground(getDrawable(R.drawable.tag));
            sTag = "吐槽";
        });

        otherTag.setOnClickListener(view -> {
            helpTag.setTextColor(Color.parseColor("#FFC0CB"));
            helpTag.setBackground(getDrawable(R.drawable.tag));
            recommendTag.setTextColor(Color.parseColor("#FFC0CB"));
            recommendTag.setBackground(getDrawable(R.drawable.tag));
            hateTag.setTextColor(Color.parseColor("#FFC0CB"));
            hateTag.setBackground(getDrawable(R.drawable.tag));
            otherTag.setTextColor(Color.parseColor("#FFA500"));
            otherTag.setBackground(getDrawable(R.drawable.tag2));
            sTag = "其它";
        });

        upload.setOnClickListener(view -> {
            sTitle = title.getText().toString();
            if (sTitle.length() == 0) {
                toast("请输入文章标题");
                return;
            }
            if (sTag.length() == 0) {
                toast("请选择文章标签");
                return;
            }
            sContent = content.getText().toString();
            if (sContent.length() == 0) {
                toast("请输入文章正文");
                return;
            }
            doAddPost();
        });
    }

    @Override
    protected void initData() {

    }

    public void doAddPost() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = dateFormat.format(new Date());
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("title", sTitle);
        params.put("tag", sTag);
        params.put("content", sContent);
        params.put("time", time);

        AsyncHttpUtil.httpPost(Ports.postsAddPost, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDelayed(() -> {
                    upload.showError(3000);
                    ToastUtil.toast("服务器有一些小问题~", ToastConst.warnStyle);
                }, 1000);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        postDelayed(() -> {
                            try {
                                ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            upload.showError(3000);
                        }, 1000);
                    } else {
                        JSONObject jsObj = jsonObject.getJSONObject("data");
                        postDelayed(() -> {
                            upload.showSucceed();
                            postDelayed(() -> {
                                ToastUtil.toast("发帖成功", ToastConst.successStyle);
                                Intent resIntent = new Intent();
                                try {
                                    resIntent.putExtra("postID", jsObj.getString("id"));
                                    resIntent.putExtra("title", sTitle);
                                    resIntent.putExtra("tag", sTag);
                                    resIntent.putExtra("time", time);
                                    setResult(Activity.RESULT_OK, resIntent);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                finish();
                            }, 1000);
                        }, 1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    private void toast(String str) {
        Toast.makeText(AddPostActivity.this, str, Toast.LENGTH_SHORT).show();
        upload.reset();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}