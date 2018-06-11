package com.vn.ntsc.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.MenuLeftBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 8/25/2017.
 */

public class MenuLeftAdapter extends RecyclerView.Adapter<MenuLeftAdapter.ViewHolder> {

    private List<MenuLeftBean> mData;
    private Context mContext;
    private MenuLeftListener listener;

    public MenuLeftAdapter(Context context, MenuLeftListener listener) {
        mData = new ArrayList<>();
        this.mContext = context;
        this.listener = listener;
    }

    public void setData(List<MenuLeftBean> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public List<MenuLeftBean> getData() {
        return mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_menu_left, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mData.size() > 0) {
            holder.onBind(mContext, mData.get(position), position, listener);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu_left_icon)
        ImageView icon;
        @BindView(R.id.menu_left_title)
        TextView title;
        @BindView(R.id.menu_left_notification)
        TextView nitify;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(Context context, final MenuLeftBean bean, final int position, final MenuLeftListener listener) {
            icon.setImageResource(bean.icon);
            title.setText(bean.title);
            if (bean.id == TypeView.MenuLeft.MENU_LEFT_LOGOUT
                    || bean.id == TypeView.MenuLeft.MENU_LEFT_HOME
                    || bean.id == TypeView.MenuLeft.MENU_LEFT_HELP
                    || bean.id == TypeView.MenuLeft.MENU_LEFT_LOGIN) {
                nitify.setVisibility(View.GONE);
            } else {
                nitify.setVisibility(View.VISIBLE);
                nitify.setText(bean.notify + "");
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.openItemMenuLeft(bean, bean.id);
                }
            });
        }
    }
}
