package com.ximalaya.ting.android.opensdk.test;

import android.Manifest;
import android.app.ActionBar;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.NotificationChannelUtils;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.test.download.DownloadTrackActivity;
import com.ximalaya.ting.android.opensdk.test.fragment.AlbumListFragment;
import com.ximalaya.ting.android.opensdk.test.fragment.PayTrackFragment;
import com.ximalaya.ting.android.opensdk.test.fragment.RadiosFragment;
import com.ximalaya.ting.android.opensdk.test.fragment.ScheduleFragment;
import com.ximalaya.ting.android.opensdk.test.fragment.TracksFragment;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.test.pay.PayActivity;
import com.ximalaya.ting.android.opensdk.test.util.ToolUtil;
import com.ximalaya.ting.android.opensdk.util.NetworkType;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:MainFragmentActivity
 *
 * @author jack.qin
 * @Date 2015-5-25 下午5:51:12
 * @see
 * @since Ver 1.1
 */
public class MainFragmentActivity extends FragmentActivity {
    private static final String[] CONTENT = new String[]{"点播", "直播", "节目表" ,"付费","专辑" };
    private static final String TAG = "MainFragmentActivity";

    private TextView mTextView;
    private TextView mTime;
    private ImageButton mBtnPreSound;
    private ImageButton mBtnPlay;
    private ImageButton mBtnNextSound;
    private SeekBar mSeekBar;
    private ImageView mSoundCover;
    private ProgressBar mProgress;

    private ViewPager mViewPager;
    private PagerTabStrip mIndicator;
    private PagerAdapter mAdapter;

    private Context mContext;

    private XmPlayerManager mPlayerManager;

    private boolean mUpdateProgress = true;

    private TracksFragment mTracksFragment;
    private RadiosFragment mRadiosFragment;
    private ScheduleFragment mScheduleFragment;
    private AlbumListFragment mAlbumListFragment;
    private PayTrackFragment mPayTrackFragment;
    private BaseFragment mCurrFragment;

    private static final int noCrashNotifation = 10000;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        initView();

        mPlayerManager = XmPlayerManager.getInstance(mContext);

        boolean useXmPlayNotification = true;

        if(useXmPlayNotification) {
            Notification mNotification = XmNotificationCreater.getInstanse(this).initNotification(this.getApplicationContext(), MainFragmentActivity.class);

            // 如果之前贵方使用了 `XmPlayerManager.init(int id, Notification notification)` 这个初始化的方式
            // 请参考`4.8 播放器通知栏使用`重新添加新的通知栏布局,否则直接升级可能导致在部分手机播放时崩溃
            // 如果不想使用sdk内部搞好的notification,或者想自建notification 可以使用下面的  init()函数进行初始化
            mPlayerManager.init((int) System.currentTimeMillis(), mNotification);
        } else {
            mPlayerManager.init();
            // https://developer.android.com/about/versions/oreo/background?hl=zh-cn#services 防止后台启动service后导致崩溃问题
            Notification notification = NotificationChannelUtils.newNotificationBuilder(getApplication())
                    .setContentTitle("防止崩溃notification").setSmallIcon(R.drawable.notification_default).build();
            mPlayerManager.setNotificationForNoCrash(noCrashNotifation ,notification);
        }

        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);
        mPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                mPlayerManager.removeOnConnectedListerner(this);

                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                Toast.makeText(MainFragmentActivity.this, "播放器初始化成功", Toast.LENGTH_SHORT).show();
            }
        });

        // 此代码表示播放时会去监测下是否已经下载
        XmPlayerManager.getInstance(this).setCommonBusinessHandle(XmDownloadManager.getInstance());

        // 如果已经登录uid不会为null
        Toast.makeText(MainFragmentActivity.this, "" + AccessTokenManager.getInstanse().getUid(), Toast.LENGTH_SHORT).show();

        ActivityCompat.requestPermissions(this ,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE} ,0);
    }

    @Override
    protected void onDestroy() {
        if (mPlayerManager != null) {
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        }
        XmPlayerManager.release();
        CommonRequest.release();
        super.onDestroy();
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Log.i(TAG, "onSoundPrepared");
            mSeekBar.setEnabled(true);
            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            Log.i(TAG, "onSoundSwitch index:" + curModel);
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String coverUrl = null;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    coverUrl = info.getCoverUrlLarge();
                } else if (model instanceof Schedule) {
                    Schedule program = (Schedule) model;
                    title = program.getRelatedProgram().getProgramName();
                    coverUrl = program.getRelatedProgram().getBackPicUrl();
                } else if (model instanceof Radio) {
                    Radio radio = (Radio) model;
                    title = radio.getRadioName();
                    coverUrl = radio.getCoverUrlLarge();
                }
                mTextView.setText(title);
                x.image().bind(mSoundCover ,coverUrl);
            }
            updateButtonStatus();
        }


        private void updateButtonStatus() {
            if (mPlayerManager.hasPreSound()) {
                mBtnPreSound.setEnabled(true);
            } else {
                mBtnPreSound.setEnabled(false);
            }
            if (mPlayerManager.hasNextSound()) {
                mBtnNextSound.setEnabled(true);
            } else {
                mBtnNextSound.setEnabled(false);
            }
        }

        @Override
        public void onPlayStop() {
            Log.i(TAG, "onPlayStop");
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);
        }

        @Override
        public void onPlayStart() {
            Log.i(TAG, "onPlayStart");
            mBtnPlay.setImageResource(R.drawable.widget_pause_normal);

        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            String title = "";
            PlayableModel info = mPlayerManager.getCurrSound();
            if (info != null) {
                if (info instanceof Track) {
                    title = ((Track) info).getTrackTitle();
                } else if (info instanceof Schedule) {
                    title = ((Schedule) info).getRelatedProgram().getProgramName();
                } else if (info instanceof Radio) {
                    title = ((Radio) info).getRadioName();
                }
            }
            mTextView.setText(title);
            mTime.setText("[" + ToolUtil.formatTime(currPos) + "/" + ToolUtil.formatTime(duration) + "]");
            if (mUpdateProgress && duration != 0) {
                mSeekBar.setProgress((int) (100 * currPos / (float) duration));
            }
            if(info instanceof Track) {
                System.out.println("MainFragmentActivity.onPlayProgress  " + currPos + "   " + duration + "   " + ((Track) info).getDuration());
            }
        }

        @Override
        public void onPlayPause() {
            Log.i(TAG, "onPlayPause");
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);
        }

        @Override
        public void onSoundPlayComplete() {
            Log.i(TAG, "onSoundPlayComplete");
            Toast.makeText(MainFragmentActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            Log.i(TAG, "XmPlayerException = onError " + exception.getMessage() + "   " + XmPlayerManager.getInstance(MainFragmentActivity.this).isPlaying());

            System.out.println("MainFragmentActivity.onError   "+ exception);
            mBtnPlay.setImageResource(R.drawable.widget_play_normal);

            if(!NetworkType.isConnectTONetWork(mContext)) {
                Toast.makeText(MainFragmentActivity.this, "没有网络导致停止播放", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            mSeekBar.setSecondaryProgress(position);
            System.out.println("MainFragmentActivity.onBufferProgress   " + position);
        }

        public void onBufferingStart() {
            Log.i(TAG, "onBufferingStart   " + XmPlayerManager.getInstance(MainFragmentActivity.this).isPlaying());
            mSeekBar.setEnabled(false);
            mProgress.setVisibility(View.VISIBLE);
        }

        public void onBufferingStop() {
            Log.i(TAG, "onBufferingStop");
            mSeekBar.setEnabled(true);
            mProgress.setVisibility(View.GONE);
        }

    };

    private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

        @Override
        public void onStartPlayAds(Advertis ad, int position) {
            Log.i(TAG, "onStartPlayAds, Ad:" + ad.getName() + ", pos:" + position);
//            if (ad != null) {
//                x.image().bind(mSoundCover ,ad.getImageUrl());
//            }
            mBtnPlay.setEnabled(true);
            mBtnPlay.setImageResource(R.drawable.widget_pause_normal);
        }

        @Override
        public void onStartGetAdsInfo() {
            Log.i(TAG, "onStartGetAdsInfo");
            mBtnPlay.setEnabled(false);
            mSeekBar.setEnabled(false);
        }

        @Override
        public void onGetAdsInfo(final AdvertisList ads) {

        }

        @Override
        public void onError(int what, int extra) {
            Log.i(TAG, "onError what:" + what + ", extra:" + extra);
        }

        @Override
        public void onCompletePlayAds() {
            Log.i(TAG, "onCompletePlayAds");
            mBtnPlay.setEnabled(true);
            mSeekBar.setEnabled(true);
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null && model instanceof Track) {
                x.image().bind(mSoundCover ,((Track) model).getCoverUrlLarge());
            }
        }

        @Override
        public void onAdsStopBuffering() {
            Log.i(TAG, "onAdsStopBuffering");
        }

        @Override
        public void onAdsStartBuffering() {
            Log.i(TAG, "onAdsStartBuffering");
        }
    };


    class SlidingPagerAdapter extends FragmentPagerAdapter {
        public SlidingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            if (0 == position) {
                if (mTracksFragment == null) {
                    mTracksFragment = new TracksFragment();
                }
                f = mTracksFragment;
            } else if (1 == position) {
                if (mRadiosFragment == null) {
                    mRadiosFragment = new RadiosFragment();
                }
                f = mRadiosFragment;
            } else if (2 == position) {
                if (mScheduleFragment == null) {
                    mScheduleFragment = new ScheduleFragment();
                }
                f = mScheduleFragment;
            } else if(4 == position) {
                if(mAlbumListFragment == null) {
                    mAlbumListFragment = new AlbumListFragment();
                }
                f = mAlbumListFragment;
            } else if(3 == position) {
                if(mPayTrackFragment == null) {
                    mPayTrackFragment = new PayTrackFragment();
                }
                f = mPayTrackFragment;
            }
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            menu.add(0 ,1 ,0 ,"下载").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add(0 ,4 ,0 ,"付费").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add(0 ,5 ,0 ,"登录").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        menu.add(0 ,2 ,0 ,"测试1");
        menu.add(0 ,3 ,0 ,"测试2");
        menu.add(0 ,6 ,0 ,"测试3");
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        ActionBar actionBar = getActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Open SDK Demo");

        setContentView(R.layout.act_main);
        mContext = MainFragmentActivity.this;

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (PagerTabStrip) findViewById(R.id.indicator);
        mTextView = (TextView) findViewById(R.id.message);
        mTime = (TextView) findViewById(R.id.time);
        mBtnPreSound = (ImageButton) findViewById(R.id.pre_sound);
        mBtnPlay = (ImageButton) findViewById(R.id.play_or_pause);
        mBtnNextSound = (ImageButton) findViewById(R.id.next_sound);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSoundCover = (ImageView) findViewById(R.id.sound_cover);
        mProgress = (ProgressBar) findViewById(R.id.buffering_progress);

        mViewPager.setOffscreenPageLimit(2);
        mIndicator.setTabIndicatorColor(Color.RED);
        mIndicator.setTextColor(Color.RED);

        mAdapter = new SlidingPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 0) {
                    mCurrFragment = mTracksFragment;
                } else if (arg0 == 1) {
                    mCurrFragment = mRadiosFragment;
                } else if (arg0 == 2) {
                    mCurrFragment = mScheduleFragment;
                    if (mCurrFragment != null) {
                        mCurrFragment.refresh();
                    }
                }  else if(arg0 == 3) {
                    mCurrFragment = mAlbumListFragment;
                    if(mCurrFragment != null) {
                        mCurrFragment.refresh();
                    }
                }
                else {
                    mCurrFragment = null;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                System.out.println("淡定  ===  ");
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayerManager.seekToByPercent(seekBar.getProgress() / (float) seekBar.getMax());
                mUpdateProgress = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateProgress = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });

        mBtnPreSound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPlayerManager.playPre();
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("MainFragmentActivity.onClick   " + mPlayerManager.isPlaying());
                if (mPlayerManager.isPlaying()) {
                    mPlayerManager.pause();
                } else {
                    mPlayerManager.play();
                }
            }
        });

        mBtnNextSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickTime = System.currentTimeMillis();
                mPlayerManager.playNext();
            }
        });
    }

    private long lastClickTime;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == 1) {
            startActivity(new Intent(MainFragmentActivity.this , DownloadTrackActivity.class));
        } else if(itemId == 4) {
            startActivity(new Intent(MainFragmentActivity.this , PayActivity.class));
        } else if(itemId == 2) {
            XmPlayerManager.getInstance(mContext).seekToByPercent(0.99f);
        } else if(itemId == 5) {
            startActivity(new Intent(MainFragmentActivity.this ,XMAuthDemoActivity.class));
        } else if(itemId == 3) {
            Map<String ,String> map = new HashMap<String, String>();
            CommonRequest.getBoughtAlbums(map, new IDataCallBack<AlbumList>() {
                @Override
                public void onSuccess(AlbumList object) {
                    System.out.println("object = [" + object + "]");
                }

                @Override
                public void onError(int code, String message) {
                    System.out.println("code = [" + code + "], message = [" + message + "]");
                }
            });
        } else if(itemId == 6) {

            final Track track = new Track();
            track.setKind(PlayableModel.KIND_PAID_TRACK);
            track.setDataId(System.currentTimeMillis());
            track.setType(Track.TYPE_LOCAL_SERVER);
            track.setTrackTitle("asad");
            track.setPlayUrl32("https://vos-video.ccrgt.com/ximalaya/1562917175269.m4a");

            XmPlayerManager.getInstance(MainFragmentActivity.this).playList(new ArrayList<Track>() {
                {
                    add(track);
                }
            },0);

        }

        return super.onOptionsItemSelected(item);
    }


}
