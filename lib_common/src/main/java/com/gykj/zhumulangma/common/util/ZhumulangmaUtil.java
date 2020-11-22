package com.gykj.zhumulangma.common.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.blankj.utilcode.constant.MemoryConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.httputil.util.SignatureUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by 10719
 * on 2019/6/10
 */
public class ZhumulangmaUtil {
    public  static String toWanYi(long l){
        if(l<10000){
          return String.valueOf(l);
        }else if (l<100000000){
            DecimalFormat df = new DecimalFormat("#.00");
            double n = (double)l/10000;
            return String.valueOf(df.format(n)+"万");
        }else {
            DecimalFormat df = new DecimalFormat("#.00");
            double n = (double)l/100000000;
            return String.valueOf(df.format(n)+"亿");
        }
    }

    public static String toFenShiTian(long min){
        if(min<60)
            return min+"分钟";
        else if (min<60*24)
            return (int)min/60+"小时";
        else if(min<60*24*30)
            return (int)min/60/24+"天";
        else
            return "数月";
    }

    public static String secondToTime(long second){
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;
        //剩余秒数
        if(days>0){
            return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        }else if(hours>0){
            return hours + "小时" + minutes + "分" + second + "秒";
        } else if(minutes>0){
            return minutes + "分" + second + "秒";
        }else{
            return  second + "秒";
        }
    }
    public static String secondToTimeE(long second){
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;
        //剩余秒数
        if(days>0){
            return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        }else if(hours>0){
            return (hours<10?"0"+hours:hours)  + ":" + (minutes<10?"0"+minutes:minutes) + ":" + (second<10?"0"+second:second) + "";
        } else if(minutes>0){
            return (minutes<10?"0"+minutes:minutes) + ":" + (second<10?"0"+second:second) + "";
        }else{
            return    "00:" +  (second<10?"0"+second:second);
        }
    }
    public static String byte2FitMemorySize(final long byteSize) {
        if (byteSize < 0) {
            return "shouldn't be less than zero!";
        } else if (byteSize < MemoryConstants.KB) {
            return String.format("%.2fB", (double) byteSize);
        } else if (byteSize < MemoryConstants.MB) {
            return String.format("%.2fKB", (double) byteSize / MemoryConstants.KB);
        } else if (byteSize < MemoryConstants.GB) {
            return String.format("%.2fMB", (double) byteSize / MemoryConstants.MB);
        } else {
            return String.format("%.2fGB", (double) byteSize / MemoryConstants.GB);
        }
    }
    /**
     * 滚动显示
     * @param cur
     * @param start
     * @param end
     * @return
     */
    public  static float visibleByScroll(float cur,float start,float end){
        return (cur-start) / (end-start);
    }

    /**
     * 滚动消失
     * @param cur
     * @param start
     * @param end
     * @return
     */
    public  static float unvisibleByScroll(float cur,float start,float end){
        if(cur<start)
            return 1;
        else if (cur>end)
            return 0;
        else
        return  (end-cur)/end;
    }

    /**
     * 封装公共参数
     * @param specificParams
     * @return
     */
    public static Observable<Map<String,String>> commonParams(Map<String, String> specificParams){
        return Observable.fromCallable(() -> {
            Map<String, String> stringStringMap = CommonRequest.CommonParams(specificParams);
            String appsecret = CommonRequest.getInstanse().getAppsecret();
            String s = SignatureUtil.generateSignature(appsecret, stringStringMap);
            stringStringMap.put("sig",s);
            return stringStringMap;
        });
    }

    /**
     * 过滤付费声音
     * @param liveData
     * @return
     */
    public static LiveData<List<Track>> filterPaidTrack(LiveData<List<Track>> liveData){
        return Transformations.map(liveData, input -> {
            Iterator<Track> iterator = input.iterator();
            while (iterator.hasNext()){
                Track next = iterator.next();
                if(next.isPaid()){
                    iterator.remove();
                }
            }
            return input;
        });
    }

    /**
     * 过滤付费专辑
     * @param liveData
     * @return
     */
    public static LiveData<List<Album>> filterPaidAlbum(LiveData<List<Album>> liveData){
        return Transformations.map(liveData, input -> {
            Iterator<Album> iterator = input.iterator();
            while (iterator.hasNext()){
                Album next = iterator.next();
                if(next.isPaid()){
                    iterator.remove();
                }
            }
            return input;
        });
    }
}
