package com.gykj.zhumulangma.main;

import android.content.Intent;

import com.gykj.zhumulangma.common.mvvm.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description: <SplashActivity><br>
 * Author:      mxdl<br>
 * Date:        2019/6/22<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class SplashActivity extends BaseActivity {

    @Override
    public int onBindLayout() {
        return R.layout.main_activity_splash;
    }

    @Override
    public void initView() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
        },1000);
    }

    @Override
    public void initData() {
    }


    @Override
    public boolean enableSimplebar() {
        return false;
    }
}