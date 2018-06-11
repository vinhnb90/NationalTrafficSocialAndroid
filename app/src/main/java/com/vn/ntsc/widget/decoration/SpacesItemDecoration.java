package com.vn.ntsc.widget.decoration;

import android.graphics.Rect;
import android.support.annotation.Px;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by nankai on 9/18/2017.
 */

public class SpacesItemDecoration extends BaseLayoutDecoration {

    public SpacesItemDecoration( @Px int spacing ){
        super( 1, spacing );
    }

    public SpacesItemDecoration( int spanCount, @Px int spacing ){
        super( spanCount, spacing );
    }

    @Override
    public void setPadding( RecyclerView rv, @Px int margin ){
        super.setPadding( rv, margin );
    }

    @Override
    public void setPadding( RecyclerView rv, @Px int top, @Px int bottom, @Px int left, @Px int right ){
        super.setPadding( rv, top, bottom, left, right );
    }

    @Override
    public void getItemOffsets( Rect outRect, View view, RecyclerView parent, RecyclerView.State state ){
        super.getItemOffsets( outRect, view, parent, state );
        int position = parent.getChildAdapterPosition( view );
        int spanCurrent = position % getSpanCount();
        int orientation = OrientationHelper.VERTICAL;
        boolean isInverse = false;
        if( parent.getLayoutManager() instanceof StaggeredGridLayoutManager ){
            orientation = ( (StaggeredGridLayoutManager) parent.getLayoutManager() ).getOrientation();
            isInverse = ( (StaggeredGridLayoutManager) parent.getLayoutManager() ).getReverseLayout();
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            spanCurrent = lp.getSpanIndex();
        }else if( parent.getLayoutManager() instanceof GridLayoutManager ){
            orientation = ( (GridLayoutManager) parent.getLayoutManager() ).getOrientation();
            isInverse = ( (GridLayoutManager) parent.getLayoutManager() ).getReverseLayout();
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            spanCurrent = lp.getSpanIndex();
        }else if( parent.getLayoutManager() instanceof LinearLayoutManager ){
            orientation = ( (LinearLayoutManager) parent.getLayoutManager() ).getOrientation();
            isInverse = ( (LinearLayoutManager) parent.getLayoutManager() ).getReverseLayout();
            position = parent.getChildAdapterPosition( view ); // item position
            spanCurrent = 0;
        }

        calculateMargin( outRect, position, spanCurrent, state.getItemCount(), orientation, isInverse );
    }

}