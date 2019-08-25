package com.gykj.zhumulangma.common.jpush;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.util.log.TLog;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Author: Thomas.
 * Date: 2019/8/5 15:31
 * Email: 1071931588@qq.com
 * Description:
 */
public class JPushReceiver extends JPushMessageReceiver {
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        TLog.d(customMessage);
    }

    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        TLog.d(b);
    }

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        TLog.d(s);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        super.onCommandResult(context, cmdMessage);
        TLog.d(cmdMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        TLog.d(notificationMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        TLog.d(notificationMessage);
        ARouter.getInstance().build(AppConstants.Router.Main.A_MAIN).navigation();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                BaseActivityEvent<String> event = new BaseActivityEvent(EventCode.MainCode.JPUSH);
//                event.setData(AppConstants.Router.Task.F_ACCEPT);
                EventBus.getDefault().post(event);
            }
        },500);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageDismiss(context, notificationMessage);
        TLog.d(notificationMessage);
    }
}
