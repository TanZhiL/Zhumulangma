package com.gykj.zhumulangma.home.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmActivity;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.PlayRadioAdapter;
import com.gykj.zhumulangma.home.databinding.HomeActivityPlayRadioBinding;
import com.gykj.zhumulangma.home.dialog.PlayRadioPopup;
import com.gykj.zhumulangma.home.dialog.PlaySchedulePopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayRadioViewModel;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.lxj.xpopup.enums.PopupAnimation.TranslateFromBottom;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/5 11:16
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
@Route(path = Constants.Router.Home.F_PLAY_RADIIO)
public class PlayRadioActivity extends BaseMvvmActivity<HomeActivityPlayRadioBinding,PlayRadioViewModel> implements
        View.OnClickListener, PlaySchedulePopup.onSelectedListener, IXmPlayerStatusListener,
        IXmAdsStatusListener, OnSeekChangeListener, View.OnTouchListener {
    private PlaySchedulePopup mSchedulePopup;
    private final XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(this);
    private Schedule mSchedule;
    private boolean isPlaying;

    private PlayRadioPopup mPlayRadioPopup;

    @Override
    public int onBindLayout() {
        return R.layout.home_activity_play_radio;
    }
    @Override
    public void initView() {
        mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_left).setRotation(-90);
        mBinding.includeItemRadio.ivItemPlay.setVisibility(View.GONE);
        mSchedulePopup = new PlaySchedulePopup(this, this);
        mHandler.postDelayed(() -> {
            if (mPlayerManager.isPlaying()) {
                if (mPlayerManager.isAdPlaying()) {
                    bufferingAnim();
                } else {
                    playingAnim();
                }
            }
        }, 100);

        mPlayRadioPopup=new PlayRadioPopup(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.ivHistory.setOnClickListener(this);
        mBinding.tvHistory.setOnClickListener(this);
        mBinding.tvPlayList.setOnClickListener(this);
        mBinding.ivPlayList.setOnClickListener(this);
        mBinding.flPlayPause.setOnClickListener(this);
        mBinding.lavNext.setOnClickListener(this);
        mBinding.lavPre.setOnClickListener(this);
        mPlayerManager.addPlayerStatusListener(this);
        mPlayerManager.addAdsStatusListener(this);
        mBinding.isbProgress.setOnSeekChangeListener(this);
        mBinding.isbProgress.setOnTouchListener(this);
    }

    @Override
    public void initData() {
        postDelayed(() -> {
            try {
                mSchedule = (Schedule) mPlayerManager.getCurrSound();
                setTitle(new String[]{mSchedule.getRadioName()});
                mBinding.includeItemRadio.tvRadioName.setText(mSchedule.getRadioName());
                mBinding.includeItemRadio.tvPlaycount.setText(ZhumulangmaUtil.toWanYi(mSchedule.getRadioPlayCount()) + "人听过");
                mViewModel.getPrograms(String.valueOf(mSchedule.getRadioId()));

                mBinding.tvTime.setText(mSchedule.getStartTime().substring(mSchedule.getStartTime().length() - 5) + "~"
                        + mSchedule.getEndTime().substring(mSchedule.getEndTime().length() - 5));
                mBinding.isbProgress.setUserSeekAble(mPlayerManager.getCurrPlayType() == XmPlayListControl.PLAY_SOURCE_TRACK);

                Program program = mSchedule.getRelatedProgram();
                mBinding.tvProgramName.setText(program.getProgramName());
                Glide.with(this).load(program.getBackPicUrl()).into(mBinding.includeItemRadio.ivCover);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < program.getAnnouncerList().size(); i++) {
                    sb.append(program.getAnnouncerList().get(i).getNickName());
                    if (i != program.getAnnouncerList().size() - 1) {
                        sb.append(",");
                    }
                }
                mBinding.tvAnnouncerName.setText("主播: "
                        + (TextUtils.isEmpty(sb.toString()) ? mSchedule.getRadioName() : sb.toString()));
                initProgress(mPlayerManager.getPlayCurrPositon(), mPlayerManager.getDuration());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast("网络异常");
                finish();
            }
        }, 0);

    }

    private void initProgress(int cur, int dur) {
        if (BaseUtil.isInTime(mSchedule.getStartTime() + "-" + mSchedule.getEndTime()) == 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:HH:mm", Locale.getDefault());
            try {
                long start = sdf.parse(mSchedule.getStartTime()).getTime();
                long end = sdf.parse(mSchedule.getEndTime()).getTime();
                cur = (int) (System.currentTimeMillis() - start);
                dur = (int) (end - start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mBinding.tvCurrent.setText(ZhumulangmaUtil.secondToTimeE(cur / 1000));
        mBinding.tvDuration.setText(ZhumulangmaUtil.secondToTimeE(dur / 1000));
        if (!isTouch) {
            mBinding.isbProgress.setMax((float) dur / 1000);
            mBinding.isbProgress.setProgress((float) cur / 1000);
        }
    }

    @Override
    public void initViewObservable() {
        mViewModel.getProgramsEvent().observe(this, programList ->
                mBinding.includeItemRadio.tvDesc.setText(getString(R.string.playing,
                        programList.getmProgramList().get(0).getProgramName())));
        mViewModel.getYestodayEvent().observe(this, schedules ->
                mPlayRadioPopup.getYestodayAdapter().setNewData(schedules));
        mViewModel.getTodayEvent().observe(this, schedules ->
                mPlayRadioPopup.getTodayAdapter().setNewData(schedules));
        mViewModel.getTomorrowEvent().observe(this, schedules ->
                mPlayRadioPopup.getTomorrowAdapter().setNewData(schedules));
        mViewModel.getPauseAnimEvent().observe(this, aVoid -> pauseAnim());
    }


    private void updatePlayStatus() {
        updatePlayStatus(mPlayRadioPopup.getYestodayAdapter());
        updatePlayStatus(mPlayRadioPopup.getTodayAdapter());
    }

    private void updatePlayStatus(PlayRadioAdapter adapter) {
        if (adapter== null) {
            return;
        }
        Schedule schedule = (Schedule) mPlayerManager.getCurrSound();
        if (null == schedule) {
            return;
        }
        List<Schedule> schedules = adapter.getData();

        for (int i = 0; i < schedules.size(); i++) {
            LottieAnimationView lavPlaying = (LottieAnimationView) adapter
                    .getViewByPosition(i, R.id.lav_playing);
            TextView tvTitle = (TextView) adapter
                    .getViewByPosition(i, R.id.tv_title);
            if (null != lavPlaying && tvTitle != null) {
                if (schedules.get(i).getDataId() == schedule.getDataId()) {
                    lavPlaying.setVisibility(View.VISIBLE);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                    if (XmPlayerManager.getInstance(this).isPlaying()) {
                        lavPlaying.playAnimation();
                    } else {
                        lavPlaying.pauseAnimation();
                    }
                } else {
                    lavPlaying.cancelAnimation();
                    tvTitle.setTextColor(getResources().getColor(R.color.textColorPrimary));
                    lavPlaying.setVisibility(View.GONE);
                }
            }else {
                adapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    public SimpleBarStyle onBindBarLeftStyle() {
        return SimpleBarStyle.LEFT_ICON;
    }

    @Override
    public Integer onBindBarLeftIcon() {
        return R.drawable.ic_common_titlebar_back;
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_home_dingshi};
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        new XPopup.Builder(this).setPopupCallback(new SimpleCallback(){
            @Override
            public void beforeShow(BasePopupView popupView) {
                super.beforeShow(popupView);
                mSchedulePopup.getScheduleAdapter().notifyDataSetChanged();
            }
        }).asCustom(mSchedulePopup).show();
    }

    @Override
    public void onLeftIconClick(View v) {
        super.onLeftIconClick(v);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mSchedulePopup != null && mSchedulePopup.getPickerView() != null && mSchedulePopup.getPickerView().isShowing()) {
            mSchedulePopup.getPickerView().dismiss();
        }else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_history || id == R.id.tv_history) {
            finish();
            RouteHelper.navigateTo(Constants.Router.Listen.F_HISTORY);
        } else if (id == R.id.iv_play_list || id == R.id.tv_play_list) {
            if(mSchedule==null){
                return;
            }
            new XPopup.Builder(this).popupAnimation(TranslateFromBottom).setPopupCallback(new SimpleCallback() {
                @Override
                public void onCreated(BasePopupView popupView) {
                    super.onCreated(popupView);
                   mViewModel.getSchedules(String.valueOf(mSchedule.getRadioId()));
                }
            }).enableDrag(false).asCustom(mPlayRadioPopup).show();
        } else if (R.id.fl_play_pause == id) {
            if (mPlayerManager.isPlaying()) {
                mPlayerManager.pause();
            } else {
                mPlayerManager.play();
            }
        } else if (R.id.lav_pre == id) {
            mBinding.lavPre.playAnimation();
            if (mPlayerManager.hasPreSound()) {
                mPlayerManager.playPre();
            } else {
                ToastUtil.showToast("没有更多");
            }
        } else if (R.id.lav_next == id) {
            mBinding.lavNext.playAnimation();
            if (mPlayerManager.hasNextSound()) {
                mPlayerManager.playNext();
            } else {
                ToastUtil.showToast("没有更多");
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        postDelayed(()->{
            if(mSchedule==null){
                return;
            }
            if (mPlayRadioPopup.getYestodayAdapter() != null) {
                mViewModel.getSchedules(mSchedule.getRadioName());
            }
        },100);
    }

    @Override
    public void onSelected(int type, long time) {

    }


    @Override
    public void onStartGetAdsInfo() {
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
    }


    @Override
    public void onAdsStartBuffering() {
        bufferingAnim();
    }

    @Override
    public void onAdsStopBuffering() {
    }


    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        bufferingAnim();
    }

    @Override
    public void onCompletePlayAds() {
        //    playAnim();
        //    Log.e(TAG, "onCompletePlayAds() playAnim");
    }

    @Override
    public void onError(int i, int i1) {
    }

    @Override
    public void onPlayStart() {
        updatePlayStatus();
        if (!mPlayerManager.isBuffering()) {

            playAnim();
        }

    }

    @Override
    public void onPlayPause() {
        updatePlayStatus();
        pauseAnim();
    }

    @Override
    public void onPlayStop() {
        updatePlayStatus();
        pauseAnim();
    }

    @Override
    public void onSoundPlayComplete() {

        updatePlayStatus();
        mViewModel.onPlayComplete(mPlayerManager);
    }

    @Override
    public void onSoundPrepared() {
        updatePlayStatus();
    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        updatePlayStatus();
        initData();
    }

    @Override
    public void onBufferingStart() {
        if (mPlayerManager.isPlaying()) {
            bufferingAnim();
        }
    }

    @Override
    public void onBufferingStop() {
        if (mPlayerManager.isPlaying()) {
            playAnim();
        } else {
            pauseAnim();
        }
    }

    @Override
    public void onBufferProgress(int i) {
    }

    @Override
    public void onPlayProgress(int i, int i1) {
        if (mSchedule == null) {
            return;
        }
        initProgress(i, i1);

    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    private void playAnim() {
        if (!isPlaying) {

            mBinding.lavPlayPause.setMinAndMaxFrame(55, 90);
            mBinding.lavPlayPause.loop(false);
            mBinding.lavPlayPause.playAnimation();
            mBinding.lavBuffering.cancelAnimation();
            mBinding.lavBuffering.setVisibility(View.GONE);
            mBinding.lavPlayPause.setVisibility(View.VISIBLE);
            mBinding.lavPlayPause.addAnimatorListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    playingAnim();
                    mBinding.lavPlayPause.removeAnimatorListener(this);
                }
            });
        }
    }

    private void playingAnim() {
        mBinding.lavPlayPause.removeAllAnimatorListeners();
        isPlaying = true;
        mBinding.lavPlayPause.setMinAndMaxFrame(90, 170);
        mBinding.lavPlayPause.loop(true);
        mBinding.lavPlayPause.playAnimation();
        mBinding.lavBuffering.cancelAnimation();
        mBinding.lavBuffering.setVisibility(View.GONE);
        mBinding.lavPlayPause.setVisibility(View.VISIBLE);
    }

    private void bufferingAnim() {

        mBinding.lavPlayPause.cancelAnimation();
        mBinding.lavBuffering.playAnimation();
        isPlaying = false;
        mBinding.lavPlayPause.setVisibility(View.GONE);
        mBinding.lavBuffering.setVisibility(View.VISIBLE);
    }

    private void pauseAnim() {
        mBinding.lavBuffering.cancelAnimation();
        mBinding.lavPlayPause.removeAllAnimatorListeners();
        isPlaying = false;
        mBinding.lavPlayPause.setMinAndMaxFrame(180, 210);
        mBinding.lavPlayPause.loop(false);
        mBinding.lavPlayPause.playAnimation();
        mBinding.lavBuffering.setVisibility(View.GONE);
        mBinding.lavPlayPause.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSeeking(SeekParams seekParams) {
        TextView indicator = mBinding.isbProgress.getIndicator().getTopContentView().findViewById(R.id.tv_indicator);
        indicator.setText(ZhumulangmaUtil.secondToTimeE(seekParams.progress)
                + "/" + ZhumulangmaUtil.secondToTimeE((long) seekParams.seekBar.getMax()));
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
        isTouch = true;
    }

    boolean isTouch;

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        mPlayerManager.seekTo(seekBar.getProgress() * 1000);
        mHandler.postDelayed(touchRunable,200);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPlayerManager.removePlayerStatusListener(this);
        mPlayerManager.removeAdsStatusListener(this);
    }

    @Override
    public Class<PlayRadioViewModel> onBindViewModel() {
        return PlayRadioViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(getApplication());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            mHandler.removeCallbacks(touchRunable);
            isTouch=true;
        }else if(event.getAction()==MotionEvent.ACTION_UP){
        }
        return false;
    }

    private Runnable touchRunable= new Runnable() {
        @Override
        public void run() {
            isTouch=false;
        }
    };
}
