package com.vn.ntsc.ui.chat.emoji;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.chat.model.EmojiModel;
import com.vn.ntsc.ui.chat.listener.OnEmojiClickListener;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.chats.ChatUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ducng on 12/6/2017.
 */

public class TabEmojiFragment extends Fragment {
    public static final String TAG = TabEmojiFragment.class.getSimpleName();
    public static final int SPAN_COUNT = 5;

    private Set<EmojiModel> emojiList;
    private OnEmojiClickListener mOnEmojiClickListener;


    public static TabEmojiFragment newInstance(@NonNull ArrayList<EmojiModel> emojiList, @NonNull OnEmojiClickListener listener) {
        TabEmojiFragment fragment = new TabEmojiFragment();
        fragment.mOnEmojiClickListener = listener;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ChatUtils.EMOJI, emojiList);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_emoji_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            ArrayList<EmojiModel> emojis = getArguments().getParcelableArrayList(ChatUtils.EMOJI);
            if (emojis != null)
                emojiList = new HashSet<>(emojis);
        }

        LogUtils.e(TAG, "emojiList " + emojiList.size());

        initView(view);
    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.lst_emoji);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), SPAN_COUNT));
        recyclerView.setHasFixedSize(true);
        EmojiGridAdapter mAdapter = new EmojiGridAdapter(emojiList);
        recyclerView.setAdapter(mAdapter);
    }

    private class EmojiGridAdapter extends RecyclerView.Adapter<EmojiGridAdapter.ViewHolder> {
        private ArrayList<EmojiModel> mEmoji;

        EmojiGridAdapter(Set<EmojiModel> emojis) {
            mEmoji = new ArrayList<>(emojis);
        }

        @Override
        public EmojiGridAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new EmojiGridAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_emoji_grid, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(final EmojiGridAdapter.ViewHolder viewHolder, final int i) {

            final String link = mEmoji.get(i).getUrl();
            ImagesUtils.loadImageWithoutBackgroundFix(viewHolder.itemView.getContext(), link, viewHolder.imgEmoji);

            viewHolder.imgEmoji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnEmojiClickListener != null) {
                        mOnEmojiClickListener.onEmojiClick(mEmoji.get(i).getCode());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return emojiList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imgEmoji;

            public ViewHolder(View itemView) {
                super(itemView);
                imgEmoji = itemView.findViewById(R.id.imv_emoji);
            }
        }
    }

}
