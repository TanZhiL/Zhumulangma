package com.ximalaya.ting.android.opensdk.test.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.test.data.ViewHolder;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;

import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by le.xin on 2016/11/29.
 */

public class AlbumListFragment extends BaseFragment {
    private static final String TAG = "AlbumListFragment";
    private Context mContext;
    private ListView mListView;
    private TrackAdapter mTrackAdapter;

    private int mPageId = 1;
    private TrackList mTrackHotList = null;
    private boolean mLoading = false;

    private CommonRequest mXimalaya;
    private XmPlayerManager mPlayerManager;

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            if (mTrackAdapter != null) {
                mTrackAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onSoundPrepared() {
        }

        @Override
        public void onSoundPlayComplete() {
        }

        @Override
        public void onPlayStop() {
        }

        @Override
        public void onPlayStart() {
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
        }

        @Override
        public void onPlayPause() {
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            return false;

        }

        @Override
        public void onBufferingStop() {
        }

        @Override
        public void onBufferingStart() {
        }

        @Override
        public void onBufferProgress(int percent) {
        }

    };

    public void refresh() {
        Log.e(TAG, "---refresh");
        if (hasMore()) {
            loadData();
        }
    }

    private boolean hasMore() {
        if (mTrackHotList != null && mTrackHotList.getTotalPage() <= mPageId) {
            return false;
        }
        return true;
    }

    private void loadData() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.PAGE, "" + mPageId);
        param.put(DTransferConstants.PAGE_SIZE, "" + 100);
        param.put(DTransferConstants.ALBUM_ID, 239463+"");
        CommonRequest.getTracks(param, new IDataCallBack<TrackList>() {
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
            public void onError(int i, String s) {
                mLoading = false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_main, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        mXimalaya = CommonRequest.getInstanse();
        mPlayerManager = XmPlayerManager.getInstance(mContext);

        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);

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
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                mPlayerManager.playList(mTrackHotList, position);
            }
        });

        loadData();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        if (mPlayerManager != null) {
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        }
        super.onDestroyView();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.track_content, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Track sound = mTrackHotList.getTracks().get(position);
            holder.title.setText(sound.getTrackTitle());
            holder.intro.setText(sound.getAnnouncer() == null ? sound.getTrackTags() : sound.getAnnouncer().getNickname());
            x.image().bind(holder.cover, sound.getCoverUrlLarge());
            PlayableModel curr = mPlayerManager.getCurrSound();
            if (sound.equals(curr)) {
                holder.content.setBackgroundResource(R.color.selected_bg);
            } else {
                holder.content.setBackgroundColor(Color.WHITE);
            }
            return convertView;
        }
    }
}
