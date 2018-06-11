package com.nankai.designlayout.bottomnavigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nankai.designlayout.R;

/**
 * Created by nankai on 12/5/2017.
 */

public class FragmentEmpty extends Fragment {

    public static FragmentEmpty newInstance() {
        FragmentEmpty fragment = new FragmentEmpty();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_empty, container, false);
        return rootView;
    }
}
