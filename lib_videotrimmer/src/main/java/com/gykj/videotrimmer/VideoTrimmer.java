package com.gykj.videotrimmer;

import android.content.Context;
import android.util.Log;

import iknow.android.utils.BaseUtils;
import nl.bravobit.ffmpeg.FFmpeg;

/**
 * Author: Thomas.
 * Date: 2019/8/2 9:57
 * Email: 1071931588@qq.com
 * Description:
 */
public class VideoTrimmer {
    public static void init(Context context)
    {
        BaseUtils.init(context);
        if (!FFmpeg.getInstance(context).isSupported()) {
            Log.e("VideoTrimmer","Android cup arch not supported!");
        }
    }
}
