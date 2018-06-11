package com.vn.ntsc.ui.timeline.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.ui.timeline.adapter.TimelineAdapter;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by nankai on 8/29/2017.
 */

public abstract class TimelineViewHolder<E extends BaseBean> {

    protected List<E> mData;
    protected View view;
    protected TimelineFragment fragment;

    public boolean isInject() {
        return fragment == null;
    }

    public void setFragment(TimelineFragment fragment) {
        this.fragment = fragment;
        initInject(fragment.getContext());
    }

    protected void initInject(Context context) {
        view = LayoutInflater.from(context).inflate(getLayoutId(), null);
        ButterKnife.bind(this, view);
    }

    protected abstract int getLayoutId();

    protected Context getContext(){
        return view.getContext();
    }

    protected TimeLinePresenter getPresenter() {
        return fragment.getPresenter();
    }

    public abstract void initInjectView(TimelineAdapter adapter);

    public abstract void setData(List<E> datas);
}
