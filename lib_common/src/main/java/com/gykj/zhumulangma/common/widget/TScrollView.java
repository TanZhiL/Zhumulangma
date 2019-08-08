package com.gykj.zhumulangma.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by 10719
 * on 2019/6/14
 */
public class TScrollView extends ScrollView {

    private OnScrollListener listener;

    public void setOnScrollListener(OnScrollListener listener) {
        this.listener = listener;
    }

    public TScrollView(Context context) {
        super(context);
    }

    public TScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置接口
    public interface OnScrollListener{
        void onScroll(int scrollY);
    }

    //重写原生onScrollChanged方法，将参数传递给接口，由接口传递出去
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener != null){

            //这里我只传了垂直滑动的距离
            listener.onScroll(t);
        }
    }
}
