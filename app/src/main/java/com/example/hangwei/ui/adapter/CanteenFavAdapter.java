package com.example.hangwei.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.app.Canteen;

public class CanteenFavAdapter extends AppAdapter<Canteen> {

    public CanteenFavAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final TextView mName;
        private final ImageView mFoodPic;

        private ViewHolder() {
            super(R.layout.fav_item);
            mName = findViewById(R.id.fav_item_name);
            mFoodPic = findViewById(R.id.fav_item_pic);
        }

        @Override
        public void onBindView(int position) {
            Canteen canteen = getItem(position);

            mName.setText(canteen.name);
            Glide.with(this.getItemView()).load(canteen.picUrl).into(mFoodPic);
        }
    }
}
