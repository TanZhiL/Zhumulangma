package com.ximalaya.ting.android.opensdk.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.auth.call.IXmlyAuthListener;
import com.ximalaya.ting.android.opensdk.auth.constants.XmlyConstants;
import com.ximalaya.ting.android.opensdk.auth.exception.XmlyException;
import com.ximalaya.ting.android.opensdk.auth.handler.XmlySsoHandler;
import com.ximalaya.ting.android.opensdk.auth.model.XmlyAuth2AccessToken;
import com.ximalaya.ting.android.opensdk.auth.model.XmlyAuthInfo;
import com.ximalaya.ting.android.opensdk.auth.utils.AccessTokenKeeper;
import com.ximalaya.ting.android.opensdk.auth.utils.QrcodeLoginUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.datatrasfer.ILoginOutCallBack;
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 该类主要演示如何进行授权认证
 *
 * @author 喜马拉雅
 * @since 2016/8/28
 */
public class XMAuthDemoActivity extends Activity {

    private static final String TAG = XMAuthDemoActivity.class.getSimpleName();

    /**
     * 当前 DEMO 应用的回调页，第三方应用应该使用自己的回调页。
     */
//    public static final String REDIRECT_URL =  "http://api.ximalaya.com";
    public static final String REDIRECT_URL = DTransferConstants.isRelease ?
            "http://api.ximalaya.com/openapi-collector-app/get_access_token" :
            "http://studio.test.ximalaya.com/qunfeng-web/access_token/callback";

    /**
     * 显示认证后的 AccessToken
     */
    private TextView mTvAccessToken;

    /**
     * 显示认证后的 AccessToken 过期时间
     */
    private TextView mTvAccessTokenExpireTime;

    /**
     * 显示认证后的 AccessToken 有效或无效提示信息
     **/
    private TextView mTvAccessTokenMemo;

    /**
     * 喜马拉雅授权实体类对象
     */
    private XmlyAuthInfo mAuthInfo;

    /**
     * 喜马拉雅授权管理类对象
     */
    private XmlySsoHandler mSsoHandler;

    /**
     * 封装了 "access_token"，"refresh_token"，并提供了他们的管理功能
     */
    private XmlyAuth2AccessToken mAccessToken;

    private CountDownTimer countDownTimer;

    // 是否是第三方登录方式
    private boolean isThirdType = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_auth_demo);
        if (XmlyConstants.IS_RELEASE) {
            setTitle("授权DEMO-生产环境");
        } else {
            setTitle("授权DEMO-测试环境");
        }

        try {
            mAuthInfo = new XmlyAuthInfo(this, CommonRequest.getInstanse().getAppKey(), CommonRequest.getInstanse()
                    .getPackId(), REDIRECT_URL, DTransferConstants.isRelease ? CommonRequest.getInstanse().getAppKey() : "qunfeng");
        } catch (XimalayaException e) {
            e.printStackTrace();
        }
        mSsoHandler = new XmlySsoHandler(this, mAuthInfo);

        initView();
    }

    private void initView() {
        mTvAccessToken = (TextView) findViewById(R.id.tv_access_token);
        mTvAccessTokenExpireTime = (TextView) findViewById(R.id.tv_access_token_expire_time);
        mTvAccessTokenMemo = (TextView) findViewById(R.id.tv_access_token_memo);

        // 演示 Web 授权，仅作演示，第三方应用集成时不要采用该方式
        findViewById(R.id.btn_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorizeWeb(new CustomAuthListener());
            }
        });

        // 演示客户端授权，仅作演示，第三方应用集成时不要采用该方式
        findViewById(R.id.btn_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new CustomAuthListener());
            }
        });

        // 演示客户端 + Web 授权，如果手机安装了微博客户端则使用客户端授权，没有则进行网页授权，第三方应用集成时应采用该方式
        findViewById(R.id.btn_one_in_key).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new CustomAuthListener());
            }
        });

        // 第三方授权方式 具体请参考 "OAuth2.0–合作方使用第三方账号登录并授权的接口文档"
        findViewById(R.id.btn_third_author).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isThirdType = true;
                mSsoHandler.authorizeByThird("13000000001", "bfdc153c0a635d3d49c082afeb4e0bba" ,new CustomAuthListener());

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bundle bundle = mSsoHandler.authorizeByThirdSync("13000000001",
//                                "bfdc153c0a635d3d49c082afeb4e0bba");
//                        System.out.println("XMAuthDemoActivity.run   " + bundle);
//                        if(bundle != null) {
//                            parseAccessToken(bundle);
//                        }
//                    }
//                }).start();
            }
        });

        // 用户退出登录，清空喜马拉雅授权 SDK 中保存的 access token 信息
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessTokenManager.getInstanse().loginOut(new ILoginOutCallBack() {
                    @Override
                    public void onSuccess() {
                        if (mAccessToken != null && mAccessToken.isSessionValid()) {
                            AccessTokenKeeper.clear(XMAuthDemoActivity.this);
                            mAccessToken = new XmlyAuth2AccessToken();
                        } else {
                            Toast.makeText(XMAuthDemoActivity.this, getResources().getString(R.string.tip_has_not_login), Toast.LENGTH_SHORT).show();
                        }
                        mTvAccessTokenMemo.setText(getResources().getString(R.string.tip_access_token_is_invalid));
                        mTvAccessToken.setText("");
                        mTvAccessTokenExpireTime.setText("");

                        TingApplication.unregisterLoginTokenChangeListener();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMessage) {
                        TingApplication.unregisterLoginTokenChangeListener();
                    }
                });

            }
        });

        final XmlyAuth2AccessToken accessToken = AccessTokenKeeper.readAccessToken (this);
        if (accessToken.isSessionValid()) {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String date = sdf.format(new Date(accessToken.getExpiresAt()));
            // 显示 access token 信息
            mTvAccessTokenMemo.setText(getResources().getString(R.string.tip_access_token_is_valid));
            mTvAccessToken.setText(getResources().getString(R.string.tip_access_token) + accessToken.toString());
            mTvAccessTokenExpireTime.setText(getResources().getString(R.string.tip_access_token_expires_at) + (!TextUtils.isEmpty(date) ? date : ""));
        }

        findViewById(R.id.btn_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQrcodeAndCheck();
            }
        });

    }

    // 客户端认证授权回调
    // 非常重要：发起客户端认证授权的 Activity 必须重写 onActivityResults
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 喜马拉雅认证授权回调类。
     * 1. 客户端授权时，需要在 {@link #onActivityResult} 中调用 {@link XmlySsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * <p>
     * 注意：当认证授权成功时，喜马拉雅 OAuthSDK 中已经保存了 access_token uid 等信息，
     * 需要使用时调用 {@link AccessTokenKeeper#readAccessToken(Context)}。
     */
    class CustomAuthListener implements IXmlyAuthListener {

        // 当认证授权成功时，回调该方法
        @Override
        public void onComplete(Bundle bundle) {
            parseAccessToken(bundle);

            TingApplication.registerLoginTokenChangeListener(XMAuthDemoActivity.this.getApplicationContext());
        }

        // 当授权过程中发生异常（如回调地址无效等信息等）时，回调该方法
        @Override
        public void onXmlyException(final XmlyException e) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 请查看“喜马拉雅Android平台OAuth2SDK文档”，查看错误编号对应的详细错误信息
                    mTvAccessTokenMemo.setText(getResources().getString(R.string.tip_auth_failure) + e.getMessage());
                    mTvAccessToken.setText("");
                    mTvAccessTokenExpireTime.setText("");
                    Toast.makeText(XMAuthDemoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            isThirdType = false;
        }

        // 当用户主动取消授权时，回调该方法
        @Override
        public void onCancel() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(XMAuthDemoActivity.this, getResources().getString(R.string.tip_auth_cancel), Toast.LENGTH_SHORT).show();
                }
            });

            isThirdType = false;
        }
    }

    private void parseAccessToken(Bundle bundle) {
        // 从 Bundle 中解析 access token
        mAccessToken = XmlyAuth2AccessToken.parseAccessToken(bundle);
        if (mAccessToken.isSessionValid()) {
            /**
             * 关键!!!!!!!!!!
             * 结果返回之后将取回的结果设置到token管理器中
             */
            if(!isThirdType) {
                AccessTokenManager.getInstanse().setAccessTokenAndUid(mAccessToken.getToken(), mAccessToken
                        .getRefreshToken(), mAccessToken.getExpiresAt(), mAccessToken.getUid());
            } else {
                /**
                 * 如果是第三方登录方式需要使用如下方式
                 */
                AccessTokenManager.getInstanse().setAccessTokenAndUidByThirdType(mAccessToken.getToken(),
                        mAccessToken.getExpiresAt(), "13000000001", "bfdc153c0a635d3d49c082afeb4e0bba");
            }

            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String date = sdf.format(new Date(mAccessToken.getExpiresAt()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 显示 access token 信息
                    mTvAccessTokenMemo.setText(getResources().getString(R.string.tip_access_token_is_valid));
                    mTvAccessToken.setText(getResources().getString(R.string.tip_access_token) + mAccessToken.toString());
                    mTvAccessTokenExpireTime.setText(getResources().getString(R.string.tip_access_token_expires_at) + (!TextUtils.isEmpty(date) ? date : ""));
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 显示 access token 信息
                    mTvAccessTokenMemo.setText(getResources().getString(R.string.tip_access_token_is_invalid));
                    mTvAccessToken.setText("");
                    mTvAccessTokenExpireTime.setText("");
                }
            });
        }
        isThirdType = false;
    }

    private void createQrcodeAndCheck() {
        QrcodeLoginUtil.requestGernerateQRCodeForLogin(new HashMap<String, String>() {
            {
                put("size" ,"L");
            }
        }, new QrcodeLoginUtil.IGenerateCallBack() {
            @Override
            public void qrcodeImage(Bitmap bitmap , final String q) {
                ImageView qrcode = (ImageView) findViewById(R.id.img_qrcode);
                qrcode.setImageBitmap(bitmap);

                if(countDownTimer != null) {
                    countDownTimer.cancel();
                }
                countDownTimer = new CountDownTimer(Integer.MAX_VALUE ,3000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        QrcodeLoginUtil.checkQRCodeLoginStatus(new HashMap<String, String>() {
                            {
                                put("qrcode_id" , q);
                            }
                        }, new IDataCallBack<XmlyAuth2AccessToken>() {
                            @Override
                            public void onSuccess(XmlyAuth2AccessToken object) {
                                if(object!= null) {
                                    /**
                                     * 关键!!!!!!!!!!
                                     * 结果返回之后将取回的结果设置到token管理器中
                                     */
                                    AccessTokenManager.getInstanse().setAccessTokenAndUid(object.getToken() ,object.getRefreshToken() ,object.getExpiresAt(),object.getUid());

                                    if(countDownTimer != null){
                                        countDownTimer.cancel();
                                    }
                                    Toast.makeText(XMAuthDemoActivity.this, "登录成功了", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(int code, String message) {
                                if(code == 207) {
                                    // 此时，需要继续轮询本接口，查询用户登录状态；
                                    System.out.println("客户端还未登录");
                                } else  if(code == 217) {
                                    System.out.println("二维码过期了");
                                    // 此时，需要重新请求接口获取一个新的有效二维码图片，并进一步轮询本接口查询用户登录状态；
                                    if(countDownTimer != null) {
                                        countDownTimer.cancel();
                                    }
                                    createQrcodeAndCheck();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFinish() {

                    }
                };

                countDownTimer.start();

            }

            @Override
            public void onError(int code, String message) {
            }
        });
    }

}
