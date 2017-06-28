package com.ofcat.whereboardgame.joinplay;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;

import java.util.List;

/**
 * Created by orangefaller on 2017/6/22.
 */

public class PlayerRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AdapterListener listener;

    private List<WaitPlayerRoomDTO> waitPlayerRoomDTOList;


    public PlayerRoomListAdapter() {
    }

    public void setDataList(List<WaitPlayerRoomDTO> list) {
        this.waitPlayerRoomDTOList = list;

    }

    public void setAdapterListener(AdapterListener listener) {
        this.listener = listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater(parent);

        View playerRoomItem = inflater.inflate(R.layout.item_player_room, parent, false);
        return new VHPlayerRoomItem(playerRoomItem);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHPlayerRoomItem) {

            String test = "Open";
//            if (position % 2 == 0) {
//                test = "Close";
//            }
            ((VHPlayerRoomItem) holder).render(test,
                    waitPlayerRoomDTOList.get(position).getDate(),
                    waitPlayerRoomDTOList.get(position).getStoreName(),
                    waitPlayerRoomDTOList.get(position).getInitiator());

            ((VHPlayerRoomItem) holder).setLocationTag(
                    waitPlayerRoomDTOList.get(position).getAddressTag());
        }
    }

    @Override
    public int getItemCount() {
        if (null != waitPlayerRoomDTOList) {
            return waitPlayerRoomDTOList.size();
        }
        return 0;
    }

    private LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

    private class VHPlayerRoomItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvLocation;
        private TextView tvTitle;
        private TextView tvSubTitle;
        private TextView tvBottomTitle;
        private TextView tvLocationTag;

        public VHPlayerRoomItem(View itemView) {
            super(itemView);

            tvLocation = (TextView) itemView.findViewById(R.id.tv_item_location);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tv_item_sub_title);
            tvBottomTitle = (TextView) itemView.findViewById(R.id.tv_item_bottom_title);
            tvLocationTag = (TextView) itemView.findViewById(R.id.tv_item_title_tag);

            itemView.setOnClickListener(this);
        }

        public void render(String location, String title, String subTitle, String bottomTitle) {
            tvLocation.setText(location);
            tvTitle.setText(title);
            tvSubTitle.setText(subTitle);
            tvBottomTitle.setText(bottomTitle);
        }

        public void setLocationTag(String locationTag) {
            tvLocationTag.setText(locationTag);
        }

        @Override
        public void onClick(View view) {
            if (listener != null && waitPlayerRoomDTOList != null && !waitPlayerRoomDTOList.isEmpty()) {
                listener.onClickItem(view, waitPlayerRoomDTOList.get(getAdapterPosition()));
            }

        }
    }

    public interface AdapterListener {

        void onClickItem(View view, WaitPlayerRoomDTO waitPlayerRoomDTO);

    }

}
