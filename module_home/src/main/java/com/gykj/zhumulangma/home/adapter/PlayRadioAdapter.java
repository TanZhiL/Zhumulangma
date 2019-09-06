package com.gykj.zhumulangma.home.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.BarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.zhumulangma.home.R;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Author: Thomas.
 * Date: 2019/9/6 16:58
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlayRadioAdapter extends BaseQuickAdapter<Schedule, BaseViewHolder> {
    private SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:HH:mm", Locale.getDefault());
    public PlayRadioAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Schedule item) {
        Program relatedProgram = item.getRelatedProgram();
        helper.setText(R.id.tv_title,relatedProgram.getProgramName());
        helper.setText(R.id.tv_time,item.getStartTime().substring(item.getStartTime().length() - 5) + "-"
                + item.getEndTime().substring(item.getEndTime().length() - 5));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < relatedProgram.getAnnouncerList().size(); i++) {
            sb.append(relatedProgram.getAnnouncerList().get(i).getNickName());
            if (i != relatedProgram.getAnnouncerList().size() - 1) {
                sb.append(",");
            }
        }
        helper.setText(R.id.tv_announcer_name,(TextUtils.isEmpty(sb.toString()) ? item.getRadioName() : sb.toString()));
        helper.setGone(R.id.tv_announcer_name,!TextUtils.isEmpty(sb.toString())||!TextUtils.isEmpty(item.getRadioName()));

        try {
            long start = sdf.parse(item.getStartTime()).getTime();
            helper.setGone(R.id.tv_zhibo,
                    BaseUtil.isInTime(item.getStartTime() + "-" + item.getEndTime()) == 0);
            helper.setGone(R.id.tv_huiting,
                    BaseUtil.isInTime(item.getStartTime() + "-" + item.getEndTime()) != 0);

            if(start > System.currentTimeMillis()){
                helper.setGone(R.id.tv_zhibo,false);
                helper.setGone(R.id.tv_huiting,false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(null!= XmPlayerManager.getInstance(mContext).getCurrSound()){
            LottieAnimationView lavPlaying=helper.getView(R.id.lav_playing);
            Schedule schedule = (Schedule) XmPlayerManager.getInstance(mContext).getCurrSound();
            if(schedule.getDataId()==item.getDataId()){
                lavPlaying.setVisibility(View.VISIBLE);
                helper.setTextColor(R.id.tv_title,mContext.getResources().getColor(R.color.colorPrimary));
                if(XmPlayerManager.getInstance(mContext).isPlaying()){
                    lavPlaying.playAnimation();
                }else {
                    lavPlaying.pauseAnimation();
                }
            }else {
                helper.setTextColor(R.id.tv_title,mContext.getResources().getColor(R.color.textColorPrimary));
                lavPlaying.cancelAnimation();
                lavPlaying.setVisibility(View.GONE);
            }

        }
//        helper.setVisible(R.id.v_line,helper.getLayoutPosition()!=getItemCount()-1);
    }
}
