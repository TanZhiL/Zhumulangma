package com.gykj.zhumulangma.main.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.main.R;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.impl.FullScreenPopupView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SplashAdPopup extends FullScreenPopupView implements View.OnClickListener {
    private Context mContext;

    public SplashAdPopup(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.main_dialog_splash_ad;
    }

    int second = 5;

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_time).setOnClickListener(v -> dismiss());
        ImageView ivAd = findViewById(R.id.iv_ad);
        File adFile = new File(mContext.getFilesDir().getAbsolutePath() + AppConstants.Default.AD_NAME);
        if (adFile.exists()) {
            ivAd.setImageURI(Uri.fromFile(adFile));
            //缩进处理
            SpannableString string = new SpannableString("缩进" + SPUtils.getInstance().getString(AppConstants.SP.AD_LABEL));
            string.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ((TextView) findViewById(R.id.tv_label)).setText(string);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                second--;
                post(() -> ((TextView) findViewById(R.id.tv_time)).setText("跳过广告 " + second));
                if (second == 0) {
                    post(() -> dismiss());
                }
            }, 1, 1, TimeUnit.SECONDS);
            ivAd.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        String path = SPUtils.getInstance().getString(AppConstants.SP.AD_URL);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    public static class AlphaAnimator extends PopupAnimator {
        @Override
        public void initAnimator() {
            targetView.setAlpha(1);
        }

        @Override
        public void animateShow() {
        }

        @Override
        public void animateDismiss() {
            targetView.animate().alpha(0).scaleX(1.5f).scaleY(1.5f).
                    setInterpolator(new LinearInterpolator()).setDuration(300).start();
        }
    }
}
