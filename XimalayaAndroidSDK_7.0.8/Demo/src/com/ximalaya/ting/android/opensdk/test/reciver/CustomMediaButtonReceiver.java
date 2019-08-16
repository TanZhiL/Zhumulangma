package com.ximalaya.ting.android.opensdk.test.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by le.xin on 2018/9/25.
 *
 * @author le.xin
 * @email le.xin@ximalaya.com
 * @phoneNumber 17097256298
 */
public class CustomMediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("CustomMediaButtonReceiver.onReceive  " + intent);
    }
}
