package com.example.hangwei.ui.home.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hangwei.R;
import com.example.hangwei.app.TitleBarFragment;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.dialog.MessageDialog;
import com.example.hangwei.ui.commu.friends.detail.FriendDetailActivity;
import com.example.hangwei.ui.home.activity.FavoriteActivity;
import com.example.hangwei.ui.home.activity.HistoryActivity;
import com.example.hangwei.ui.home.activity.HomeActivity;
import com.example.hangwei.ui.home.activity.ImageCropActivity;
import com.example.hangwei.ui.home.activity.ImageSelectActivity;
import com.example.hangwei.ui.home.activity.SettingsActivity;
import com.example.hangwei.ui.init.FindPwdActivity;
import com.example.hangwei.ui.init.LoginActivity;
import com.example.hangwei.data.glide.GlideApp;
import com.example.hangwei.utils.ToastUtil;
import com.hjq.http.model.FileContentResolver;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MineFragment extends TitleBarFragment<HomeActivity> {
    private ImageView avatarView;
    private String userName;
    private Uri mAvatarUrl; /** 头像地址 */
    private TextView userNameView;
    private TextView email;
    private AppCompatButton btn_resetPwd;
    private ImageView historyView;
    private ImageView starView;
    private ImageView postsView;
    private TextView settingView;
    private TextView inviteView;
    private TextView contactView;
    private TextView supportView;
    private AppCompatButton btn_logout;

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
        email = findViewById(R.id.mine_email);
        btn_resetPwd = findViewById(R.id.mine_btn_reset);
        historyView = findViewById(R.id.mine_history);
        starView = findViewById(R.id.mine_star);
        postsView = findViewById(R.id.mine_posts);
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

        btn_resetPwd.setOnClickListener(view -> {
            startActivity(FindPwdActivity.class);
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

        historyView.setOnClickListener(view -> startActivity(HistoryActivity.class));

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

        settingView.setOnClickListener(view -> startActivity(SettingsActivity.class));

        inviteView.setOnClickListener(view -> {
            
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
        SharedPreferences prefs = getAttachActivity().getSharedPreferences("BasePrefs", MODE_PRIVATE);
        userName = prefs.getString("usedName", "小航兵");
        userNameView.setText(userName);
        email.setText(prefs.getString("usedEmail", "邮箱"));
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
        GlideApp.with(getActivity())
                .load(mAvatarUrl)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(avatarView);
    }
}
