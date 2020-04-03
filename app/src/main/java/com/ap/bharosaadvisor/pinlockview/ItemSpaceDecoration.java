package com.ap.bharosaadvisor.pinlockview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemSpaceDecoration extends RecyclerView.ItemDecoration
{

    private final int horizontalSpaceWidth;
    private final int verticalSpaceHeight;
    private final int spanCount;
    private final boolean includeEdge;

    ItemSpaceDecoration(int horizontalSpaceWidth, int verticalSpaceHeight, int spanCount, boolean includeEdge)
    {
        this.horizontalSpaceWidth = horizontalSpaceWidth;
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.spanCount = spanCount;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {

        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        if (includeEdge)
        {
            outRect.right = horizontalSpaceWidth - column * horizontalSpaceWidth / spanCount;
            outRect.left = (column + 1) * horizontalSpaceWidth / spanCount;

            if (position < spanCount)
                outRect.top = verticalSpaceHeight;
            outRect.bottom = verticalSpaceHeight;
        } else
        {
            outRect.right = column * horizontalSpaceWidth / spanCount;
            outRect.left = horizontalSpaceWidth - (column + 1) * horizontalSpaceWidth / spanCount;
            if (position >= spanCount)
                outRect.top = verticalSpaceHeight;
        }
    }
}
