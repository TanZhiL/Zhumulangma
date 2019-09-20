package com.gykj.zhumulangma.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.gykj.zhumulangma.common.mvvm.SupportActivity;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * Description: <SplashActivity><br>
 * Author:      mxdl<br>
 * Date:        2019/6/22<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class SplashActivity extends SupportActivity implements CancelAdapt {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_splash);
       startActivity(new Intent(SplashActivity.this,MainActivity.class));
    }
}