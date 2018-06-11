package com.vn.ntsc.ui.profile.edit.job;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vn.ntsc.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dev22 on 8/23/17.
 */
public class JobHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item)
    AppCompatCheckBox item;

    public JobHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
