package com.example.hangwei_administrator.ui.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hangwei_administrator.R;
import com.example.hangwei_administrator.app.AppActivity;
import com.example.hangwei_administrator.dialog.MessageDialog;
import com.example.hangwei_administrator.ui.commu.posts.detail.PostDetailActivity;
import com.example.hangwei_administrator.ui.home.element.Favorite;
import com.example.hangwei_administrator.base.BaseFragment;
import com.example.hangwei_administrator.base.FragmentPagerAdapter;
import com.example.hangwei_administrator.consts.ToastConst;
import com.example.hangwei_administrator.data.AsyncHttpUtil;
import com.example.hangwei_administrator.data.Ports;
import com.example.hangwei_administrator.ui.home.adapter.TabAdapter;
import com.example.hangwei_administrator.ui.home.fragment.CommentFragment;
import com.example.hangwei_administrator.ui.home.fragment.SideDishFragment;
import com.example.hangwei_administrator.ui.manage.dish.DishModifyActivity;
import com.example.hangwei_administrator.utils.ToastUtil;
import com.example.hangwei_administrator.widget.layout.XCollapsingToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * desc   : 餐品详情页
 */
public final class DishInfoActivity extends AppActivity
        implements TabAdapter.OnTabListener,
        ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {
    private String dishName;
    private String price;
    private String url;

    private String userName;
    private boolean favored;
    private int favorCnt;

    private String mDishId;
    private TextView mName;
    private TextView mPrice;
    private ImageView mPic;
    public Favorite mFavorite;
    private ToggleButton btn_favor;
    private TextView dish_favorCnt;
    private TextView dish_location;

    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private ImageButton dish_modify;
    private ImageButton dish_del;

    private CommentFragment commentFragment;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<BaseFragment<?>> mPagerAdapter;
    private Intent resIntent = new Intent();

    public static DishInfoActivity newInstance() {
        return new DishInfoActivity();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dish_info;
    }

    @Override
    protected void initView() {
        mName = findViewById(R.id.dish_info_name);
        mPrice = findViewById(R.id.dish_info_price);
        mPic = findViewById(R.id.dish_info_pic);
        dish_location = findViewById(R.id.dish_info_location);

        mTabView = findViewById(R.id.dish_info_middle);
        mViewPager = findViewById(R.id.dish_info_pager);

        btn_favor = findViewById(R.id.dish_info_favor);
        dish_favorCnt = findViewById(R.id.dish_info_favor_count);

        dish_modify = findViewById(R.id.dish_info_modify);
        dish_del = findViewById(R.id.dish_info_del);

        mPagerAdapter = new FragmentPagerAdapter<>(this);

        commentFragment = CommentFragment.newInstance();
        mPagerAdapter.addFragment(commentFragment, "评价");
        mPagerAdapter.addFragment(SideDishFragment.newInstance(), "餐品搭配");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(this.getContext());
        mTabView.setAdapter(mTabAdapter);

        btn_favor.setOnClickListener(view -> doFavor());

        dish_modify.setOnClickListener(view -> {
            Intent intent = new Intent(this, DishModifyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", mDishId);
            bundle.putString("name", dishName);
            bundle.putString("price", price);
            intent.putExtras(bundle);
            startActivityForResult(intent, (resultCode, data) -> {
                if (resultCode == RESULT_OK && data != null) {
                    dishName = data.getStringExtra("name");
                    price = data.getStringExtra("price");
                    url = data.getStringExtra("picUrl");
                    mName.setText(dishName);
                    mPrice.setText(price);
                    Glide.with(this).load(url).into(mPic);
                }
            });
        });

        dish_del.setOnClickListener(view -> {
            new MessageDialog.Builder(this)
                    .setTitle("删除餐品")
                    .setMessage("确认删除这个餐品？")
                    .setConfirm("仍然删除")
                    .setCancel("取消")
                    .setListener(dialog -> doDelete())
                    .show();
        });
    }

    @Override
    protected void initData() {
        userName = getSharedPreferences("BasePrefs", MODE_PRIVATE).getString("usedName", null);

        mTabAdapter.addItem("评价");
        mTabAdapter.addItem("餐品搭配");
        mTabAdapter.setOnTabListener(this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        dishName = bundle.getString("name");
        price = bundle.getString("price");
        url = bundle.getString("picUrl");

        if (!bundle.getBoolean("canMod", true)) {
            findViewById(R.id.dish_info_mod).setVisibility(View.GONE);
        }

        mDishId = bundle.getString("id");
        mName.setText(dishName);
        mPrice.setText(price);
        dish_location.setText(bundle.getString("location"));
        setFavor();
        favorCnt = bundle.getInt("favorCnt");
        dish_favorCnt.setText(String.valueOf(favorCnt));
        Glide.with(this).load(url).into(mPic);
        mFavorite = new Favorite(this, findViewById(R.id.dish_info_favorite), mDishId, Ports.dishFavChange, Ports.dishFavCheck);
    }

    /**
     * {@link TabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }

    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {

    }

    public String getDishId() {
        return mDishId;
    }

    public void setFavor() {
        System.out.println(mDishId);
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("dishID", mDishId);

        AsyncHttpUtil.httpPost(Ports.dishIsUp, params, new Callback() {
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
                        runOnUiThread(() -> {
                            try {
                                favored = jsonObject.getJSONObject("data").getBoolean("isThumbUp");
                                btn_favor.setChecked(favored);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        response.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    public void doFavor() {
        btn_favor.setChecked(favored);

        HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("dishID", mDishId);
        params.put("isThumbUp", String.valueOf(!favored));
        System.out.println(params);

        AsyncHttpUtil.httpPost(Ports.dishUp, params, new Callback() {
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
                        runOnUiThread(() -> {
                            favored = !favored;
                            btn_favor.setChecked(favored);
                            favorCnt = favored ? favorCnt + 1 : favorCnt - 1;
                            dish_favorCnt.setText(String.valueOf(favorCnt));
                            if (favored) {
                                ToastUtil.toast("已收到客官的赞啦~", ToastConst.successStyle);
                            } else {
                                ToastUtil.toast("已取赞，我们会继续努力~", ToastConst.warnStyle);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    public void doDelete() {
        HashMap<String, String> params = new HashMap<>();
        params.put("dishId", mDishId);
        AsyncHttpUtil.httpPost(Ports.dishDel, params, new Callback() {
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
                        runOnUiThread(() -> {
                            ToastUtil.toast("删除成功", ToastConst.hintStyle);
                            resIntent.putExtra("isDelete", true);
                            finish();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.body().close(); // 关闭响应体
                }
            }
        });
    }

    @Override
    public void finish() {
        resIntent.putExtra("favorCnt", favorCnt);
        resIntent.putExtra("commentCnt", commentFragment.getmCommentCount());
        resIntent.putExtra("dishName", dishName);
        resIntent.putExtra("price", price);
        resIntent.putExtra("url", url);
        setResult(Activity.RESULT_OK, resIntent);
        super.finish();
    }
}