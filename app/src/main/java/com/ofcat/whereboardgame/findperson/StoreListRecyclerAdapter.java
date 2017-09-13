package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.dataobj.StoreInfoDTO;

import java.util.ArrayList;

/**
 * Created by orangefaller on 2017/9/13.
 */

public class StoreListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = StoreListRecyclerAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<StoreInfoDTO> storeInfos;

    private AdapterListener listener;

    public StoreListRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setStoreData(ArrayList<StoreInfoDTO> storeData) {
        this.storeInfos = storeData;
    }

    public void setAdapterListener(AdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater(parent);
        View storeInfoView = inflater.inflate(R.layout.item_board_game_store_info, parent, false);
        return new VHStoreInfo(storeInfoView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHStoreInfo) {
            ((VHStoreInfo) holder).render(storeInfos.get(position).getStoreName(), storeInfos.get(position).getAddress());
        }
    }

    @Override
    public int getItemCount() {
        return storeInfos != null ? storeInfos.size() : 0;
    }

    private LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

    private class VHStoreInfo extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle;
        private TextView tvSubTitle;

        public VHStoreInfo(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tv_item_sub_title);
            itemView.setOnClickListener(this);

        }

        public void render(String title, String subTitle) {
            tvTitle.setText(title);
            tvSubTitle.setText(subTitle);
        }

        @Override
        public void onClick(View view) {
            if (listener != null && storeInfos != null && !storeInfos.isEmpty()) {
                listener.onClickItem(view, storeInfos.get(getAdapterPosition()));
            }
        }
    }

    public interface AdapterListener {
        void onClickItem(View view, StoreInfoDTO storeInfoDTO);
    }
}
