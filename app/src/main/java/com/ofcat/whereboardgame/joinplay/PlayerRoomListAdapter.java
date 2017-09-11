package com.ofcat.whereboardgame.joinplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;

import java.util.List;
import java.util.Locale;

/**
 * Created by orangefaller on 2017/6/22.
 */

public class PlayerRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AdapterListener listener;

    private List<WaitPlayerRoomDTO> waitPlayerRoomDTOList;

    private Context context;

    public PlayerRoomListAdapter(Context context) {
        this.context = context;
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

            String date = String.format("開團日：%s", waitPlayerRoomDTOList.get(position).getDate());
            ((VHPlayerRoomItem) holder).render(waitPlayerRoomDTOList.get(position).getRoomStatus(),
                    date,
                    waitPlayerRoomDTOList.get(position).getStoreName(),
                    waitPlayerRoomDTOList.get(position).getInitiator());

            ((VHPlayerRoomItem) holder).setLocationTag(
                    waitPlayerRoomDTOList.get(position).getAddressTag());


            String currentPersonStr = getCurrentPersonStr(waitPlayerRoomDTOList.get(position).getCurrentPerson());
            if (currentPersonStr.equals("")) {
                ((VHPlayerRoomItem) holder).setCurrentPersonText("", false);
            } else {
                ((VHPlayerRoomItem) holder).setCurrentPersonText(currentPersonStr, true);
            }

            String needPersonStr = getNeedPersonStr(waitPlayerRoomDTOList.get(position).getNeedPerson());
            if (needPersonStr.equals("")) {
                ((VHPlayerRoomItem) holder).setNeedPersonText("", false);
            } else {
                ((VHPlayerRoomItem) holder).setNeedPersonText(needPersonStr, true);

            }
        }
    }

    @Override
    public int getItemCount() {
        if (null != waitPlayerRoomDTOList) {
            return waitPlayerRoomDTOList.size();
        }
        return 0;
    }

    private String getCurrentPersonStr(int currentPerson) {
        if (currentPerson > 0 && currentPerson < 21) {
            return String.format(Locale.getDefault(), "目前人數：%d人", currentPerson);
        } else if (currentPerson >= 21) {
            return String.format(Locale.getDefault(), "目前人數：%d人以上", currentPerson);
        } else {
            return "";
        }
    }

    private String getNeedPersonStr(int currentPerson) {
        if (currentPerson > 0 && currentPerson < 21) {
            return String.format(Locale.getDefault(), "徵求人數：%d人", currentPerson);
        } else if (currentPerson >= 21) {
            return String.format(Locale.getDefault(), "徵求人數：%d人以上", currentPerson);
        } else {
            return "";
        }
    }

    private LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

    private class VHPlayerRoomItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvLocation;
        private TextView tvTitle;
        private TextView tvSubTitle;
        private TextView tvBottomTitle;
        private TextView tvBottomSubTitleLeft;
        private TextView tvBottomSubTitleRight;
        private TextView tvLocationTag;

        public VHPlayerRoomItem(View itemView) {
            super(itemView);

            tvLocation = (TextView) itemView.findViewById(R.id.tv_item_location);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tv_item_sub_title);
            tvBottomTitle = (TextView) itemView.findViewById(R.id.tv_item_bottom_title);
            tvBottomSubTitleLeft = (TextView) itemView.findViewById(R.id.tv_item_bottom_sub_title_left);
            tvBottomSubTitleRight = (TextView) itemView.findViewById(R.id.tv_item_bottom_sub_title_right);
            tvLocationTag = (TextView) itemView.findViewById(R.id.tv_item_title_tag);

            itemView.setOnClickListener(this);
        }

        public void render(String location, String title, String subTitle, String bottomTitle) {
            tvLocation.setText(location);
            tvTitle.setText(title);
            tvSubTitle.setText(subTitle);
            tvBottomTitle.setText(bottomTitle);
        }

        public void render(int roomStatus, String title, String subTitle, String bottomTitle) {
            switch (roomStatus) {
                case 1:
                    tvLocation.setText("缺");
                    tvLocation.setBackgroundResource(R.drawable.ring_red);
                    tvLocation.setTextColor(context.getResources().getColor(R.color.pink));
                    break;
                case 2:
                    tvLocation.setText("滿");
                    tvLocation.setBackgroundResource(R.drawable.ring_green);
                    tvLocation.setTextColor(context.getResources().getColor(R.color.green_deep));
                    break;
                default:
                    break;
            }

            tvTitle.setText(title);
            tvSubTitle.setText(subTitle);
            tvBottomTitle.setText(bottomTitle);
        }

        public void setLocationTag(String locationTag) {
            tvLocationTag.setText(locationTag);
        }

        public void setCurrentPersonText(String bottomSubTitle, boolean isShow) {
            tvBottomSubTitleLeft.setVisibility(isShow ? View.VISIBLE : View.GONE);
            tvBottomSubTitleLeft.setText(bottomSubTitle);
        }

        public void setNeedPersonText(String bottomSubTitle, boolean isShow) {
            tvBottomSubTitleRight.setVisibility(isShow ? View.VISIBLE : View.GONE);
            tvBottomSubTitleRight.setText(bottomSubTitle);
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
