package com.gykj.zhumulangma.home.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.RequestCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/8 9:18
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:扫一扫
 */
@Route(path = AppConstants.Router.Home.F_SCAN)
public class ScanFragment extends BaseFragment implements View.OnClickListener {

    private ZXingView scanView;
    private boolean isLight;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_scan;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        if (StatusBarUtils.supportTransparentStatusBar()) {
            fd(R.id.fl_title).setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        }
        scanView = fd(R.id.zxingview);
        scanView.setDelegate(delegate);
    }

    @Override
    public void initListener() {
        super.initListener();
        fd(R.id.iv_pop).setOnClickListener(this);
        fd(R.id.tv_pic).setOnClickListener(this);
        fd(R.id.tv_on).setOnClickListener(this);
        fd(R.id.iv_on).setOnClickListener(this);
        fd(R.id.tv_off).setOnClickListener(this);
        fd(R.id.iv_off).setOnClickListener(this);
    }

    @Override
    public void initData() {
        scanView.setVisibility(View.VISIBLE);
    }

    private QRCodeView.Delegate delegate = new QRCodeView.Delegate() {
        @Override
        public void onScanQRCodeSuccess(String result) {
            if(null==result){
                ToastUtil.showToast(ToastUtil.LEVEL_W,"未发现二维码/条码");
                scanView.startSpot();
                return;
            }
            if (RegexUtils.isURL(result)) {
                Object navigation = ARouter.getInstance().build(AppConstants.Router.Discover.F_WEB)
                        .withString(KeyCode.Discover.PATH, result)
                        .navigation();
                if(navigation!=null){
                    startWithPop((ISupportFragment) navigation);
                }
            } else {
                new AlertDialog.Builder(mActivity)
                        .setTitle("扫描结果")
                        .setMessage(result)
                        .setPositiveButton("确定", (dialog1, which) -> scanView.startSpot()).show();
            }
        }

        @Override
        public void onCameraAmbientBrightnessChanged(boolean isDark) {
            if (isDark) {
                fd(R.id.gp_on).setVisibility(View.VISIBLE);
                fd(R.id.gp_off).setVisibility(View.GONE);
            } else {
                fd(R.id.gp_off).setVisibility(isLight ? View.VISIBLE : View.GONE);
                fd(R.id.gp_on).setVisibility(View.GONE);
            }
        }

        @Override
        public void onScanQRCodeOpenCameraError() {
            ToastUtil.showToast(ToastUtil.LEVEL_E, "打开相机失败");
        }
    };

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        scanView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        scanView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    public void onStop() {
        super.onStop();
        if (scanView != null) {
            scanView.stopCamera();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHOW_GP));
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.HIDE_GP));
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scanView != null)
            scanView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_pic) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .maxSelectNum(1)
                    .compress(false)
                    .forResult(RequestCode.Common.PIKE_IMAGE);
        } else if (id == R.id.iv_pop) {
            pop();
        } else if (id == R.id.iv_on || id == R.id.tv_on) {
            scanView.openFlashlight();
            fd(R.id.gp_on).setVisibility(View.GONE);
            fd(R.id.gp_off).setVisibility(View.VISIBLE);
            isLight = true;
        } else if (id == R.id.iv_off || id == R.id.tv_off) {
            scanView.closeFlashlight();
            isLight = false;
            fd(R.id.gp_on).setVisibility(View.GONE);
            fd(R.id.gp_off).setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.Common.PIKE_IMAGE:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> mediaList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    scanView.decodeQRCode(mediaList.get(0).getPath());
                    break;
            }
        }
    }
}
