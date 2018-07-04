package com.vn.ntsc.ui.mediadetail.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.widget.views.images.ImageViewTouch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MediaAdapter extends FragmentStatePagerAdapter {

    /*-----------------------------------var-----------------------------------*/
    private List<MediaEntity> mData;

    //use HashMap to save fragment more benefit than SpareArray Map with above 1000
    private HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();

    /*-----------------------------------instance-----------------------------------*/
    public MediaAdapter(FragmentManager fm, List<MediaEntity> data) {
        super(fm);
        mData = new ArrayList<>();
        this.mData = data;
    }

    /*-----------------------------------lifecycle-----------------------------------*/
    @Override
    public Fragment getItem(int position) {
        //return fragment
        Fragment fragment = null;
        switch (mData.get(position).mType) {

            case TypeView.MediaDetailType.IMAGE_TYPE:
                fragment = ImageFragment.newInstance(position, mData.get(position));
                break;

            case TypeView.MediaDetailType.VIDEO_TYPE:
                fragment = VideoFragment.newInstance(position);
                break;

            case TypeView.MediaDetailType.STREAM_TYPE:
                fragment = VideoFragment.newInstance(position);
                break;

            case TypeView.MediaDetailType.AUDIO_TYPE:
                fragment = AudioFragment.newInstance(position);
                break;
            default:
                fragment = null;
        }


        //save fragment to collection, use in outside
        fragmentHashMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        fragmentHashMap.remove(position);
    }


    /*-----------------------------------func-----------------------------------*/
    public HashMap<Integer, Fragment> getFragmentHashMap() {
        return fragmentHashMap;
    }


    public void removeItem(int position) {
        // Remove the corresponding item in the data set
        fragmentHashMap.remove(mData.get(position));
        mData.remove(position);
        // Notify the adapter that the data set is changed
        notifyDataSetChanged();
    }

    /*-----------------------------------interface-----------------------------------*/
}
