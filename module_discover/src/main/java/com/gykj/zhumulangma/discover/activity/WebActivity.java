package com.gykj.zhumulangma.discover.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseActivity;
import com.gykj.zhumulangma.discover.R;
import com.gykj.zhumulangma.discover.databinding.DiscoverActivityWebBinding;
import com.just.agentweb.AgentWeb;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/25 15:44
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:
 */
@Route(path = Constants.Router.Discover.F_WEB)
public class WebActivity extends BaseActivity<DiscoverActivityWebBinding> {
    private AgentWeb mAgentWeb;
    @Autowired(name = KeyCode.Discover.PATH)
    public String mPath;
    private ImageView ivClose;
    private AlertDialog mAlertDialog;
    private String mTitle;
    private Bitmap mIcon;
    @Override
    public int onBindLayout() {
        return R.layout.discover_activity_web;
    }
    
    @Override
    public void initView() {
        ivClose = mSimpleTitleBar.getLeftCustomView().findViewById(R.id.iv_left);
        ivClose.setImageResource(R.drawable.ic_common_web_close);
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(v -> showDialog());
    }

    @Override
    public void initData() {

        mAgentWeb =  AgentWeb.with(this)
                .setAgentWebParent(mBinding.flContainer, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(getResources().getColor(R.color.colorPrimary),1)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
                .go(mPath);
    }
    protected com.just.agentweb.WebChromeClient mWebChromeClient = new com.just.agentweb.WebChromeClient() {
        private HashMap<String, Long> timer = new HashMap<>();

        @Override
        public void onReceivedTitle(android.webkit.WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitle = title;
            setTitle(new String[]{title});
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            mIcon = icon;
        }
    };

    protected com.just.agentweb.WebViewClient mWebViewClient = new com.just.agentweb.WebViewClient() {
        private HashMap<String, Long> timer = new HashMap<>();

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //intent:// scheme的处理 如果返回false ， 则交给 DefaultWebClient 处理 ， 默认会打开该Activity  ， 如果Activity不存在则跳到应用市场上去.  true 表示拦截
            //例如优酷视频播放 ，intent://play?vid=XODEzMjU1MTI4&refer=&tuid=&ua=Mozilla%2F5.0%20(Linux%3B%20Android%207.0%3B%20SM-G9300%20Build%2FNRD90M%3B%20wv)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Version%2F4.0%20Chrome%2F58.0.3029.83%20Mobile%20Safari%2F537.36&source=exclusive-pageload&cookieid=14971464739049EJXvh|Z6i1re#Intent;scheme=youku;package=com.youku.phone;end;
            //优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ， 则跳到应用市场下载该应用 .
            String url = request.getUrl().toString();
            if (url.startsWith("intent://"))
                return true;
            else if (url.startsWith("youku"))
                return true;
//            else if(isAlipay(view,url))  //不需要，defaultWebClient内部会自动处理
//                return true;

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            timer.put(url, System.currentTimeMillis());
            if (url.equals(mPath)) {
                ivClose.setVisibility(View.GONE);
            } else {
                ivClose.setVisibility(View.VISIBLE);
            }
        }
    };

    private void showDialog() {

        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setMessage("您确定要关闭该页面吗?")
                    .setNegativeButton("再逛逛", (dialog, which) -> {
                        if (mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                       finish();
                    }).create();
        }
        mAlertDialog.show();

    }

    @Override
    public void onResume() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }

    @Override
    public void onSimpleBackClick() {
        if (!mAgentWeb.back()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            finish();
        }
    }


    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_share};
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        UMWeb web = new UMWeb(mPath);
        web.setTitle(mTitle);//标题
        web.setThumb(new UMImage(this, mIcon));  //缩略图
        web.setDescription("珠穆朗玛听");//描述
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHARE, new ShareAction(this).withMedia(web)));
    }
}
