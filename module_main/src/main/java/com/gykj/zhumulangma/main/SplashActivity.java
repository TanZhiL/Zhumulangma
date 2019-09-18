package com.gykj.zhumulangma.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.gykj.zhumulangma.common.mvvm.SupportActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description: <SplashActivity><br>
 * Author:      mxdl<br>
 * Date:        2019/6/22<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class SplashActivity extends SupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_splash);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
        },1000);
    }
}