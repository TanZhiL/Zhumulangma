package com.gykj.zhumulangma.discover.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.RequestCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseActivity;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.discover.R;
import com.gykj.zhumulangma.discover.databinding.DiscoverActivityScanBinding;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;

import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/8 9:18
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:扫一扫
 */
@Route(path = Constants.Router.Discover.F_SCAN)
public class ScanActivity extends BaseActivity<DiscoverActivityScanBinding> implements View.OnClickListener {

    private boolean isLight;

    @Override
    public int onBindLayout() {
        return R.layout.discover_activity_scan;
    }
    
    @Override
    public void initView() {
        if (StatusBarUtils.supportTransparentStatusBar()) {
            mBinding.flTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        }
        mBinding.zxingview.setDelegate(delegate);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.ivPop.setOnClickListener(this);
        mBinding.tvPic.setOnClickListener(this);
        mBinding.tvOn.setOnClickListener(this);
        mBinding.ivOn.setOnClickListener(this);
        mBinding.tvOff.setOnClickListener(this);
        mBinding.ivOff.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mBinding.zxingview.setVisibility(View.VISIBLE);
        mBinding.clContent.setBackground(null);
    }

    private QRCodeView.Delegate delegate = new QRCodeView.Delegate() {
        @Override
        public void onScanQRCodeSuccess(String result) {
            if (null == result) {
                ToastUtil.showToast(ToastUtil.LEVEL_W, "未发现二维码/条码");
                mBinding.zxingview.startSpot();
                return;
            }
            if (RegexUtils.isURL(result)) {
                finish();
                Postcard postcard = mRouter.build(Constants.Router.Discover.F_WEB)
                        .withString(KeyCode.Discover.PATH, result);
                RouteHelper.navigateTo(postcard);
            } else {
                new AlertDialog.Builder(ScanActivity.this)
                        .setTitle("扫描结果")
                        .setMessage(result)
                        .setPositiveButton("确定", (dialog1, which) -> mBinding.zxingview.startSpot()).show();
            }
        }

        @Override
        public void onCameraAmbientBrightnessChanged(boolean isDark) {
            if (isDark) {
                mBinding.gpOn.setVisibility(View.VISIBLE);
                mBinding.gpOff.setVisibility(View.GONE);
            } else {
                mBinding.gpOff.setVisibility(isLight ? View.VISIBLE : View.GONE);
                mBinding.gpOn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onScanQRCodeOpenCameraError() {
            ToastUtil.showToast(ToastUtil.LEVEL_E, "打开相机失败");
        }
    };

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBinding.zxingview.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBinding.zxingview != null) {
            mBinding.zxingview.stopCamera();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding.zxingview != null)
            mBinding.zxingview.onDestroy();
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
           finish();
        } else if (id == R.id.iv_on || id == R.id.tv_on) {
            mBinding.zxingview.openFlashlight();
            mBinding.gpOn.setVisibility(View.GONE);
            mBinding.gpOff.setVisibility(View.VISIBLE);
            isLight = true;
        } else if (id == R.id.iv_off || id == R.id.tv_off) {
            mBinding.zxingview.closeFlashlight();
            isLight = false;
            mBinding.gpOn.setVisibility(View.GONE);
            mBinding.gpOff.setVisibility(View.GONE);
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
                    mBinding.zxingview.decodeQRCode(mediaList.get(0).getPath());
                    break;
            }
        }
    }
}
