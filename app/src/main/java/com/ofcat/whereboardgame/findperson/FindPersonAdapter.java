package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;

/**
 * Created by orangefaller on 2017/4/30.
 */

public class FindPersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private Context ctx;

    private String[] titleArray;

    public FindPersonAdapter(Context context) {
        this.ctx = context;
        this.titleArray = ctx.getResources().getStringArray(R.array.findperson_item_title_list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater(parent);
        View editView = inflater.inflate(R.layout.item_edit_info, parent, false);
        return new VHEditInfo(editView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHEditInfo){
            ((VHEditInfo) holder).setTvInfoTile(titleArray[position]);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    private LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

    private class VHEditInfo extends RecyclerView.ViewHolder{

        private TextView tvInfoTile;

        public VHEditInfo(View itemView) {
            super(itemView);
            tvInfoTile = (TextView) itemView.findViewById(R.id.tv_info_title);
            tvInfoTile.setText("Title");

        }

        public void setTvInfoTile(String title){
            tvInfoTile.setText(title);
        }

        public void render(){

        }
    }
}
