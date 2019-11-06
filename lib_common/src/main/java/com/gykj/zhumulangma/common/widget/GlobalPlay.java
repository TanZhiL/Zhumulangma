package com.gykj.zhumulangma.common.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gykj.zhumulangma.common.R;
import com.gykj.zhumulangma.common.extra.GlideApp;
import com.next.easynavigation.utils.NavigationUtil;

/**
 * Author: Thomas.<br/>
 * Date: 2019/11/4 17:53<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:
 */
public class GlobalPlay extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "GlobalPlay";
    private Paint mPaint;
    private Paint mBpPaint;
    private float mWidth, mHeight;
    private int mUnreachColor = 0x88a6a6a6;
    private int mReachedColor = 0xffff7050;
    private float mRadius;
    private float mBarWidth;
    private Bitmap mBitmap;
    private Path mPlayPath;
    /**
     * 0.0~1.0
     */
    private float mProgress;
    /**
     * 矩形范围
     */
    private RectF mRectF;
    private Matrix mMatrix;
    private BitmapShader mShader;
    private int mDegree;
    private boolean isPlaying;

    private ValueAnimator mAnimator;
    private CornerPathEffect mPathEffect;

    public GlobalPlay(Context context) {
        this(context, null);
    }

    public GlobalPlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.notification_default));
        mBpPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mMatrix = new Matrix();
        mAnimator = ValueAnimator.ofInt(360);
        mAnimator.setDuration(5000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(this);
        mPathEffect = new CornerPathEffect(NavigationUtil.dip2px(getContext(), 2));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //0,先将坐标移至中心点
        canvas.translate(mWidth/2, mHeight /2);
        canvas.rotate(-90);
        //1.画未到达进度条弧形
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mUnreachColor);
        mPaint.setStrokeWidth(mBarWidth);
        canvas.drawArc(mRectF, mProgress * 360, (1 - mProgress) * 360, false, mPaint);
        //2.画到达进度条弧形
        mPaint.setColor(mReachedColor);
        canvas.drawArc(mRectF, 0, mProgress * 360, false, mPaint);
        canvas.rotate(90);
        //3.画圆形图片
        mMatrix.setRotate(mDegree, mBitmap.getWidth() >> 1, mBitmap.getHeight() >> 1);
        mMatrix.postTranslate(-mBitmap.getWidth() >> 1, -mBitmap.getHeight() >> 1);
        float scale = (mRadius - mBarWidth) * 1.0f / (Math.min(mBitmap.getWidth(), mBitmap.getHeight()) >> 1);
        mMatrix.postScale(scale, scale);
        mShader.setLocalMatrix(mMatrix);
        mBpPaint.setShader(mShader);
        canvas.drawCircle(0, 0, mRadius - mBarWidth, mBpPaint);
        //4.绘制半透明蒙版
        if (isPlaying)
            return;
        mPaint.setColor(0x88ffffff);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, mRadius - mBarWidth, mPaint);
        mPaint.setColor(mReachedColor);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setPathEffect(mPathEffect);
        canvas.translate(mRadius / 2.3f, 0);
        //5.绘制开始播放按钮
        canvas.drawPath(mPlayPath, mPaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mDegree = (int) animation.getAnimatedValue();
        invalidate();
    }

    public void play(String avatarUrl) {

        GlideApp.with(this)
                .load(avatarUrl)
                .error(R.drawable.notification_default)
                .placeholder(R.drawable.notification_default)
                .into(new SimpleTarget<Drawable>((int)mWidth,(int)mHeight) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        setBitmap(ImageUtils.drawable2Bitmap(resource));
                        isPlaying = true;
                        mAnimator.start();
                    }
                });
    }

    public void play(@DrawableRes int res) {
        setBitmap(BitmapFactory.decodeResource(getResources(), res));
        isPlaying = true;
        mAnimator.start();
    }

    public void pause() {
        mAnimator.pause();
        isPlaying = false;
        invalidate();
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    public void show() {
        this.animate().translationY(0).setDuration(300).withStartAction(() -> {
            if (isPlaying)
                mAnimator.start();
            setVisibility(VISIBLE);
        });
    }

    public void hide() {
        this.animate().translationY(getHeight()).setDuration(300).withEndAction(() -> {
            setVisibility(GONE);
            mAnimator.pause();
        });
    }

    public void setImage(String avatarUrl) {
        GlideApp.with(this)
                .load(avatarUrl)
                .error(R.drawable.notification_default)
                .placeholder(R.drawable.notification_default)
                .into(new SimpleTarget<Drawable>((int)mWidth, (int)mHeight) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        setBitmap(ImageUtils.drawable2Bitmap(resource));
                        invalidate();
                    }
                });
    }

    public void setImage(@DrawableRes int res) {
        setBitmap(BitmapFactory.decodeResource(getResources(), res));
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    public void setRadius(float radius) {
        mRadius = radius;
        mRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        //考虑线条宽度,向内缩小半个宽度
        mRectF.inset(mBarWidth/2, mBarWidth/2);
        mPlayPath = new Path();
        mPlayPath.moveTo(0, 0);
        mPlayPath.lineTo(-mRadius / 1.4f, (float) (Math.tan(Math.toRadians(30)) * mRadius / 1.4f));
        mPlayPath.lineTo(-mRadius / 1.4f, -(float) (Math.tan(Math.toRadians(30)) * mRadius / 1.4f));
        mPlayPath.close();
    }

    public void setBarWidth(float barWidth) {
        mBarWidth = barWidth;
        setRadius(mRadius);
    }
}
