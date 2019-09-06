package com.gykj.zhumulangma.home.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.PlayTrackAdapter;
import com.lxj.xpopup.core.BottomPopupView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

/**
 * Author: Thomas.
 * Date: 2019/9/4 8:38
 * Email: 1071931588@qq.com
 * Description:
 */
public class PlayTrackPopup extends BottomPopupView implements View.OnClickListener,
        OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemChildClickListener {

    private PlayTrackAdapter mTrackAdapter;
    private Context mContext;
    private SmartRefreshLayout refreshLayout;
    private onActionListener mActionListener;
    private RecyclerView recyclerView;
    private ImageView ivShunxu;
    private TextView tvShunxu;

    public PlayTrackPopup(@NonNull Context context, @NonNull onActionListener listener) {
        super(context);
        mContext = context;
        mActionListener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.home_dialog_play_track;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_close).setOnClickListener(this);
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mTrackAdapter = new PlayTrackAdapter(R.layout.home_item_play_track);
        mTrackAdapter.bindToRecyclerView(recyclerView);
        mTrackAdapter.setOnItemClickListener(this);
        mTrackAdapter.setOnItemChildClickListener(this);
        refreshLayout.setOnRefreshLoadMoreListener(this);
        findViewById(R.id.ll_sort).setOnClickListener(this);
        findViewById(R.id.ll_shuxu).setOnClickListener(this);
        ivShunxu=findViewById(R.id.iv_shunxu);
        tvShunxu=findViewById(R.id.tv_shunxu);
        setShunxuStatus();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_close) {
            dismiss();
        } else if (R.id.ll_sort == id) {
            mActionListener.onSort();
        }else if (R.id.ll_shuxu == id) {
            XmPlayListControl.PlayMode playMode = XmPlayerManager.getInstance(mContext).getPlayMode();
            switch (playMode){
                     //列表播放
                case PLAY_MODEL_LIST :
                    XmPlayerManager.getInstance(mContext).setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                    break;
                    //列表循环
                case PLAY_MODEL_LIST_LOOP:
                    XmPlayerManager.getInstance(mContext).setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
                    break;
                    //随机播放
                case PLAY_MODEL_RANDOM :
                    XmPlayerManager.getInstance(mContext).setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
                    break;
                    //单曲循环
                case PLAY_MODEL_SINGLE_LOOP :
                    XmPlayerManager.getInstance(mContext).setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                    break;
            }
           setShunxuStatus();
        }
    }

    private void setShunxuStatus() {
        XmPlayListControl.PlayMode playMode = XmPlayerManager.getInstance(mContext).getPlayMode();
        switch (playMode){
            //列表播放
            case PLAY_MODEL_LIST :
                tvShunxu.setText("列表播放");
                ivShunxu.setImageResource(R.drawable.ic_home_play_shunxu);
                break;
            //列表循环
            case PLAY_MODEL_LIST_LOOP:
                tvShunxu.setText("列表循环");
                ivShunxu.setImageResource(R.drawable.ic_home_play_listloop);
                break;
            //随机播放
            case PLAY_MODEL_RANDOM :
                tvShunxu.setText("随机播放");
                ivShunxu.setImageResource(R.drawable.ic_home_play_random);
                break;
            //单曲循环
            case PLAY_MODEL_SINGLE_LOOP :
                tvShunxu.setText("单曲循环");
                ivShunxu.setImageResource(R.drawable.ic_home_play_singleloop);
                break;
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mActionListener.onLoadMore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mActionListener.onRefresh();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        mActionListener.onTrackItemClick(adapter, view, position);
    }

    public PlayTrackAdapter getTrackAdapter() {
        return mTrackAdapter;
    }

    public SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (R.id.iv_download == id) {
            XmDownloadManager.getInstance().downloadSingleTrack(
                    mTrackAdapter.getData().get(position).getDataId(), null);
        }
    }

    public interface onActionListener {
        void onRefresh();

        void onLoadMore();

        void onSort();

        void onTrackItemClick(BaseQuickAdapter adapter, View view, int position);
    }
}
