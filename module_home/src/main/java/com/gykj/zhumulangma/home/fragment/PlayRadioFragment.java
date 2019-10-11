package com.gykj.zhumulangma.home.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.PlayRadioAdapter;
import com.gykj.zhumulangma.home.dialog.PlayRadioPopup;
import com.gykj.zhumulangma.home.dialog.PlaySchedulePopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.PlayRadioViewModel;
import com.lxj.xpopup.XPopup;
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

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.lxj.xpopup.enums.PopupAnimation.TranslateFromBottom;

/**
 * Author: Thomas.
 * Date: 2019/9/5 11:16
 * Email: 1071931588@qq.com
 * Description:
 */
@Route(path = AppConstants.Router.Home.F_PLAY_RADIIO)
public class PlayRadioFragment extends BaseMvvmFragment<PlayRadioViewModel> implements
        View.OnClickListener, PlaySchedulePopup.onSelectedListener, IXmPlayerStatusListener,
        IXmAdsStatusListener, OnSeekChangeListener, View.OnTouchListener {
    private PlaySchedulePopup mSchedulePopup;
    private XmPlayerManager mPlayerManager = XmPlayerManager.getInstance(mActivity);
    private Schedule mSchedule;
    private IndicatorSeekBar isbProgress;
    private LottieAnimationView lavPlayPause;
    private LottieAnimationView lavBuffering;
    private LottieAnimationView lavPlayNext;
    private LottieAnimationView lavPlayPre;
    private boolean isPlaying;

    private PlayRadioPopup mPlayRadioPopup;
    private Handler mHandler;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_play_radio;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }
    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }
    @Override
    protected void initView(View view) {
        mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_left).setRotation(-90);
        fd(R.id.iv_item_play).setVisibility(View.GONE);
        mSchedulePopup = new PlaySchedulePopup(mActivity, this);
        isbProgress = fd(R.id.ib_progress);
        lavBuffering = fd(R.id.lav_buffering);
        lavPlayPause = fd(R.id.lav_play_pause);
        lavPlayNext = fd(R.id.lav_next);
        lavPlayPre = fd(R.id.lav_pre);
        new Handler().postDelayed(() -> {
            if (mPlayerManager.isPlaying()) {
                if (mPlayerManager.isAdPlaying()) {
                    bufferingAnim();
                } else {
                    playingAnim();
                }
            }
        }, 100);

        mPlayRadioPopup=new PlayRadioPopup(mActivity);
    }

    @Override
    public void initListener() {
        super.initListener();
        fd(R.id.iv_history).setOnClickListener(this);
        fd(R.id.tv_history).setOnClickListener(this);
        fd(R.id.tv_play_list).setOnClickListener(this);
        fd(R.id.iv_play_list).setOnClickListener(this);
        fd(R.id.fl_play_pause).setOnClickListener(this);
        lavPlayNext.setOnClickListener(this);
        lavPlayPre.setOnClickListener(this);
        mPlayerManager.addPlayerStatusListener(this);
        mPlayerManager.addAdsStatusListener(this);
        isbProgress.setOnSeekChangeListener(this);
        isbProgress.setOnTouchListener(this);
    }

    @Override
    public void initData() {
        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            try {
                mSchedule = (Schedule) mPlayerManager.getCurrSound();
                setTitle(new String[]{mSchedule.getRadioName()});
                ((TextView) fd(R.id.tv_radio_name)).setText(mSchedule.getRadioName());
                ((TextView) fd(R.id.tv_playcount)).setText(ZhumulangmaUtil.toWanYi(mSchedule.getRadioPlayCount()) + "人听过");
                mViewModel._getPrograms(String.valueOf(mSchedule.getRadioId()));

                ((TextView) fd(R.id.tv_time)).setText(mSchedule.getStartTime().substring(mSchedule.getStartTime().length() - 5) + "~"
                        + mSchedule.getEndTime().substring(mSchedule.getEndTime().length() - 5));
                isbProgress.setUserSeekAble(mPlayerManager.getCurrPlayType() == XmPlayListControl.PLAY_SOURCE_TRACK);

                Program program = mSchedule.getRelatedProgram();
                ((TextView) fd(R.id.tv_program_name)).setText(program.getProgramName());
                Glide.with(this).load(program.getBackPicUrl()).into((ImageView) fd(R.id.iv_cover));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < program.getAnnouncerList().size(); i++) {
                    sb.append(program.getAnnouncerList().get(i).getNickName());
                    if (i != program.getAnnouncerList().size() - 1) {
                        sb.append(",");
                    }
                }
                ((TextView) fd(R.id.tv_announcer_name)).setText("主播: "
                        + (TextUtils.isEmpty(sb.toString()) ? mSchedule.getRadioName() : sb.toString()));
                initProgress(mPlayerManager.getPlayCurrPositon(), mPlayerManager.getDuration());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast("网络异常");
                pop();
            }
        }, 200);

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
        ((TextView) fd(R.id.tv_current)).setText(ZhumulangmaUtil.secondToTimeE(cur / 1000));
        ((TextView) fd(R.id.tv_duration)).setText(ZhumulangmaUtil.secondToTimeE(dur / 1000));
        if (!isTouch) {
            isbProgress.setMax((float) dur / 1000);
            isbProgress.setProgress((float) cur / 1000);
        }
    }

    @Override
    public void initViewObservable() {
        mViewModel.getProgramsSingleLiveEvent().observe(this, programList ->
                ((TextView) fd(R.id.tv_desc)).setText(getString(R.string.playing,
                        programList.getmProgramList().get(0).getProgramName())));
        mViewModel.getYestodaySingleLiveEvent().observe(this, schedules ->
                mPlayRadioPopup.getYestodayAdapter().setNewData(schedules));
        mViewModel.getTodaySingleLiveEvent().observe(this, schedules ->
                mPlayRadioPopup.getTodayAdapter().setNewData(schedules));
        mViewModel.getTomorrowSingleLiveEvent().observe(this, schedules ->
                mPlayRadioPopup.getTomorrowAdapter().setNewData(schedules));
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
                    tvTitle.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
                    if (XmPlayerManager.getInstance(mActivity).isPlaying()) {
                        lavPlaying.playAnimation();
                    } else {
                        lavPlaying.pauseAnimation();
                    }
                } else {
                    lavPlaying.cancelAnimation();
                    tvTitle.setTextColor(mActivity.getResources().getColor(R.color.textColorPrimary));
                    lavPlaying.setVisibility(View.GONE);
                }
            }else {
                adapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    protected int onBindBarLeftStyle() {
        return BarStyle.LEFT_ICON;
    }

    @Override
    protected Integer onBindBarLeftIcon() {
        return R.drawable.ic_common_titlebar_back;
    }

    @Override
    protected Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_home_dingshi};
    }

    @Override
    protected void onRight1Click(View v) {
        super.onRight1Click(v);
        new XPopup.Builder(getContext()).setPopupCallback(new SimpleCallback(){
            @Override
            public void beforeShow() {
                super.beforeShow();
                mSchedulePopup.getScheduleAdapter().notifyDataSetChanged();
            }
        }).asCustom(mSchedulePopup).show();
    }

    @Override
    protected void onLeftIconClick(View v) {
        super.onLeftIconClick(v);
        pop();
    }

    @Override
    public boolean onBackPressedSupport() {
        if(super.onBackPressedSupport()){
            return true;
        }else if (mSchedulePopup != null && mSchedulePopup.getPickerView() != null && mSchedulePopup.getPickerView().isShowing()) {
            mSchedulePopup.getPickerView().dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.HIDE_GP));
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.SHOW_GP));
    }

    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_history || id == R.id.tv_history) {
            pop();
            navigateTo(AppConstants.Router.Listen.F_HISTORY);
        } else if (id == R.id.iv_play_list || id == R.id.tv_play_list) {
            if(mSchedule==null){
                return;
            }
            new XPopup.Builder(getContext()).popupAnimation(TranslateFromBottom).setPopupCallback(new SimpleCallback() {
                @Override
                public void onCreated() {
                    super.onCreated();
                   mViewModel._getSchedules(String.valueOf(mSchedule.getRadioId()));
                }
            }).enableDrag(false).asCustom(mPlayRadioPopup).show();
        } else if (R.id.fl_play_pause == id) {
            if (mPlayerManager.isPlaying()) {
                mPlayerManager.pause();
            } else {
                mPlayerManager.play();
            }
        } else if (R.id.lav_pre == id) {
            lavPlayPre.playAnimation();
            if (mPlayerManager.hasPreSound()) {
                mPlayerManager.playPre();
            } else {
                ToastUtil.showToast("没有更多");
            }
        } else if (R.id.lav_next == id) {
            lavPlayNext.playAnimation();
            if (mPlayerManager.hasNextSound()) {
                mPlayerManager.playNext();
            } else {
                ToastUtil.showToast("没有更多");
            }
        }
    }

    @Override
    public void onNewBundle(Bundle args) {
        super.onNewBundle(args);
        new Handler().postDelayed(()->{
            if(mSchedule==null){
                return;
            }
            if (mPlayRadioPopup.getYestodayAdapter() != null) {
                mViewModel._getSchedules(mSchedule.getRadioName());
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
        if (SPUtils.getInstance().getInt(AppConstants.SP.PLAY_SCHEDULE_TYPE, 0) == 1) {
            SPUtils.getInstance().put(AppConstants.SP.PLAY_SCHEDULE_TYPE, 0);

        } else if (!mPlayerManager.hasNextSound()) {
            pauseAnim();
        }
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

            lavPlayPause.setMinAndMaxFrame(55, 90);
            lavPlayPause.loop(false);
            lavPlayPause.playAnimation();
            lavBuffering.cancelAnimation();
            lavBuffering.setVisibility(View.GONE);
            lavPlayPause.setVisibility(View.VISIBLE);
            lavPlayPause.addAnimatorListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    playingAnim();
                    lavPlayPause.removeAnimatorListener(this);
                }
            });
        }
    }

    private void playingAnim() {
        lavPlayPause.removeAllAnimatorListeners();
        isPlaying = true;
        lavPlayPause.setMinAndMaxFrame(90, 170);
        lavPlayPause.loop(true);
        lavPlayPause.playAnimation();
        lavBuffering.cancelAnimation();
        lavBuffering.setVisibility(View.GONE);
        lavPlayPause.setVisibility(View.VISIBLE);
    }

    private void bufferingAnim() {

        lavPlayPause.cancelAnimation();
        lavBuffering.playAnimation();
        isPlaying = false;
        lavPlayPause.setVisibility(View.GONE);
        lavBuffering.setVisibility(View.VISIBLE);
    }

    private void pauseAnim() {
        lavBuffering.cancelAnimation();
        lavPlayPause.removeAllAnimatorListeners();
        isPlaying = false;
        lavPlayPause.setMinAndMaxFrame(180, 210);
        lavPlayPause.loop(false);
        lavPlayPause.playAnimation();
        lavBuffering.setVisibility(View.GONE);
        lavPlayPause.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSeeking(SeekParams seekParams) {
        TextView indicator = isbProgress.getIndicator().getTopContentView().findViewById(R.id.tv_indicator);
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
        mHandler.removeCallbacksAndMessages(null);
        mPlayerManager.removePlayerStatusListener(this);
        mPlayerManager.removeAdsStatusListener(this);
    }

    @Override
    public Class<PlayRadioViewModel> onBindViewModel() {
        return PlayRadioViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
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
