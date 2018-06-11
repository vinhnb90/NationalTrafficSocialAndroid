package com.vn.ntsc.ui.search.area;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.json.regions.RegionItem;
import com.vn.ntsc.ui.profile.edit.EditProfileActivity;
import com.vn.ntsc.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hnc on 15/08/2017.
 */

public class ChooseEachAreaAdapter extends RecyclerView.Adapter<ChooseEachAreaAdapter.ViewHolder> {
    public static final String editProfile = EditProfileActivity.class.getSimpleName();

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ROUND_TOP = 2;
    public static final int TYPE_ROUND_NORMAL = 3;
    public static final int TYPE_ROUND_BOTTOM = 4;

    public static final String NORTHERN = "Miền Bắc";
    public static final String CENTRAL = "Miền Trung";
    public static final String SOUTH = "Miền Nam";

    public static final int STARTNORTHERN = 1;
    public static final int ENDNORTHERN = 25;

    public static final int STARTCENTRAL = 27;
    public static final int ENDCENTRAL = 45;

    public static final int STARSOUTH = 47;
    public static final int ENDSOUTH = 65;

    private Context mContext;
    private ArrayList<RegionItem> mData;
    private LayoutInflater mLayoutInflater;


    private boolean isClickNorthern = true;
    private boolean isClickCentral = true;
    private boolean isClickSouth = true;

    /**
     * true: single choice
     */
    private boolean isSingleChoice = false;


    private static String chooseAreasType;

    public ChooseEachAreaAdapter(Context context, String areasType) {
        this.mContext = context;
        chooseAreasType = areasType;
        mLayoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mContext, mLayoutInflater.inflate(R.layout.item_search_by_area, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.onBind(mData.get(position), position);
        checkItemAreas(mData.get(position), holder);
        holder.mRadioButtonContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelection(position);
            }
        });

        holder.mLayoutItemAreas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelection(position);
            }
        });

        holder.mTvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mData.get(position).name) {
                    case NORTHERN:
                        if (isClickNorthern) {
                            for (int i = STARTNORTHERN; i <= ENDNORTHERN; i++) {
                                mData.get(i).setChecked(true);
                            }
                            isClickNorthern = false;
                        } else {
                            for (int i = STARTNORTHERN; i <= ENDNORTHERN; i++) {
                                mData.get(i).setChecked(false);
                            }
                            isClickNorthern = true;
                        }
                        break;
                    case CENTRAL:
                        if (isClickCentral) {
                            for (int i = STARTCENTRAL; i <= ENDCENTRAL; i++) {
                                mData.get(i).setChecked(true);
                            }
                            isClickCentral = false;
                        } else {
                            for (int i = STARTCENTRAL; i <= ENDCENTRAL; i++) {
                                mData.get(i).setChecked(false);
                            }
                            isClickCentral = true;
                        }
                        break;
                    case SOUTH:
                        if (isClickSouth) {
                            for (int i = STARSOUTH; i <= ENDSOUTH; i++) {
                                mData.get(i).setChecked(true);
                            }
                            isClickSouth = false;
                        } else {
                            for (int i = STARSOUTH; i <= ENDSOUTH; i++) {
                                mData.get(i).setChecked(false);
                            }
                            isClickSouth = true;
                        }
                        break;
                }
                notifyDataSetChanged();
            }
        });

    }

    /**
     * toggle state for single choice
     *
     * @param position current position
     */
    private void toggleSelection(int position) {
        if (mData.get(position).isChecked()) {
            // don't allow un-check if single choice = true
            if (!isSingleChoice) mData.get(position).setChecked(false);
        } else {
            // only check one
            if (isSingleChoice) clearAll();
            mData.get(position).setChecked(true);
        }

        notifyDataSetChanged();
    }

    /**
     * set text choose all or delete for each domain
     *
     * @param data
     * @param holder
     */
    private void checkItemAreas(RegionItem data, ViewHolder holder) {
        switch (data.name) {
            case NORTHERN:
                for (int i = STARTNORTHERN; i <= ENDNORTHERN; i++) {
                    if (mData.get(i).isChecked()) {
                        holder.mTvChoose.setText(R.string.areas_delete_all);
                        isClickNorthern = false;
                    } else {
                        holder.mTvChoose.setText(R.string.areas_select_all);
                        isClickNorthern = true;
                    }
                }
                break;
            case CENTRAL:
                for (int i = STARTCENTRAL; i <= ENDCENTRAL; i++) {
                    if (mData.get(i).isChecked()) {
                        holder.mTvChoose.setText(R.string.areas_delete_all);
                        isClickCentral = false;
                    } else {
                        holder.mTvChoose.setText(R.string.areas_select_all);
                        isClickCentral = true;
                    }
                }
                break;
            case SOUTH:
                for (int i = STARSOUTH; i <= ENDSOUTH; i++) {
                    if (mData.get(i).isChecked()) {
                        holder.mTvChoose.setText(R.string.areas_delete_all);
                        isClickSouth = false;
                    } else {
                        holder.mTvChoose.setText(R.string.areas_select_all);
                        isClickSouth = true;
                    }
                }
                break;
        }
    }

    /**
     * clear all selected
     */
    private void clearAll() {
        for (RegionItem area : mData) {
            area.setChecked(false);
        }
    }

    /**
     * set single choice mode
     *
     * @param singleChoice true: single choice, false multi choice
     */
    public void setSingleChoice(boolean singleChoice) {
        isSingleChoice = singleChoice;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<RegionItem> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public ArrayList<RegionItem> getData() {
        return mData;
    }

    //========================================Holder================================================

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rbtn_content)
        AppCompatCheckBox mRadioButtonContent;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.layout_bottom)
        RelativeLayout mLayoutBottom;
        @BindView(R.id.tv_choose)
        TextView mTvChoose;
        @BindView(R.id.tv_areas)
        TextView mTvAreas;
        @BindView(R.id.item_areas)
        RelativeLayout mLayoutItemAreas;
        private Context mContext;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            itemView.setClickable(true);
            this.mContext = context;
            ButterKnife.bind(this, itemView);
        }

        private void onBind(final RegionItem data, int position) {
            mTvAreas.setText(data.name);
            mRadioButtonContent.setChecked(data.isChecked());

            switch (data.mAdapterType) {
                case TYPE_ROUND_TOP:
                    itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_search_area_round_top));
                    mLayoutItemAreas.setVisibility(View.VISIBLE);
                    mLayoutBottom.setVisibility(View.GONE);
                    break;

                case TYPE_ROUND_NORMAL:
                    itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_search_area_round_normal));
                    mLayoutItemAreas.setVisibility(View.VISIBLE);
                    mLayoutBottom.setVisibility(View.GONE);
                    break;

                case TYPE_ROUND_BOTTOM:
                    itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_search_area_round_bottom));
                    mLayoutItemAreas.setVisibility(View.VISIBLE);
                    mLayoutBottom.setVisibility(View.GONE);
                    break;

                case TYPE_HEADER:
                    itemView.setBackgroundColor(Color.TRANSPARENT);
                    mLayoutItemAreas.setVisibility(View.GONE);
                    mLayoutBottom.setVisibility(View.VISIBLE);

                    if (chooseAreasType != null) {
                        mTvChoose.setVisibility(View.GONE);
                        LogUtils.d("TYPE_HEADER", "11 " + chooseAreasType + "___" + editProfile);
                    } else {
                        LogUtils.d("TYPE_HEADER", "22 " + chooseAreasType + "___" + editProfile);
                        mTvChoose.setVisibility(View.VISIBLE);
                    }
                    title.setText(data.name);
                    break;
            }
        }
    }

}
