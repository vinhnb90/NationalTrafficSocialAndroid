package com.vn.ntsc.ui.chat.emoji;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.model.chat.model.EmojiModel;
import com.vn.ntsc.repository.model.chat.sql.DatabaseHelper;
import com.vn.ntsc.ui.chat.listener.OnEmojiClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ducng on 12/6/2017.
 */

public class EmojiFragment extends BaseFragment {

    @BindView(R.id.fragment_emoji_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fragment_emoji_tab)
    TabLayout mTabLayout;

    // key = category, value= list emoji path
    private HashMap<String, ArrayList<EmojiModel>> emojiMap = new HashMap<>();
    private OnEmojiClickListener onEmojiClickListener;
    // list store category icons
    private List<String> categoryIcons = new ArrayList<>();
    private ViewPagerAdapter adapter;

    public static EmojiFragment newInstance(OnEmojiClickListener listener) {
        Bundle args = new Bundle();
        EmojiFragment fragment = new EmojiFragment();
        fragment.onEmojiClickListener = listener;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_emoji;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get all emoji from db
        ArrayList<EmojiModel> emojiDb = DatabaseHelper.getInstance(getContext()).getAllEmoji();
        for (EmojiModel model : emojiDb) {
            String key = model.getCatId();
            if (!emojiMap.containsKey(key)) {
                categoryIcons.add(model.getUrl());
                ArrayList<EmojiModel> list = new ArrayList<>();
                list.add(model);

                emojiMap.put(key, list);
            } else {
                emojiMap.get(key).add(model);
            }
        }

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabsIcon();
    }

    /**
     * set icon for tab emoji
     *
     * @see #categoryIcons
     */
    private void setupTabsIcon() {
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);

            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_emoji_tab, null);
            ImageView img = v.findViewById(R.id.item_tab_emoji);

            String path = adapter.getCategoryIcon(i);
            Glide.with(this)
                    /// will load first frame on tab
                    .asBitmap()
                    .load(path)
                    .into(img);

            if (tab != null)
                tab.setCustomView(v);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager(), emojiMap, onEmojiClickListener);
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<TabEmojiFragment> mFragmentList = new ArrayList<>();
        private final List<String> listCategoryIcon = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager, HashMap<String, ArrayList<EmojiModel>> emojiMap, OnEmojiClickListener onEmojiClickListener) {
            super(manager);

            // add fragments
            for (String key : emojiMap.keySet()) {
                ArrayList<EmojiModel> emojiModels = emojiMap.get(key);

                // store first emoji path as category icon
                listCategoryIcon.add(emojiModels.get(0).getUrl());
                mFragmentList.add(TabEmojiFragment.newInstance(emojiModels, onEmojiClickListener));
            }
        }

        /**
         * @param position to get category icon
         * @return category icon
         */
        public String getCategoryIcon(int position) {
            return listCategoryIcon.get(position);
        }

        @Override
        public TabEmojiFragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
