package com.vn.ntsc.ui.imagepreview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.utils.ImagesUtils;

import java.util.ArrayList;

public class PreviewImageAdapter extends PagerAdapter {
    private final String TAG = "PreviewImageAdapter";

    public interface OnEventListener {
        void onClose();
    }

    private OnEventListener mOnEventListener;
    private LayoutInflater mInflater;
    private ArrayList<String> mFilePaths = new ArrayList<String>();

    // constructor
    public PreviewImageAdapter(BaseActivity activity, OnEventListener mOnEventListener, ArrayList<String> filePaths) {
        this.mFilePaths = filePaths;
        this.mOnEventListener = mOnEventListener;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.mFilePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View viewLayout = mInflater.inflate(R.layout.item_layout_preview_image, container, false);

        ImageView imgDisplayNew = viewLayout.findViewById(R.id.imgDisplayNew);
        Button btnClose = viewLayout.findViewById(R.id.btnClose);
        ImagesUtils.loadImage(mFilePaths.get(position), imgDisplayNew);

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEventListener.onClose();
            }
        });

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}