package com.youth.banner.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;


public class BannerViewPager extends ViewPager {
    private boolean scrollable = true;

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * https://www.jianshu.com/p/6e36fb6b80f7
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        /**
         * 设ViewPager中有3张照片
         * 直到完全隐藏此ViewPager，并执行了onDetachedFromWindow
         * 再回来时，将会出现bug，第一次滑动时没有动画效果，并且，经常出现view没有加载的情况
         */
        try {
            Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
            mFirstLayout.setAccessible(true);
            mFirstLayout.set(this, false);

            setCurrentItem(getCurrentItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (((Activity) getContext()).isFinishing()) {
            super.onDetachedFromWindow();
        }
    }
    float startX,startY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = Math.abs(ev.getX() - startX);
                float y = Math.abs(ev.getY() - startY);
                getParent().requestDisallowInterceptTouchEvent(x > y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(this.scrollable) {
            if (getCurrentItem() == 0 && getChildCount() == 0) {
                return false;
            }
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(this.scrollable) {
            if (getCurrentItem() == 0 && getChildCount() == 0) {
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
}
