package com.gykj.zhumulangma.main.dialog;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.BarUtils;
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

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            FrameLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.topMargin = BarUtils.getStatusBarHeight();
            setLayoutParams(layoutParams);
        }
    }
}
