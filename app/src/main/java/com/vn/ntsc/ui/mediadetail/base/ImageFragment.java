package com.vn.ntsc.ui.mediadetail.base;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.LogUtils;

import static com.vn.ntsc.ui.mediadetail.util.Utils.ARG_DATA_MEDIA;
import static com.vn.ntsc.ui.mediadetail.util.Utils.ARG_POSITION_MEDIA;

public class ImageFragment extends Fragment implements IDetailMediaInteractor.ImageFragmentView, IteractionImage {
    /*----------------------------------------var----------------------------------------*/
    private static String TAG = ImageFragment.class.getName();

    private View view;
    private int mPos;
    private MediaEntity mMediaEntity;
    private boolean mIsFirstInitialView;
    private boolean mAlreadyVisibleToUser;
    private ImageViewHolder mImageViewHolder;

    private IDetailMediaInteractor.Presenter mIPresenter;

    /*----------------------------------------instance----------------------------------------*/
    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(int position, MediaEntity mediaEntity) {
        ImageFragment fragment = new ImageFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_MEDIA, position);
        args.putParcelable(ARG_DATA_MEDIA, mediaEntity);
        fragment.setArguments(args);
        return fragment;
    }

    /*----------------------------------------lifecycle----------------------------------------*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPos = getArguments().getInt(ARG_POSITION_MEDIA);
            mMediaEntity = getArguments().getParcelable(ARG_DATA_MEDIA);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            //when visible
            mAlreadyVisibleToUser = true;

            //if is initialed data then loadImage
            if (mIsFirstInitialView) {
                mIPresenter.loadImage(mPos);

            }
        } else {
            //when invisible
            if (mAlreadyVisibleToUser) {
                mAlreadyVisibleToUser = false;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_media_detail_image, container, false);
        new DetailMediaPresenter.Builder((IDetailMediaInteractor.ActivityView) getActivity(), this).build();

        mImageViewHolder = new ImageViewHolder(getActivity(), view, this);

        mImageViewHolder.showLoadingView(true);

        //load image
        mIPresenter.loadImage(mPos);

        //flag is initial data done
        mIsFirstInitialView = true;

        //if mAlreadyVisibleToUser then save pos is playing
        //lazy loading
        mIPresenter.savePositionMediaPlayingNow(mPos);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlreadyVisibleToUser = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageViewHolder.unbind();
    }

    /*----------------------------------------override----------------------------------------*/
    @Override
    public void setPresenter(@NonNull IDetailMediaInteractor.Presenter presenter) {
        mIPresenter = presenter;
    }

    @Override
    public void loadImage(final String bitmapImageUrl) {
        if (bitmapImageUrl == null)
            return;

        LogUtils.d(TAG, "loadImage: " + bitmapImageUrl);
        mImageViewHolder.loadImage(bitmapImageUrl);
    }

    @Override
    public void loadImageAgain() {
        mIPresenter.loadImage(mPos);
    }


    /*----------------------------------------func----------------------------------------*/
    /*----------------------------------------inner----------------------------------------*/

}

interface IteractionImage {
    /**
     * load again if fail
     */
    void loadImageAgain();
}
