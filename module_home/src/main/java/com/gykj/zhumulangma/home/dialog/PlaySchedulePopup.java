package com.gykj.zhumulangma.home.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.home.R;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.Arrays;
import java.util.List;

import retrofit2.http.POST;

/**
 * Author: Thomas.
 * Date: 2019/9/2 11:48
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlaySchedulePopup extends BottomPopupView implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener, OnOptionsSelectListener {
    private static final String TAG = "PlaySchedulePopup";
    private PlayScheduleAdapter mScheduleAdapter;
    private Context mContext;
    private onSelectedListener mListener;
    private String[] mSchedules = {"不开启", "播完当前声音", "10分钟", "20分钟", "30分钟", "60分钟", "90分钟", "自定义"};
    private String[] mHours = {"0小时", "1小时", "2小时", "3小时", "4小时", "5小时", "6小时", "7小时",
            "8小时", "9小时", "10小时", "11小时", "12小时", "13小时", "14小时", "15小时",
            "16小时", "17小时", "18小时", "19小时", "20小时", "21小时", "22小时", "23小时"
    };
    private String[] mMinutes = {"0分钟","5分钟", "10分钟", "15分钟", "20分钟", "25分钟", "30分钟", "35分钟", "40分钟", "45分钟", "55分钟"};

    private int[] mTimes = {0, 0, 10, 20, 30, 60, 90, 0};
    OptionsPickerView<String> pickerView;
    public PlaySchedulePopup(@NonNull Context context,@NonNull onSelectedListener listener) {
        super(context);
        mContext = context;
        mListener=listener;

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_layout_play_schedule;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        RecyclerView recyclerView = findViewById(R.id.rv_schedule);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mScheduleAdapter = new PlayScheduleAdapter(R.layout.home_item_play_schedule);
        mScheduleAdapter.bindToRecyclerView(recyclerView);
        mScheduleAdapter.setNewData(Arrays.asList(mSchedules));
        findViewById(R.id.tv_close).setOnClickListener(this);
        mScheduleAdapter.setOnItemClickListener(this);

    }

    // 最大高度为Window的0.85
    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext()) * .85f);
    }

    @Override
    protected void onShow() {
        mScheduleAdapter.notifyDataSetChanged();
        super.onShow();
    }

    public PlayScheduleAdapter getScheduleAdapter() {
        return mScheduleAdapter;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        RadioButton radioButton = (RadioButton) adapter.getViewByPosition(position, R.id.rb_indicator);
        radioButton.setChecked(true);
        if (position != 7) {
            SPUtils.getInstance().put(AppConstants.SP.PLAY_SCHEDULE_TYPE, position);
            SPUtils.getInstance().put(AppConstants.SP.PLAY_SCHEDULE_TIME, System.currentTimeMillis() + mTimes[position] * 60 * 1000);
        }
        if (position != 0 && position != 1 && position != 7) {
            mListener.onSelected(position,SPUtils.getInstance().getLong(AppConstants.SP.PLAY_SCHEDULE_TIME, 0));
            XmPlayerManager.getInstance(mContext).pausePlayInMillis(SPUtils.getInstance().getLong(AppConstants.SP.PLAY_SCHEDULE_TIME, 0));
        } else if (position == 0) {
            mListener.onSelected(position,-1);
            XmPlayerManager.getInstance(mContext).pausePlayInMillis(0);
        } else if (position == 1) {
            mListener.onSelected(position,0);
            XmPlayerManager.getInstance(mContext).pausePlayInMillis(-1);
        } else {
            pickerView = new OptionsPickerBuilder(mContext, this)
                    .setCancelColor(0x8A000000)
                    .setSubmitColor(mContext.getResources().getColor(R.color.colorPrimary))
                    .setCyclic(true,true,true)
                    .setLineSpacingMultiplier(2.6f)
                    .setSelectOptions(0,1)
                    .setDecorView(((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content))
                    .build();
            pickerView.setNPicker(Arrays.asList(mHours), Arrays.asList(mMinutes), null);
            pickerView.show();
        }
        dismiss();

    }


    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        SPUtils.getInstance().put(AppConstants.SP.PLAY_SCHEDULE_TYPE, 7);
        SPUtils.getInstance().put(AppConstants.SP.PLAY_SCHEDULE_TIME, System.currentTimeMillis()
        +options1*1000*60*60+options2*1000*5*60);
        mListener.onSelected(7,SPUtils.getInstance().getLong(AppConstants.SP.PLAY_SCHEDULE_TIME, 0));
        XmPlayerManager.getInstance(mContext).pausePlayInMillis(SPUtils.getInstance().getLong(AppConstants.SP.PLAY_SCHEDULE_TIME, 0));
    }

    public OptionsPickerView<String> getPickerView() {
        return pickerView;
    }

    public static class PlayScheduleAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public PlayScheduleAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_label, item);
            int type = SPUtils.getInstance().getInt(AppConstants.SP.PLAY_SCHEDULE_TYPE, 0);
            long time = SPUtils.getInstance().getLong(AppConstants.SP.PLAY_SCHEDULE_TIME, 0);
            if(helper.getLayoutPosition()==0){
                if(type==0){
                    helper.setChecked(R.id.rb_indicator, true);
                }
                else if(type==1){
                    helper.setChecked(R.id.rb_indicator, false);
                }
                else if(System.currentTimeMillis() >= time){
                    helper.setChecked(R.id.rb_indicator, true);
                }else {
                    helper.setChecked(R.id.rb_indicator, false);
                }
            }else {
                if (helper.getLayoutPosition() == type) {
                    if (type == 1) {
                        helper.setChecked(R.id.rb_indicator, true);
                    } else {
                        if (System.currentTimeMillis() < time) {
                            helper.setChecked(R.id.rb_indicator, true);
                        } else {
                            helper.setChecked(R.id.rb_indicator, false);
                        }
                    }
                } else {
                    helper.setChecked(R.id.rb_indicator, false);
                }
            }

        }
    }
    public interface onSelectedListener{
        void onSelected(int type,long time);
    }
}
