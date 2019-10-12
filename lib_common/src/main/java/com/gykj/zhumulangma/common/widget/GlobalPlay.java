package com.gykj.zhumulangma.common.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gykj.zhumulangma.common.R;

/**
 * Created by 10719
 * on 2019/6/18
 */
public class GlobalPlay extends FrameLayout {

    private ImageView civAvatar;
    private ImageView ivPlay;
    private ImageView civMask;
    private Animation mAnimation;
    private CircleProgressBar mCircleProgressBar;

    public GlobalPlay(@NonNull Context context) {
        this(context,null);
    }

    public GlobalPlay(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GlobalPlay(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.common_widget_global_play,this);
        civAvatar=findViewById(R.id.civ_avatar);
        ivPlay=findViewById(R.id.iv_play);
        civMask=findViewById(R.id.civ_mask);
        mCircleProgressBar=findViewById(R.id.cpb_progress);
        mAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mAnimation.setDuration(5000);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(new LinearInterpolator());
    }
    public void play(String avatarUrl){
        ivPlay.setVisibility(GONE);
        civMask.setVisibility(GONE);
        Glide.with(getContext()).load(avatarUrl).into(civAvatar);
        civAvatar.startAnimation(mAnimation);
    }
    public void play(@DrawableRes int res){
        ivPlay.setVisibility(GONE);
        civMask.setVisibility(GONE);
        Glide.with(getContext()).load(res).into(civAvatar);
        civAvatar.startAnimation(mAnimation);
    }
    public void pause(){
        ivPlay.setVisibility(VISIBLE);
        civMask.setVisibility(VISIBLE);
        civAvatar.clearAnimation();
    }
    public void setProgress(float progress){
        mCircleProgressBar.setProgress((int) (progress*100));
    }
    public void show(){
        this.animate().translationY(0).setDuration(300).withStartAction(()->setVisibility(VISIBLE));
    }
    public void hide(){
        this.animate().translationY(getHeight()).setDuration(300).withEndAction(()->setVisibility(GONE));
    }

    public void setImage(String url){
        Glide.with(getContext()).load(url).into(civAvatar);
    }
    public void setImage(@DrawableRes int res){
        Glide.with(getContext()).load(res).into(civAvatar);
    }
}
