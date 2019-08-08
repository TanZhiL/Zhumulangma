package com.gykj.zhumulangma.common.util;

import java.text.DecimalFormat;

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
        second = second % 60;                //剩余秒数
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
}
