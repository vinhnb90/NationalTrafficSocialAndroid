package com.vn.ntsc.ui.chat.media;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.ui.chat.emoji.EmojiFragment;
import com.vn.ntsc.ui.chat.listener.OnEmojiClickListener;
import com.vn.ntsc.ui.chat.listener.OnStickerClickListener;
import com.vn.ntsc.ui.chat.sticker.StickerFragment;
import com.vn.ntsc.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EmojiStickerFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    public static final String TAG = EmojiStickerFragment.class.getSimpleName();

    @BindView(R.id.fragment_emoji_sticker_pager)
    ViewPager mViewPager;

    @BindView(R.id.fragment_emoji_sticker_btn_emoji)
    TextView btnEmojiView;

    @BindView(R.id.fragment_emoji_sticker_btn_sticker)
    TextView btnStickerView;


    private OnStickerClickListener mOnStickerClickListener;
    private OnEmojiClickListener mOnEmojiClickListener;

    public EmojiStickerFragment() {
        // Required empty public constructor
    }

    public static EmojiStickerFragment newInstance(OnStickerClickListener listener, OnEmojiClickListener emojiClickListener) {
        EmojiStickerFragment fragment = new EmojiStickerFragment();
        fragment.mOnStickerClickListener = listener;
        fragment.mOnEmojiClickListener = emojiClickListener;
        return fragment;
    }


    private void initView() {
        mViewPager.addOnPageChangeListener(this);
        setupViewPager(mViewPager);
        bgColortabEmoji();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_emoji_sticker;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(getActivity(), getActivity());
        initView();
    }

    @Override
    protected void setUserVisibleHint() {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(EmojiFragment.newInstance(mOnEmojiClickListener));
        adapter.addFragment(StickerFragment.newInstance(mOnStickerClickListener));
        viewPager.setAdapter(adapter);
    }


    private void bgColortabEmoji() {
        btnEmojiView.setBackgroundColor(getResources().getColor(R.color.default_app));
        btnEmojiView.setTextColor(getResources().getColor(R.color.white));

        btnStickerView.setBackgroundColor(getResources().getColor(R.color.transparent));
        btnStickerView.setTextColor(getResources().getColor(R.color.default_app));
    }


    private void bgColortabSticker() {
        btnStickerView.setBackgroundColor(getResources().getColor(R.color.default_app));
        btnStickerView.setTextColor(getResources().getColor(R.color.white));

        btnEmojiView.setBackgroundColor(getResources().getColor(R.color.transparent));
        btnEmojiView.setTextColor(getResources().getColor(R.color.default_app));
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @OnClick({R.id.fragment_emoji_sticker_btn_emoji, R.id.fragment_emoji_sticker_btn_sticker})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_emoji_sticker_btn_emoji:
                mViewPager.setCurrentItem(0);
                bgColortabEmoji();
                break;
            case R.id.fragment_emoji_sticker_btn_sticker:
                mViewPager.setCurrentItem(1);
                bgColortabSticker();
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        LogUtils.e(TAG, "index 0 " + i);
    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            bgColortabEmoji();
        } else {
            bgColortabSticker();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


}
