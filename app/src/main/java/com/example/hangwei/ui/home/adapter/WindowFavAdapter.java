package com.example.hangwei.ui.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hangwei.R;
import com.example.hangwei.app.AppAdapter;
import com.example.hangwei.ui.home.element.Window;

public class WindowFavAdapter extends AppAdapter<Window> {

    public WindowFavAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        private final TextView mName;

        private ViewHolder() {
            super(R.layout.fav_item2);
            mName = findViewById(R.id.fav_item2_name);
        }

        @Override
        public void onBindView(int position) {
            Window window = getItem(position);
            mName.setText(window.name);
        }
    }
}
