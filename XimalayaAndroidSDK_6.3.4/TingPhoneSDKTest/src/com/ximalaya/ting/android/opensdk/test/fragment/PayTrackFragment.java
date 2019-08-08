package com.ximalaya.ting.android.opensdk.test.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.test.data.ViewHolder;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.exception.AddDownloadException;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by le.xin on 2017/4/21.
 */

public class PayTrackFragment extends BaseFragment implements IXmPlayerStatusListener {
    private ListView mListView;
    private Context mContext;

    private TrackAdapter mTrackAdapter;
    private int mPageId = 1;
    private TrackList mTrackHotList = null;
    private boolean mLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_main, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        mTrackAdapter = new TrackAdapter();
        mListView.setAdapter(mTrackAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int count = view.getCount();
                    count = count - 5 > 0 ? count - 5 : count - 1;
                    if (view.getLastVisiblePosition() > count && (mTrackHotList == null || mPageId <= mTrackHotList.getTotalPage())) {
                        loadData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                XmPlayerManager.getInstance(mContext).playList(mTrackHotList, position);
//                final long dataId = mTrackHotList.getTracks().get(position).getDataId();
//                PayOrderManager.clientPlaceOrderTracks(5203901, new ArrayList<Long>() {
//                    {
//                        add(dataId);
//                    }
//                }, getContext(), new IXmPayOrderListener() {
//                    @Override
//                    public void onFinish(PayFinishModel payFinishModel) {
//                        if(payFinishModel.getCode() == CODE_PAY_H5_SUCCESS) {
//                            List<Long> list = new ArrayList<Long>();
//                            list.add(dataId);
//                            if (list == null || list.size() == 0) {
//                                return;
//                            }
//                            List<Track> tracks = XmPlayerManager.getInstance(mContext).getPlayList();
//                            if (tracks == null) {
//                                return;
//                            }
//                            for (int i = 0; i < tracks.size(); i++) {
//                                Track track = tracks.get(i);
//                                if (list.contains(track.getDataId())) {
//                                    // 这两句话是重点
//                                    track.setAuthorized(true);
//                                    XmPlayerManager.getInstance(mContext).updateTrackInPlayList(track);
//                                }
//                            }
//
//                            PlayableModel currSound = XmPlayerManager.getInstance(mContext).getCurrSound();
//                            if (currSound != null && list.contains(currSound.getDataId())
//                                    && (XmPlayerManager.getInstance(mContext).isPlaying()
//                                            || XmPlayerManager.getInstance(getContext()).getPlayerStatus() == PlayerConstants.STATE_IDLE)) {
//                                XmPlayerManager.getInstance(mContext).play(XmPlayerManager.getInstance(mContext).getCurrentIndex());
//                            }
//
//                        }
//                    }
//                });
            }
        });

        loadData();
    }

    private void loadData() {
        if (mLoading) {
            return;
        }
        mLoading = true;

        Map<String ,String> maps = new HashMap<>();
        maps.put("album_id" ,"6922889");
//        maps.put("album_id" ,"4345263");
        CommonRequest.getPaidTrackByAlbum(maps, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null && trackList.getTracks() != null && trackList.getTracks().size() != 0) {
                    mPageId++;

                    if(mTrackHotList == null) {
                        mTrackHotList = trackList;
                    } else {
                        trackList.getTracks().addAll(0 ,mTrackHotList.getTracks());
                        mTrackHotList = trackList;
                    }

                    mTrackAdapter.notifyDataSetChanged();
                }
                mLoading = false;
            }

            @Override
            public void onError(int code, String message) {
                mLoading = false;
            }
        });
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
                    XmPlayerManager.getInstance(getContext()).getPlayerStatus() == PlayerConstants.STATE_IDLE) {
                // 这里面写入试听结束后的代码,比如可以引导用户进行购买等操作
                new AlertDialog.Builder(getActivity()).setMessage("试听结束").setNeutralButton("确定", null).create().show();
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


    public class TrackAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mTrackHotList == null || mTrackHotList.getTracks() == null) {
                return 0;
            }
            return mTrackHotList.getTracks().size();
        }

        @Override
        public Object getItem(int position) {
            return mTrackHotList.getTracks().get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.track_content, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                holder.downloadStatue = (Button) convertView.findViewById(R.id.downloadstatue);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Track sound = mTrackHotList.getTracks().get(position);
            holder.title.setText(sound.getTrackTitle());
            String text = sound.getAnnouncer() == null ? sound.getTrackTags() : sound.getAnnouncer().getNickname();
            holder.intro.setText(text +"(" +((sound.isFree() || sound.isTrailer()) ? "整条试听" : (sound.isHasSample() && sound.isFree() ? "部分试听:" + sound.getSampleDuration() +"秒" : "付费"))+")");
            x.image().bind(holder.cover, sound.getCoverUrlLarge());
            PlayableModel curr = XmPlayerManager.getInstance(mContext).getCurrSound();
            if (sound.equals(curr)) {
                holder.content.setBackgroundResource(R.color.selected_bg);
            } else {
                holder.content.setBackgroundColor(Color.WHITE);
            }

            DownloadState downloadState = XmDownloadManager.getInstance().getSingleTrackDownloadStatus(sound.getDataId());
            holder.downloadStatue.setEnabled(true);
            holder.downloadStatue.setVisibility(View.VISIBLE);
            if(downloadState == DownloadState.FINISHED) {
                holder.downloadStatue.setText("已下载");
                holder.downloadStatue.setEnabled(false);
            } else if(downloadState.value() < DownloadState.FINISHED.value()) {
                holder.downloadStatue.setText("正在下载");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().pauseDownloadSingleTrack(sound.getDataId());
                    }
                });
            } else if(downloadState == DownloadState.STOPPED) {
                holder.downloadStatue.setText("已暂停");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().resumeDownloadSingleTrack(sound.getDataId());
                    }
                });
            } else if(downloadState == DownloadState.ERROR) {
                holder.downloadStatue.setText("下载失败");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().resumeDownloadSingleTrack(sound.getDataId());
                    }
                });
            } else if(downloadState == DownloadState.NOADD){
                holder.downloadStatue.setText("开始下载");
                holder.downloadStatue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmDownloadManager.getInstance().downloadPayTracks(new ArrayList<Long>() {
                            {
                                add(sound.getDataId());
                            }
                        }, true, new IDoSomethingProgress<AddDownloadException>() {
                            @Override
                            public void begin() {
                                Toast.makeText(getContext(), "开始", Toast.LENGTH_SHORT).show();
                                holder.downloadStatue.setEnabled(false);
                            }

                            @Override
                            public void success() {
                                Toast.makeText(getContext(), "加入下载成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void fail(AddDownloadException ex) {
                                Toast.makeText(getContext(), "加入下载失败" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            return convertView;
        }
    }
}
