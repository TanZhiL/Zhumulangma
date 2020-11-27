package com.gykj.zhumulangma.common.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.gykj.zhumulangma.common.R;

/**
 * Author: Thomas.<br/>
 * Date: 2020/11/26 22:21<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class PlayingIconView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private float mSpan = SizeUtils.dp2px(3);
    private float mLineWidth = SizeUtils.dp2px(3);
    private ValueAnimator mAnimation;
    private float mProgess;
    public PlayingIconView(Context context) {
        this(context, null);
    }

    public PlayingIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayingIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(context.getResources().getColor(R.color.colorPrimary));
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mAnimation = ValueAnimator.ofFloat(0,1,0);
        mAnimation.setRepeatCount(ValueAnimator.INFINITE);
        mAnimation.setDuration(850).addUpdateListener(animation -> {
            mProgess = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST
                || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            setMeasuredDimension(SizeUtils.dp2px(24), SizeUtils.dp2px(24));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mWidth >> 1, mHeight);
        canvas.scale(1, -1);
        float h = mHeight - (mLineWidth / 2);
        float m = h * 0.65f;
        float n = h * 0.25f;
        float v = n + (m - n) * mProgess;
        //第二条
        canvas.drawLine(-mSpan / 2 - mLineWidth / 2, mLineWidth/2, -mSpan / 2 - mLineWidth / 2, v, mPaint);
        canvas.save();
        canvas.translate(-mSpan - mLineWidth, 0);

        m = h * 0.7f;
        n = h * 0.15f;
        v = m - (m - n) * mProgess;
        //第一条
        canvas.drawLine(-mSpan / 2 - mLineWidth / 2, mLineWidth/2, -mSpan / 2 - mLineWidth / 2,v, mPaint);
        canvas.restore();

        canvas.scale(-1, 1);

        m = h * 0.95f;
        n = h * 0.40f;
        v = m - (m - n) * mProgess;
        //第三条
        canvas.drawLine(-mSpan / 2 - mLineWidth / 2, mLineWidth/2, -mSpan / 2 - mLineWidth / 2,v, mPaint);
        canvas.save();
        canvas.translate(-mSpan - mLineWidth, 0);
        m = h * 0.85f;
        n = h * 0.35f;
        v = n + (m - n) * mProgess;
        //第四条
        canvas.drawLine(-mSpan / 2 - mLineWidth / 2, mLineWidth/2, -mSpan / 2 - mLineWidth / 2, v, mPaint);
        canvas.restore();

        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimation();
    }

    public void playAnimation(){
        mAnimation.start();
    }
    public void pauseAnimation(){
        mAnimation.pause();
    }
    public void cancelAnimation(){
        mAnimation.cancel();
        mProgess = 0;
        invalidate();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(visibility ==GONE){
            cancelAnimation();
        }
    }
}