package com.gykj.zhumulangma.common.util;

import com.blankj.utilcode.util.CollectionUtils;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/6 8:34
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:电台工具
 */
public class RadioUtil {
    /**
     * 填充节目单内容
     * @param schedulesx
     * @param radio
     */
    public static void fillData(List<Schedule> schedulesx, Radio radio) {
        if (!CollectionUtils.isEmpty(schedulesx)) {
            Iterator var = schedulesx.iterator();
            while (var.hasNext()) {
                Schedule schedulex = (Schedule) var.next();
                Program program = schedulex.getRelatedProgram();
                if (program == null) {
                    program = new Program();
                    schedulex.setRelatedProgram(program);
                }
                program.setBackPicUrl(radio.getCoverUrlLarge());
                schedulex.setRadioId(radio.getDataId());
                schedulex.setRadioName(radio.getRadioName());
                schedulex.setRadioPlayCount(radio.getRadioPlayCount());
                if (BaseUtil.isInTime(schedulex.getStartTime() + "-" + schedulex.getEndTime()) == 0) {
                    program.setRate24AacUrl(radio.getRate24AacUrl());
                    program.setRate24TsUrl(radio.getRate24TsUrl());
                    program.setRate64AacUrl(radio.getRate64AacUrl());
                    program.setRate64TsUrl(radio.getRate64TsUrl());
                    break;
                }
            }
        }
    }
}
