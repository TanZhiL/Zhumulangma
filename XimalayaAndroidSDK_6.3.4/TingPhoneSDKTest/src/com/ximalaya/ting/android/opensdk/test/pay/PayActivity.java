package com.ximalaya.ting.android.opensdk.test.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.auth.constants.XmlyConstants;
import com.ximalaya.ting.android.opensdk.auth.utils.Logger;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.history.PlayHistoryList;
import com.ximalaya.ting.android.opensdk.model.track.BatchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.test.XMAuthDemoActivity;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.exception.AddDownloadException;
import com.ximalaya.ting.android.xmpayordersdk.IXmPayOrderListener;
import com.ximalaya.ting.android.xmpayordersdk.PayFinishModel;
import com.ximalaya.ting.android.xmpayordersdk.PayOrderManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ximalaya.ting.android.opensdk.test.XMAuthDemoActivity.REDIRECT_URL;

/**
 * Created by le.xin on 2017/5/10.
 */

public class PayActivity extends Activity implements IXmPlayerStatusListener {
    public static final String TAG = "PayActivity";
    public static final String REFRESH_TOKEN_URL = "https://api.ximalaya.com/oauth2/refresh_token?";
    private ListView mListView;

    private int page = 0;
    private AlbumList mAlbumList;
    private long albumId;

    private long mLastTrackId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_pay);

        // 使用此回调了就表示贵方接了需要用户登录才能访问的接口,如果没有此类接口可以不用设置此接口,之前的逻辑没有发生改变
        CommonRequest.getInstanse().setITokenStateChange(new CommonRequest.ITokenStateChange() {
            // 此接口表示token已经失效 ,
            @Override
            public boolean getTokenByRefreshSync() {
                System.out.println("PayActivity.getTokenByRefreshSync");
                if(!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        return refreshSync();
                    } catch (XimalayaException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean getTokenByRefreshAsync() {
                System.out.println("PayActivity.getTokenByRefreshAsync");
                if(!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        refresh();
                        return true;
                    } catch (XimalayaException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public void tokenLosted() {
                System.out.println("PayActivity.tokenLosted");
                Intent intent = new Intent(PayActivity.this ,XMAuthDemoActivity.class);
                startActivity(intent);
            }
        });

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(new ArrayAdapter<String>(this ,android.R.layout.simple_expandable_list_item_1 ,android.R.id.text1 ,new ArrayList<String>() {
            {
                add("0 专辑浏览");
                add("1 专辑内声音浏览");
                add("2 专辑内声音播放(付费未购买有试听)");
                add("3 专辑内声音购买");
                add("4 声音播放(付费已购买,购买形式单条声音购买)");
                add("5 专辑整张购买");
                add("6 声音播放(付费已购买,购买形式整张购买)");
                add("7 声音下载(付费已购买,未购买不能下载)");
                add("8 付费声音下载后播放");
                add("9 已购专辑浏览");
                add("10 根据订单进行下单");
                add("11 登录");
                add("12 已购专辑内声音浏览");
            }
        }));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 专辑浏览
                        Map<String ,String> maps = new HashMap<String, String>();
                        maps.put("page" ,++page + "");
                        maps.put("count" ,"200");
                        CommonRequest.getAllPaidAlbums(maps, new IDataCallBack<AlbumList>() {
                            @Override
                            public void onSuccess(AlbumList object) {
                                mAlbumList = object;
                                System.out.println("object = [" + object + "]");
                            }

                            @Override
                            public void onError(int code, String message) {
                                System.out.println("code = [" + code + "], message = [" + message + "]");
                            }
                        });
                        break;
                    case 1: // 专辑内声音浏览
                        if(mAlbumList == null) {
                            Toast.makeText(PayActivity.this, "先点击专辑浏览", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        maps = new HashMap<String, String>();
                         albumId = mAlbumList.getAlbums().get(new Random().nextInt(mAlbumList.getAlbums().size()))
                                .getId();
                        maps.put("album_id" , albumId +"");
                        CommonRequest.getPaidTrackByAlbum(maps, new IDataCallBack<TrackList>() {
                            @Override
                            public void onSuccess(TrackList object) {
                                System.out.println("object = [" + object + "]");
                            }

                            @Override
                            public void onError(int code, String message) {
                                System.out.println("code = [" + code + "], message = [" + message + "]");
                            }
                        });
                        break;
                    case 2: // 专辑内声音播放(付费未购买有试听)
                        maps = new HashMap<>();
//                        maps.put("album_id" ,"5203860");
                        maps.put("album_id" ,"13394295");
                        maps.put("page" ,"6");
                        CommonRequest.getPaidTrackByAlbum(maps, new IDataCallBack<TrackList>() {
                            @Override
                            public void onSuccess(TrackList trackList) {
                                Track track = trackList.getTracks().get(10);
                                mLastTrackId = track.getDataId();
                                XmPlayerManager.getInstance(PayActivity.this).playList(trackList ,10);
                            }

                            @Override
                            public void onError(int code, String message) {
                            }
                        });
                        break;
                    case 3: // 专辑内声音购买
                        PayOrderManager.clientPlaceOrderTracks(13394295, new ArrayList<Long>() {
                            {
                                add(mLastTrackId);
                            }
                        }, PayActivity.this, new IXmPayOrderListener() {
                            @Override
                            public void onFinish(PayFinishModel payFinishModel) {
                                Toast.makeText(PayActivity.this, "下单结果 -- " + payFinishModel, Toast.LENGTH_SHORT).show();
                                if(payFinishModel.getCode() == CODE_PAY_H5_SUCCESS) {
                                    PlayableModel currSound =
                                            XmPlayerManager.getInstance(PayActivity.this).getCurrSound();
                                    if(currSound.getDataId() == mLastTrackId) {
                                        XmPlayerManager.getInstance(PayActivity.this).resetPlayer();
                                        XmPlayerManager.getInstance(PayActivity.this).play(
                                                XmPlayerManager.getInstance(PayActivity.this).getCurrentIndex());
                                    }
                                }
                            }
                        });


                        break;
                    case 4: // 声音播放(付费已购买,购买形式单条声音购买)
                        Map map = new HashMap<String, String>();
                        map.put("ids" ,"74158861");
                        CommonRequest.batchPaidTracks(map, new IDataCallBack<BatchTrackList>() {
                            @Override
                            public void onSuccess(BatchTrackList object) {
                                System.out.println("object = [" + object + "]");
                                XmPlayerManager.getInstance(PayActivity.this).playList(object.getTracks() ,0);
                            }

                            @Override
                            public void onError(int code, String message) {
                                System.out.println("code = [" + code + "], message = [" + message + "]");
                            }
                        });
                        break;
                    case 5: // 专辑整张购买
                        PayOrderManager.clientPlaceOrderAlbum(418783, PayActivity.this, new IXmPayOrderListener() {
                            @Override
                            public void onFinish(PayFinishModel payFinishModel) {
                                Toast.makeText(PayActivity.this, "下单结果 -- " + payFinishModel, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 6: // 声音播放(付费已购买,购买形式整张购买)
                        maps = new HashMap<>();
                        maps.put("album_id" ,"15444721");
                        CommonRequest.getPaidTrackByAlbum(maps, new IDataCallBack<TrackList>() {
                            @Override
                            public void onSuccess(TrackList trackList) {
                                XmPlayerManager.getInstance(PayActivity.this).playList(trackList ,0);
                            }

                            @Override
                            public void onError(int code, String message) {
                                ;
                            }
                        });
                        break;
                    case 7: // 声音下载(付费已购买,未购买不能下载)
                        XmDownloadManager.getInstance().downloadPayTracks(new ArrayList<Long>() {
                            {
                                add(21077011L);
                            }
                        }, true, new IDoSomethingProgress<AddDownloadException>() {
                            @Override
                            public void begin() {
                                Toast.makeText(PayActivity.this, "begin", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void success() {
                                Toast.makeText(PayActivity.this, "success", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void fail(AddDownloadException ex) {
                                Toast.makeText(PayActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                System.out.println("ex = [" + ex + "]");
                            }
                        });
                        break;
                    case 8: // 付费声音下载后播放
                        List<Track> downloadTracks = XmDownloadManager.getInstance().getDownloadTracks(true);
                        System.out.println("PayActivity.onItemClick" + downloadTracks);
                        XmPlayerManager.getInstance(PayActivity.this).playList(downloadTracks ,0);
                        break;
                    case 9: // 已购专辑浏览
                        map = new HashMap<String, String>();
                        CommonRequest.getBoughtAlbums(map, new IDataCallBack<AlbumList>() {
                            @Override
                            public void onSuccess(AlbumList object) {
                                System.out.println("object = [" + object + "]");
                            }

                            @Override
                            public void onError(int code, String message) {
                                System.out.println("code = [" + code + "], message = [" + message + "]");
                            }
                        });
                        break;
                    case 10:
                        PayOrderManager.clientPlaceOrderByOrderNum("20170804001702020000000284487932", PayActivity.this, new IXmPayOrderListener() {
                            @Override
                            public void onFinish(PayFinishModel payFinishModel) {
                                System.out.println("下单的结果是 === " + payFinishModel);
                            }
                        });
                        break;
                    case 11:
                        startActivity(new Intent(PayActivity.this ,XMAuthDemoActivity.class));
                        break;
                    case 12 :
//                        Intent intent = new Intent(PayActivity.this, PayedAlbumActivity.class);
//                        intent.putExtra("albumId" ,4345263L);
//                        startActivity(intent);
                        Map<String ,String> maps1 = new HashMap<>();
                        CommonRequest.getPlayHistoryByUid(maps1, new IDataCallBack<PlayHistoryList>() {
                            @Override
                            public void onSuccess(@Nullable PlayHistoryList object) {

                            }

                            @Override
                            public void onError(int code, String message) {

                            }
                        });


                        break;

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        XmPlayerManager.getInstance(this).removePlayerStatusListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        XmPlayerManager.getInstance(this).addPlayerStatusListener(this);
    }

    @Override
    public void onPlayStart() {

    }

    @Override
    public void onPlayPause() {

    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        if (curModel == null && lastModel instanceof Track) {
            // isAudition 表示是否为部分试听声音
            if (((Track) lastModel).isAudition() &&
                    XmPlayerManager.getInstance(getApplicationContext()).getPlayerStatus() == PlayerConstants.STATE_IDLE) {
                // 这里面写入试听结束后的代码,比如可以引导用户进行购买等操作
                new AlertDialog.Builder(PayActivity.this).setMessage("试听结束").setNeutralButton("确定", null).create().show();
            }
        }
    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int percent) {

    }

    @Override
    public void onPlayProgress(int currPos, int duration) {

    }

    @Override
    public boolean onError(XmPlayerException exception) {
        return false;
    }

    public void refresh() throws XimalayaException {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .followRedirects(false)
                    .build();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token");
            builder.add(XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN, AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
            builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().getAppKey());
            builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().getDeviceId());
            builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID);
            builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().getPackId());
            builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().getUid());
            builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL);
            FormBody body = builder.build();

            Request request = new Request.Builder()
                    .url("https://api.ximalaya.com/oauth2/refresh_token?")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.d(TAG, "refreshToken, request failed, error message = " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int statusCode = response.code();
                    String body = response.body().string();
                    System.out.println("PayActivity.refresh 刷新token ===   " + body);

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(body);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(jsonObject != null) {
                        AccessTokenManager.getInstanse().setAccessTokenAndUid(jsonObject.optString("access_token"),
                                jsonObject.optString("refresh_token"), jsonObject.optLong("expires_in"), jsonObject
                                        .optString("uid"));
                    }
                }
            });
        }

    public boolean refreshSync() throws XimalayaException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .build();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(XmlyConstants.AUTH_PARAMS_GRANT_TYPE, "refresh_token");
        builder.add(XmlyConstants.AUTH_PARAMS_REFRESH_TOKEN, AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_ID, CommonRequest.getInstanse().getAppKey());
        builder.add(XmlyConstants.AUTH_PARAMS_DEVICE_ID, CommonRequest.getInstanse().getDeviceId());
        builder.add(XmlyConstants.AUTH_PARAMS_CLIENT_OS_TYPE, XmlyConstants.ClientOSType.ANDROID);
        builder.add(XmlyConstants.AUTH_PARAMS_PACKAGE_ID, CommonRequest.getInstanse().getPackId());
        builder.add(XmlyConstants.AUTH_PARAMS_UID, AccessTokenManager.getInstanse().getUid());
        builder.add(XmlyConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URL);
        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url(REFRESH_TOKEN_URL)
                .post(body)
                .build();
        try {
            Response execute = client.newCall(request).execute();
            if(execute.isSuccessful()) {
                try {
                    String string = execute.body().string();
                    JSONObject jsonObject = new JSONObject(string);

                    AccessTokenManager.getInstanse().setAccessTokenAndUid(jsonObject.optString("access_token"),
                            jsonObject.optString("refresh_token"), jsonObject.optLong("expires_in"), jsonObject
                                    .optString("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonRequest.getInstanse().setITokenStateChange(null);
    }
}
