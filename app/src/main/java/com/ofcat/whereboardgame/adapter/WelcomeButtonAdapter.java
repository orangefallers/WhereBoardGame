package com.ofcat.whereboardgame.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ofcat.whereboardgame.R;

/**
 * Created by orangefaller on 2017/9/12.
 */

public class WelcomeButtonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private int buttonPadding;

    private String[] buttonTextArray;

    public WelcomeButtonAdapter(Context context) {
        this.context = context;
        buttonPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12, //dp值
                context.getResources().getDisplayMetrics());

        buttonTextArray = context.getResources().getStringArray(R.array.main_welcome_button_list);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WelcomeButton(new Button(context));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WelcomeButton) {
            ((WelcomeButton) holder).setText(buttonTextArray[position]);
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    private class WelcomeButton extends RecyclerView.ViewHolder {

        private Button btnWelcome;

        public WelcomeButton(View itemView) {
            super(itemView);
            btnWelcome = (Button) itemView;
            btnWelcome.setBackgroundResource(R.drawable.selector_welcome_button);
            btnWelcome.setTextColor(ContextCompat.getColor(context, R.color.white));
            btnWelcome.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
            btnWelcome.setTextSize(TypedValue.COMPLEX_UNIT_SP ,12);
        }

        public void setText(String text) {
            btnWelcome.setText(text);
        }
    }
}
