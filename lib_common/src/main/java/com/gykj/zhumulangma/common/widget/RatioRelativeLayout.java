package com.gykj.zhumulangma.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gykj.zhumulangma.common.R;

/**
 * Author: Thomas.
 * Date: 2019/7/30 14:14
 * Email: 1071931588@qq.com
 * Description:
 */
public class RatioRelativeLayout extends RelativeLayout {

    private float ratio;

    public RatioRelativeLayout(Context context) {
        super(context);
    }

    public RatioRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioRelativeLayout);
        ratio = typedArray.getFloat(R.styleable.RatioRelativeLayout_ratio, 0.0f);
    }

    public RatioRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取宽度的模式和尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (ratio != 0) {
            //根据宽高比ratio和模式创建一个测量值
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * ratio), MeasureSpec.EXACTLY);
        }
        //必须调用下面的两个方法之一完成onMeasure方法的重写，否则会报错
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置高宽比
     *
     * @param ratio 宽高比（比如：高：宽 = 4：5，ratio=0.8）
     */
    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
