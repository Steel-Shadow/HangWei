package com.example.hangwei.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.ui.home.element.Dish;

public class HotAdapter extends AppAdapter<Dish> {
    public HotAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final TextView mHotNum;
        private final TextView mName;


        private ViewHolder() {
            super(R.layout.search_hot_item);
            mHotNum = findViewById(R.id.hot_num);
            mName = findViewById(R.id.hot_dish_name);
        }

        @Override
        public void onBindView(int position) {
            Dish dish = getItem(position);
            mHotNum.setText(Integer.toString(position + 1));
            mName.setText(dish.name);

            if (position < 3) {
                mHotNum.setTextColor(0xFFFF0000);
            }
        }
    }
}
