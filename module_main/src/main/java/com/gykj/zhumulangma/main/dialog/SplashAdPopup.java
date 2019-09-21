package com.gykj.zhumulangma.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.gykj.zhumulangma.main.R;
import com.lxj.xpopup.impl.FullScreenPopupView;

public class SplashAdPopup extends FullScreenPopupView {
    public SplashAdPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.main_dialog_splash_ad;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_ad).setOnClickListener(v -> dismiss());
    }
}
