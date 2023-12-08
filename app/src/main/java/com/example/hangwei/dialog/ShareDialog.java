package com.example.hangwei.dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hangwei.base.BaseAdapter;
import com.example.hangwei.base.BaseDialog;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.consts.ToastConst;
import com.example.hangwei.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *    desc   : 分享对话框
 */
public final class ShareDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        private final RecyclerView mRecyclerView;
        private final ShareAdapter mAdapter;
        private final ShareBean mCopyLink;

        public Builder(Activity activity) {
            super(activity);

            setContentView(R.layout.share_dialog);

            final List<ShareBean> data = new ArrayList<>();
            data.add(new ShareBean(getDrawable(R.drawable.share_wechat_ic), getString(R.string.share_platform_wechat)));
            data.add(new ShareBean(getDrawable(R.drawable.share_moment_ic), getString(R.string.share_platform_moment)));
            data.add(new ShareBean(getDrawable(R.drawable.share_qq_ic), getString(R.string.share_platform_qq)));
            data.add(new ShareBean(getDrawable(R.drawable.share_qzone_ic), getString(R.string.share_platform_qzone)));

            mCopyLink = new ShareBean(getDrawable(R.drawable.share_link_ic), getString(R.string.share_platform_link));
            data.add(mCopyLink);

            mAdapter = new ShareAdapter(activity);
            mAdapter.setData(data);
            mAdapter.setOnItemClickListener(this);

            mRecyclerView = findViewById(R.id.rv_share_list);
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, data.size()));
            mRecyclerView.setAdapter(mAdapter);
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            ShareBean item = mAdapter.getItem(position);
            if (item.shareName.equals("复制链接")) {
                ToastUtil.toast("已复制到剪贴板", ToastConst.successStyle);
                getSystemService(ClipboardManager.class).setPrimaryClip(ClipData.newPlainText("url", "https://github.com/Steel-Shadow/HangWei"));
            } else {
                ToastUtil.toast("开发者正在加紧申请创建第三方平台，无奈过去半月余仍未审批通过。\n" +
                        "后续审批通过我们将第一时间进行版本更新，谢谢您的体谅", ToastConst.warnStyle);
            }
            dismiss();
        }
    }

    private static class ShareAdapter extends AppAdapter<ShareBean> {

        private ShareAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppAdapter<?>.ViewHolder {

            private final ImageView mImageView;
            private final TextView mTextView;

            private ViewHolder() {
                super(R.layout.share_item);
                mImageView = findViewById(R.id.iv_share_image);
                mTextView = findViewById(R.id.tv_share_text);
            }

            @Override
            public void onBindView(int position) {
                ShareBean bean = getItem(position);
                mImageView.setImageDrawable(bean.shareIcon);
                mTextView.setText(bean.shareName);
            }
        }
    }

    private static class ShareBean {

        /** 分享图标 */
        final Drawable shareIcon;
        /** 分享名称 */
        final String shareName;

        private ShareBean(Drawable icon, String name) {
            shareIcon = icon;
            shareName = name;
        }
    }
}