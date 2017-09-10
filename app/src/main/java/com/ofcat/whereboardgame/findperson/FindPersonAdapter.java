package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.util.DateUtility;

import java.util.Calendar;

/**
 * Created by orangefaller on 2017/4/30.
 */

public class FindPersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_TEXT_INFO = 1;
    private static final int VIEWTYPE_EDIT_INFO = 2;
    private static final int VIEWTYPE_EDIT_INFO_TWO = 3;
    private static final int VIEWTYPE_SPINNER_INFO = 4;

    private Context ctx;

    private AdapterListener adapterListener;

    private String[] titleArray;
    private String[] hintArray;
    private String[] infoTextArray;

    private String bgsPlace;
    private String nowDate;

    private Calendar selectCalendar;

    public FindPersonAdapter(Context context) {
        this.ctx = context;
        this.titleArray = ctx.getResources().getStringArray(R.array.findperson_item_title_list);
        this.hintArray = ctx.getResources().getStringArray(R.array.findperson_item_hint_list);
        this.selectCalendar = Calendar.getInstance();
        init();
    }

    private void init() {
        Calendar now = Calendar.getInstance();
        setSelectDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    }

    private int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    private int getDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void setSelectDate(int year, int month, int dayofmonth) {
        selectCalendar.set(year, month, dayofmonth);
        this.nowDate = DateUtility.getCustomFormatDate(selectCalendar);
    }

    public void setUserEditRecord(String initiator, String time, String contact, String content) {

        setInfoTextArray(initiator, 4);
        setInfoTextArray(time, 5);
        setInfoTextArray(contact, 6);
        setInfoTextArray(content, 7);

    }

    private void setInfoTextArray(String info, int position) {
        if (infoTextArray == null) {
            infoTextArray = new String[getItemCount()];
        }
//        Log.i("kevintest", "set info = " + info + " pos = " + position);
        if (position >= 0 && position < getItemCount()) {
            infoTextArray[position] = info;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater(parent);

        switch (viewType) {
            case VIEWTYPE_TEXT_INFO:
                View textView = inflater.inflate(R.layout.item_text_info, parent, false);
                return new VHTextInfo(textView);
            case VIEWTYPE_EDIT_INFO:
                View editView = inflater.inflate(R.layout.item_edit_info, parent, false);
                return new VHEditInfo(editView);
            case VIEWTYPE_EDIT_INFO_TWO:
                View dateTimeView = inflater.inflate(R.layout.item_edit_info_two, parent, false);
                return new VHEditInfoTwo(dateTimeView);
            case VIEWTYPE_SPINNER_INFO:
                View spinnerView = inflater.inflate(R.layout.item_spinner_person_info, parent, false);
                return new VHSpinnerPersonInfo(spinnerView);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        Log.i("kevintest", "onBindViewHolder pos = " + position);
        if (holder instanceof VHTextInfo) {

            switch (position) {
                case 0:
                    String text = String.format(ctx.getResources().getString(R.string.findperson_place), bgsPlace);
                    ((VHTextInfo) holder).setTextInfo(text);
                    ((VHTextInfo) holder).showIcon(false);

                    setInfoTextArray(bgsPlace, position);
                    break;
                case 1:
                    String date = String.format(ctx.getResources().getString(R.string.findperson_date), nowDate);
                    ((VHTextInfo) holder).setTextInfo(date);
                    ((VHTextInfo) holder).setIconRes(R.mipmap.ic_date_range_black_24dp);
                    ((VHTextInfo) holder).showIcon(true);

                    setInfoTextArray(nowDate, position);
                    break;
                default:
                    break;
            }

        } else if (holder instanceof VHEditInfo) {
            ((VHEditInfo) holder).setTvInfoTile(titleArray[position - 4]);

            ((VHEditInfo) holder).setEditHint(hintArray[position - 4]);

            ((VHEditInfo) holder).setEditText(getInfoTextArray()[position]);

            if (position == 4 || position == 5) {
                ((VHEditInfo) holder).useEditTextSingleLine();
            } else if (position == 6 || position == 7) {
                ((VHEditInfo) holder).setEditTextCustomBackground();
            }

        } else if (holder instanceof VHSpinnerPersonInfo) {

            if (position == 2) {
                ((VHSpinnerPersonInfo) holder).setTextInfo(ctx.getResources().getString(R.string.custom_findperson_current_person));
            }else if (position == 3){
                ((VHSpinnerPersonInfo) holder).setTextInfo(ctx.getResources().getString(R.string.custom_findperson_need_person));
            }
        }


    }


    @Override
    public int getItemViewType(int position) {
        if (position <= 1) {
            return VIEWTYPE_TEXT_INFO;
        } else if (position <= 3) {
            return VIEWTYPE_SPINNER_INFO;
        } else {
            return VIEWTYPE_EDIT_INFO;
        }

    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public void setBoardGameStorePlace(String place) {
        this.bgsPlace = place;

    }

    public void setFindPersonAdapterListener(AdapterListener listener) {
        this.adapterListener = listener;
    }

    public String[] getInfoTextArray() {
        if (infoTextArray == null) {
            infoTextArray = new String[getItemCount()];
        }
        return infoTextArray;
    }

    private LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }


    private class VHTextInfo extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTextInfo;
        private ImageView ivInfoIcon;

        public VHTextInfo(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tvTextInfo = (TextView) itemView.findViewById(R.id.tv_info_text);
            ivInfoIcon = (ImageView) itemView.findViewById(R.id.iv_info_icon);
        }

        public void setTextInfo(String textInfo) {
            tvTextInfo.setText(textInfo);
        }

        public void showIcon(boolean isShow) {
            ivInfoIcon.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }

        public void setIconRes(int res) {
            ivInfoIcon.setImageResource(res);
        }

        public String getText() {
            return tvTextInfo.getText().toString();
        }

        @Override
        public void onClick(View view) {
            if (adapterListener != null) {
                adapterListener.onTextClick(view, getAdapterPosition());
                if (getAdapterPosition() == 1) {
                    adapterListener.onDateClick(view, selectCalendar);
                }
            }
        }
    }

    private class VHEditInfo extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

        private TextView tvInfoTile;
        private EditText etInfo;

        public VHEditInfo(View itemView) {
            super(itemView);
            tvInfoTile = (TextView) itemView.findViewById(R.id.tv_info_title);
            etInfo = (EditText) itemView.findViewById(R.id.et_info);
            etInfo.setOnClickListener(this);
            etInfo.setOnFocusChangeListener(this);
            etInfo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    setInfoTextArray(editable.toString(), getAdapterPosition());
                }
            });

        }

        public void setTvInfoTile(String title) {
            tvInfoTile.setText(title);
        }

        public void setEditHint(String hint) {
            etInfo.setHint(hint);
        }

        public void setEditText(String text) {
            etInfo.setText(text);
        }

        public void useEditTextSingleLine() {
            etInfo.setInputType(InputType.TYPE_CLASS_TEXT);
            etInfo.setMaxLines(1);
        }

        public void setEditTextCustomBackground() {
            etInfo.setBackgroundResource(R.drawable.selector_edittext_black_frame);
        }

        public String getText() {
            return etInfo.getText().toString();
        }

        public void render() {

        }

        @Override
        public void onClick(View view) {
            if (adapterListener != null) {
                adapterListener.onEditClick(view, etInfo.hasFocus(), etInfo.getText().toString(), getAdapterPosition());
            }

        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if (adapterListener != null) {
//                setInfoTextArray(getText(), getAdapterPosition());
                adapterListener.onEditClick(view, b, etInfo.getText().toString(), getAdapterPosition());
            }
        }
    }

    private class VHEditInfoTwo extends RecyclerView.ViewHolder {

        private TextView tvInfoTileLeft;
        private TextView tvInfoTileRight;
        private EditText etInfoLeft;
        private EditText etInfoRight;

        public VHEditInfoTwo(View itemView) {
            super(itemView);
        }
    }

    private class VHSpinnerPersonInfo extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private Spinner spinnerInfo;
        private TextView tvTextInfo;

        public VHSpinnerPersonInfo(View itemView) {
            super(itemView);
            tvTextInfo = (TextView) itemView.findViewById(R.id.tv_info_title);
            spinnerInfo = (Spinner) itemView.findViewById(R.id.spinner_item);
            spinnerInfo.setOnItemSelectedListener(this);
        }

        public void setTextInfo(String text) {
            tvTextInfo.setText(text);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            if (adapterListener != null) {
                adapterListener.onSpinnerItemSelect(view, position, getAdapterPosition());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public interface AdapterListener {
        void onEditClick(View view, boolean hasFocus, String text, int position);

        void onTextClick(View view, int position);

        void onDateClick(View view, Calendar calendar);

        void onSpinnerItemSelect(View view, int selectPosition, int adapterPosition);

    }
}
