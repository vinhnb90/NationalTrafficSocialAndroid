package com.vn.ntsc.ui.chat.media;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.ui.chat.listener.OnMediaPanelListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MediaFragment extends Fragment {

    Unbinder unbinder;
    private OnMediaPanelListener onMediaPanelListener;

    @BindView(R.id.fragment_media_btn_emojis_photo)
    LinearLayout btnEmojisPhoto;
    @BindView(R.id.fragment_media_btn_emojis_camera)
    LinearLayout btnEmojisCamera;
    @BindView(R.id.fragment_media_btn_emoji_video)
    LinearLayout btnEmojiVideo;
    @BindView(R.id.fragment_media_btn_emoji_record_video)
    LinearLayout btnEmojiRecordVideo;
    @BindView(R.id.fragment_media_btn_record)
    LinearLayout btnRecord;


    public static MediaFragment newInstance(OnMediaPanelListener listener) {
        MediaFragment fragment = new MediaFragment();
        fragment.onMediaPanelListener = listener;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.fragment_media_btn_emojis_photo, R.id.fragment_media_btn_emojis_camera, R.id.fragment_media_btn_emoji_video, R.id.fragment_media_btn_emoji_record_video, R.id.fragment_media_btn_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_media_btn_emojis_photo:
                if (onMediaPanelListener != null) {
                    onMediaPanelListener.onPhotoClickListener();
                }
                break;
            case R.id.fragment_media_btn_emojis_camera:
                if (onMediaPanelListener != null) {
                    onMediaPanelListener.onCameraClickListener();
                }
                break;
            case R.id.fragment_media_btn_emoji_video:
                if (onMediaPanelListener != null) {
                    onMediaPanelListener.onVideoClickListener();
                }
                break;
            case R.id.fragment_media_btn_emoji_record_video:
                if (onMediaPanelListener != null) {
                    onMediaPanelListener.onRecordVideoListener();
                }
                break;
            case R.id.fragment_media_btn_record:
                if (onMediaPanelListener != null) {
                    onMediaPanelListener.onRecordingListenner();
                }
                break;

        }
    }


}
