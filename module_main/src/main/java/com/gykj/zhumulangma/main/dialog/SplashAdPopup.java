package com.gykj.zhumulangma.main.dialog;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.main.R;
import com.lxj.xpopup.impl.FullScreenPopupView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SplashAdPopup extends FullScreenPopupView {
    private Context mContext;
    public SplashAdPopup(@NonNull Context context) {
        super(context);
        mContext=context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.main_dialog_splash_ad;
    }
    int second=5;
    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_time).setOnClickListener(v -> dismiss());
        ImageView ivAd = findViewById(R.id.iv_ad);
        File adFile=new File(mContext.getFilesDir().getAbsolutePath()+ AppConstants.Defualt.AD_NAME);
        if(adFile.exists()){
          ivAd.setImageURI(Uri.fromFile(adFile));
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                second--;
                post(()->((TextView)findViewById(R.id.tv_time)).setText("跳过广告 "+second));
                if(second==0){
                   post(()-> dismiss());
                }
            },1,1, TimeUnit.SECONDS);
        }
    }
}
