package com.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class PMSwipeRefreshLayout extends SwipeRefreshLayout {

    public PMSwipeRefreshLayout(Context context) {
        super(context);
    }

    public PMSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!isEnabled()) {
            return false;
        }
        return super.onTouchEvent(arg0);
    }


}
