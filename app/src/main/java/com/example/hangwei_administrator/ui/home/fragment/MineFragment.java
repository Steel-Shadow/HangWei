package com.example.hangwei_administrator.ui.home.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.TitleBarFragment;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.dialog.InputDialog;
import com.example.hangwei_administrator.dialog.MessageDialog;
import com.example.hangwei_administrator.dialog.ShareDialog;
import com.example.hangwei_administrator.ui.commu.friends.detail.FriendDetailActivity;
import com.example.hangwei_administrator.ui.commu.friends.list.FriendsListActivity;
import com.example.hangwei_administrator.ui.home.activity.FavoriteActivity;
import com.example.hangwei_administrator.ui.home.activity.HomeActivity;
import com.example.hangwei_administrator.ui.home.activity.ImageCropActivity;
import com.example.hangwei_administrator.ui.home.activity.ImageSelectActivity;
import com.example.hangwei_administrator.ui.home.activity.SettingsActivity;
import com.example.hangwei_administrator.ui.init.LoginActivity;
import com.example.hangwei_administrator.data.glide.GlideApp;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.hjq.http.model.FileContentResolver;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MineFragment extends TitleBarFragment<HomeActivity> {
    private ImageView avatarView;
    private String userName;
    private Uri mAvatarUrl; /** 头像地址 */
    private TextView userNameView;
    private ImageView starView;
    private ImageView postsView;
    private ImageView followView;
    private TextView settingView;
    private TextView inviteView;
    private TextView contactView;
    private TextView supportView;
    private AppCompatButton btn_logout;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        avatarView = findViewById(R.id.mine_avatar);
        userNameView = findViewById(R.id.mine_userName);
        starView = findViewById(R.id.mine_star);
        postsView = findViewById(R.id.mine_posts);
        followView = findViewById(R.id.mine_follows);
        settingView = findViewById(R.id.mine_setting);
        inviteView = findViewById(R.id.mine_invite);
        contactView = findViewById(R.id.mine_contact);
        supportView = findViewById(R.id.mine_support);
        btn_logout = findViewById(R.id.mine_btn_logout);

        avatarView.setOnClickListener(view -> {
            XXPermissions.with(this)
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (allGranted) {
                                ImageSelectActivity.start(MineFragment.this.getAttachActivity(), data -> {
                                    // 裁剪头像
                                    cropImageFile(new File(data.get(0)));
                                });
                            }
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            OnPermissionCallback.super.onDenied(permissions, doNotAskAgain);
                            if (doNotAskAgain) {
                                ToastUtil.toast("被永久拒绝授权，请手动授权", ToastConst.errorStyle);
                                XXPermissions.startPermissionActivity(getContext(), permissions);
                            } else {
                                ToastUtil.toast("存取权限获取失败", ToastConst.warnStyle);
                            }
                        }
                    });
        });

        btn_logout.setOnClickListener(view -> {
            SharedPreferences prefs = getAttachActivity().getSharedPreferences("BasePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLogin", false);
            editor.commit();
            startActivity(LoginActivity.class);
            finish();
        });

        starView.setOnClickListener(view -> startActivity(FavoriteActivity.class));

        postsView.setOnClickListener(view -> {
            Intent intent = new Intent(this.getAttachActivity(), FriendDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user", userName);
            bundle.putString("userName", userName);
            bundle.putString("avatar", String.valueOf(mAvatarUrl));
            bundle.putBoolean("isFollow", true);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        followView.setOnClickListener(view -> startActivity(FriendsListActivity.class));

        settingView.setOnClickListener(view -> startActivity(SettingsActivity.class));

        inviteView.setOnClickListener(view -> {
            new ShareDialog.Builder(getAttachActivity())
                    .show();
        });

        contactView.setOnClickListener(view -> {
            new MessageDialog.Builder(getActivity())
                    .setTitle("温馨提示")
                    .setMessage("欢迎联系开发者，我的邮箱是 21371429@buaa.edu.cn")
                    .setConfirm("确定")
                    .setCancel("取消")
                    .setListener(dialog1 -> {
                        new InputDialog.Builder(getAttachActivity())
                                .setTitle("意见反馈")
                                .setHint("您的意见反馈完全匿名，欢迎畅所欲言。")
                                .setConfirm("确定")
                                .setCancel("取消")
                                .setListener((dialog, content) -> doFeedBack(content))
                                .show();
                    })
                    .show();
        });

        supportView.setOnClickListener(view -> {
            new MessageDialog.Builder(getAttachActivity())
                    .setTitle("支持我们")
                    .setMessage("如果老师和助教学长们觉得这个项目很棒，也可以试着点一下呀，作为对于开发者的激励")
                    .setConfirm("支付宝")
                    .setCancel(null)
                    .setListener(dialog -> {
                        ToastUtil.toast("<航味>因为有你的支持而能够不断更新、完善，非常感谢支持！", ToastConst.successStyle);
                        postDelayed(() -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://qr.alipay.com/fkx19951gtjzxvqotls3e11"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                ToastUtil.toast("打开支付宝失败，你可能还没有安装支付宝客户端", ToastConst.errorStyle);
                            }
                        }, 2000);
                    })
                    .show();
        });
    }

    @Override
    protected void initData() {
        prefs = getAttachActivity().getSharedPreferences("BasePrefs", MODE_PRIVATE);
        editor = prefs.edit();

        userName = prefs.getString("usedName", "小航兵");
        userNameView.setText(userName);

        String avatar = prefs.getString("usedAvatar", null);
        if (!TextUtils.isEmpty(avatar)) {
            mAvatarUrl = Uri.parse(avatar);
            GlideApp.with(getActivity())
                    .load(mAvatarUrl)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(avatarView);
        }
    }

    private void doFeedBack(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.toast("请说些什么哦~", ToastConst.warnStyle);
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("content", content);
        AsyncHttpUtil.httpPost(Ports.userFeedback, params, new Callback() {
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
                        ToastUtil.toast("已经收到您的反馈，您的每一份建议都在帮助<航味>进步，感谢支持", ToastConst.hintStyle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    /** 裁剪图片 */
    private void cropImageFile(File sourceFile) {
        ImageCropActivity.start(this.getAttachActivity(), sourceFile, 1, 1,
                new ImageCropActivity.OnCropListener() {
                    @Override
                    public void onSucceed(Uri fileUri, String fileName) {
                        File outputFile;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            outputFile = new FileContentResolver(getActivity(), fileUri, fileName);
                        } else {
                            try {
                                outputFile = new File(new URI(fileUri.toString()));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                                outputFile = new File(fileUri.toString());
                            }
                        }
                        updateCropImage(outputFile, true);
                    }

                    @Override
                    public void onError(String details) {
                        // 没有的话就不裁剪，直接上传原图片
                        // 但是这种情况极其少见，可以忽略不计
                        updateCropImage(sourceFile, false);
                    }
                });
    }

    /** 上传裁剪后的图片 */
    private void updateCropImage(File file, boolean deleteFile) {
        if (file instanceof FileContentResolver) {
            mAvatarUrl = ((FileContentResolver) file).getContentUri();
        } else {
            mAvatarUrl = Uri.fromFile(file);
        }
        uploadImage(getAbsolutePathFromContentUri(getAttachActivity(), mAvatarUrl));
    }

    private void uploadImage(String filepath) {
        AsyncHttpUtil.postImage(filepath, new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getBoolean("status")) {
                        getAttachActivity().runOnUiThread(() -> {
                            try {
                                String url = jsonObject.getJSONObject("data").getJSONObject("links").getString("url");
                                mAvatarUrl = Uri.parse(url);
                                editor.putString("usedAvatar", url);
                                editor.apply();
                                updateAvatar(url);
                                GlideApp.with(getActivity())
                                        .load(mAvatarUrl)
                                        .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                                        .into(avatarView);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        response.close();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("图片上传失败", ToastConst.warnStyle);
            }
        });
    }

    private void updateAvatar(String url) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", prefs.getString("usedID", null));
        params.put("avatar", url);

        AsyncHttpUtil.httpPost(Ports.userModifyUrl, params, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast("服务器有一点小问题~，请稍候再试", ToastConst.warnStyle);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println(response.message());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("code") == 0) {
                        ToastUtil.toast(jsonObject.getString("msg"), ToastConst.errorStyle);
                    } else {
                        ToastUtil.toast("上传成功", ToastConst.successStyle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    private static String getAbsolutePathFromContentUri(Context context, Uri contentUri) {
        String absolutePath = null;
        String[] projection = {MediaStore.MediaColumns.DATA};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(contentUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            absolutePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return absolutePath;
    }
}
