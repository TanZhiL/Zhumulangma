package com.gykj.zhumulangma.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gykj.zhumulangma.common.App;
import com.gykj.zhumulangma.common.mvvm.SupportActivity;
import com.gykj.zhumulangma.common.util.log.TLog;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * Description: <SplashActivity><br>
 * Author:      mxdl<br>
 * Date:        2019/6/22<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class SplashActivity extends Activity implements CancelAdapt {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }
}