/**
 * ScheduleFragment.java
 * com.ximalaya.ting.android.opensdk.test
 * <p/>
 * <p/>
 * ver     date      		author
 * ---------------------------------------
 * 2015-6-4 		chadwii
 * <p/>
 * Copyright (c) 2015, chadwii All Rights Reserved.
 */

package com.ximalaya.ting.android.opensdk.test.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.test.android.R;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.test.data.ViewHolder;
import com.ximalaya.ting.android.opensdk.test.fragment.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.test.util.ToolUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:ScheduleFragment
 *
 * @author chadwii
 * @Date 2015-6-4 上午10:41:12
 * @see
 * @since Ver 1.1
 */
public class ScheduleFragment extends BaseFragment {
    private static final String TAG = "ScheduleFragment";
    private Context mContext;
    private ListView mListView;

    private XmPlayerManager mPlayerManager;

    private ScheduleAdapter mAdapter;
    private ScheduleList mScheduleList;

    private boolean mLoading = false;

    private Radio mRadio;

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        mPlayerManager = XmPlayerManager.getInstance(mContext);

        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);

        mAdapter = new ScheduleAdapter();
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mScheduleList != null && mScheduleList.getmScheduleList() != null) {
                    Schedule sc = mScheduleList.getmScheduleList().get(position);
                    String time = sc.getStartTime() + "-" + sc.getEndTime();
                    if (ToolUtil.isInTime(time) < 0) {
                        mPlayerManager.playSchedule(mScheduleList.getmScheduleList(), position);
                    }
                }
            }
        });

        loadData();
    }

    private void loadData() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        PlayableModel model = mPlayerManager.getCurrSound();
        if (model == null || !(model instanceof Radio)) {
            Log.e(TAG, "loadData return class cast exception");
            mLoading = false;
            return;
        }
        Log.e(TAG, "loadData");
        mRadio = (Radio) model;
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.RADIOID, "" + mRadio.getDataId());
        CommonRequest.getSchedules(param, new IDataCallBack<ScheduleList>() {

            @Override
            public void onSuccess(ScheduleList object) {
                if (object != null) {
                    if (mScheduleList == null) {
                        mScheduleList = object;
                    } else {
                        mScheduleList.getmScheduleList().clear();
                        mScheduleList.getmScheduleList().addAll(object.getmScheduleList());
                    }
                    mAdapter.notifyDataSetChanged();
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
    public void refresh() {
        loadData();
    }

    @Override
    public void onDestroyView() {
        if (mPlayerManager != null) {
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        }
        super.onDestroyView();
    }

    private class ScheduleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mScheduleList == null || mScheduleList.getmScheduleList() == null) {
                return 0;
            }
            return mScheduleList.getmScheduleList().size();
        }

        @Override
        public Object getItem(int position) {
            return mScheduleList.getmScheduleList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_schedule, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Schedule schedule = mScheduleList.getmScheduleList().get(position);
            holder.title.setText(ToolUtil.isEmpty(schedule.getRelatedProgram().getProgramName()) ? "无节目" : schedule.getRelatedProgram().getProgramName());
            String time = schedule.getStartTime() + "-" + schedule.getEndTime();
            holder.intro.setText(time);

            int ret = ToolUtil.isInTime(time);
            if (ret > 0) {
                holder.content.setBackgroundColor(Color.WHITE);
                holder.status.setText("WAIT");
                holder.status.setBackgroundColor(Color.TRANSPARENT);
            } else if (ret == 0) {
                holder.content.setBackgroundResource(R.color.selected_bg);
                holder.status.setText("LIVE");
                holder.status.setBackgroundColor(Color.RED);
            } else {
                holder.content.setBackgroundColor(Color.WHITE);
                holder.status.setText("");
            }
            return convertView;
        }

    }
}
