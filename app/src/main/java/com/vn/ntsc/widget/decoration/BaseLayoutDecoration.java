package com.vn.ntsc.widget.decoration;

import android.graphics.Rect;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.view.View.SCROLLBARS_OUTSIDE_OVERLAY;

/**
 * Created by nankai on 9/18/2017.
 */

public abstract class BaseLayoutDecoration  extends RecyclerView.ItemDecoration{

    private final MarginDelegate marginDelegate;
    private final int spanCount;
    private final int spacing;

    BaseLayoutDecoration( int spanCount, @Px int spacing ){
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.marginDelegate = new MarginDelegate( spanCount, spacing );
    }


    void setPadding( RecyclerView rv, @Px int margin ){
        this.setPadding( rv, margin, margin, margin, margin );
    }

    void setPadding( RecyclerView rv,
                     @Px int top,
                     @Px int bottom,
                     @Px int left,
                     @Px int right ){
        rv.setClipToPadding( false );
        rv.setScrollBarStyle( SCROLLBARS_OUTSIDE_OVERLAY );
        rv.setPadding( left, top, right, bottom );
    }

    MarginDelegate getMarginDelegate(){
        return marginDelegate;
    }

    void calculateMargin(Rect outRect, int position, int spanCurrent, int itemCount, int orientation, boolean isReverse ){
        marginDelegate.calculateMargin( outRect, position, spanCurrent, itemCount, orientation, isReverse );
    }

    public int getSpacing(){
        return spacing;
    }

    public int getSpanCount(){
        return spanCount;
    }

    @Override
    public void getItemOffsets( Rect outRect, View view, RecyclerView parent, RecyclerView.State state ){
        super.getItemOffsets( outRect, view, parent, state );
    }

}
