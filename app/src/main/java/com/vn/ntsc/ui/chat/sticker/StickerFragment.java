package com.vn.ntsc.ui.chat.sticker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.ui.chat.listener.OnStickerClickListener;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.chats.ChatUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ducng on 10/27/2017.
 */

public class StickerFragment extends BaseFragment {
    @BindView(R.id.fragment_sticker_tab_sticker)
    TabLayout mTabLayout;
    @BindView(R.id.fragment_sticker_viewpager_sticker)
    ViewPager mViewPager;

    public static final String TAG = StickerFragment.class.getSimpleName();

    private List<StickerCategory> mStickerCats = new ArrayList<>(); // need create

    private OnStickerClickListener mOnStickerClickListener;

    public static StickerFragment newInstance(OnStickerClickListener listener) {
        StickerFragment fragment = new StickerFragment();
        fragment.mOnStickerClickListener = listener;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sticker;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (ChatUtils.getStickerData().size() == 0) {
            mStickerCats = ChatUtils.getEmojiInternal(getActivity(), ChatUtils.STICKER);
        } else {
            mStickerCats = ChatUtils.getStickerData();
        }
    }

    @Override
    protected void setUserVisibleHint() {
        initView();
    }


    private void initView() {
        StickerPagerAdapter mStickerPagerAdapter = new StickerPagerAdapter(getActivity(), mStickerCats);
        mViewPager.setAdapter(mStickerPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        setIconTab(getActivity(), mTabLayout);
    }

    private void setIconTab(Context context, TabLayout tabLayout) {
        for (int i = 0; i < mStickerCats.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_sticker_header, null);
            Log.e("setIconTab", "  " + view + "__" + tabLayout);
            ImageView imv = view.findViewById(R.id.item_tab_sticker);
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(view);
//            Glide.with(context).load(mStickerCats.get(i).source.pathSticker).into(imv);
            ImagesUtils.loadImage(mStickerCats.get(i).source.pathSticker, imv);
        }
    }

    private class StickerPagerAdapter extends PagerAdapter {
        private static final int SPAN_COUNT = 4;
        private List<StickerCategory> mData;
        private LayoutInflater mInflater;
        private StickerGridAdapter mAdapter;

        public StickerPagerAdapter(Context context, List<StickerCategory> stickerCategories) {
            mData = stickerCategories;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.layout_item_sticker_pager, container, false);
            RecyclerView lstSticker = view.findViewById(R.id.lst_sticker);
            lstSticker.setLayoutManager(new GridLayoutManager(container.getContext(), SPAN_COUNT));
            mAdapter = new StickerGridAdapter(mData.get(position));
            lstSticker.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    // ============================================================================================
    // Item in grid recyclerView
    private class StickerGridAdapter extends RecyclerView.Adapter<StickerGridAdapter.ViewHolder> {
        private StickerCategory stickerCategory;

        public StickerGridAdapter(StickerCategory stickerCategory) {
            this.stickerCategory = stickerCategory;
        }

        @Override
        public StickerGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StickerGridAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_sticker_grid, parent, false));
        }

        @Override
        public void onBindViewHolder(StickerGridAdapter.ViewHolder holder, final int position) {
            final String link = stickerCategory.mLstSticker.get(position).pathSticker;
            ImagesUtils.loadImageWithoutBackgroundFix(holder.itemView.getContext(), link, holder.imvSticker);
            
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnStickerClickListener != null) {
                        mOnStickerClickListener.onStickerClick(link);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return stickerCategory == null ? 0 : stickerCategory.mLstSticker.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView imvSticker;

            public ViewHolder(View itemView) {
                super(itemView);
                imvSticker = itemView.findViewById(R.id.imv_sticker);
            }
        }
    }


}
