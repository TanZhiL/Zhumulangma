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

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.db.DBManager;
import com.gykj.zhumulangma.common.net.RxAdapter;
import com.gykj.zhumulangma.main.R;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.impl.FullScreenPopupView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SplashAdPopup extends FullScreenPopupView implements View.OnClickListener {
    private Context mContext;
    private int mAdSecond = 5;

    public SplashAdPopup(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.main_dialog_splash_ad;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_time).setOnClickListener(v -> dismiss());
        ImageView ivAd = findViewById(R.id.iv_ad);
        File adFile = new File(mContext.getFilesDir().getAbsolutePath() + Constants.Default.AD_NAME);
        if (adFile.exists()) {
            ivAd.setImageURI(Uri.fromFile(adFile));
            DBManager.getInstance().getSPString(Constants.SP.AD_LABEL)
                    .compose(RxAdapter.exceptionTransformer())
                    .compose(RxAdapter.schedulersTransformer())
                    .doOnSubscribe((Consumer<Disposable>) mContext)
                    .subscribe(s -> {
                        //缩进处理
                        SpannableString string = new SpannableString("缩进" + s);
                        string.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        ((TextView) findViewById(R.id.tv_label)).setText(string);
                    }, Throwable::printStackTrace);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                mAdSecond--;
                post(() -> ((TextView) findViewById(R.id.tv_time)).setText("跳过广告 " + mAdSecond));
                if (mAdSecond == 0) {
                    post(this::dismiss);
                }
            }, 1, 1, TimeUnit.SECONDS);
            ivAd.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        DBManager.getInstance().getSPString(Constants.SP.AD_URL)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer())
                .doOnSubscribe((Consumer<Disposable>) mContext)
                .subscribe(path -> {
                    Uri uri = Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
                }, Throwable::printStackTrace);
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
