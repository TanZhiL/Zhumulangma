package com.gykj.zhumulangma.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NestRecyclerView extends RecyclerView {
    private boolean requestDisallowIntercept = true;
    public NestRecyclerView(@NonNull Context context) {
        super(context);
    }

    public NestRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float startX,startY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(requestDisallowIntercept);
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                LayoutManager layoutManager = getLayoutManager();
                if(layoutManager instanceof LinearLayoutManager){
                    int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
                    float x = Math.abs(ev.getX() - startX);
                    float y = Math.abs(ev.getY() - startY);
                    if (orientation == VERTICAL) {
                        getParent().requestDisallowInterceptTouchEvent(requestDisallowIntercept && y > x);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(requestDisallowIntercept && x > y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setRequestDisallowIntercept(boolean requestDisallowIntercept) {
        this.requestDisallowIntercept = requestDisallowIntercept;
    }
}