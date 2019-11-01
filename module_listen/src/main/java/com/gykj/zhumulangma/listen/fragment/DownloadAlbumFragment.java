package com.gykj.zhumulangma.listen.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.util.RouteUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.listen.databinding.ListenFragmentDownloadAlbumBinding;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.ComparatorUtil;
import com.ximalaya.ting.android.sdkdownloader.model.XmDownloadAlbum;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/11 10:19
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:下载专辑详情
 */
@Route(path = Constants.Router.Listen.F_DOWNLOAD_ALBUM)
public class DownloadAlbumFragment extends BaseFragment<ListenFragmentDownloadAlbumBinding> implements View.OnClickListener,
        BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    @Autowired(name = KeyCode.Listen.ALBUMID)
    public long mAlbumId;
    private XmDownloadAlbum mAlbum;
    private XmDownloadManager mDownloadManager = XmDownloadManager.getInstance();
    private XmPlayerManager mPlayerManager=XmPlayerManager.getInstance(mActivity);
    private DownloadTrackAdapter mTrackAdapter;

    @Override
    protected int onBindLayout() {
        return R.layout.listen_fragment_download_album;
    }

    @Override
    protected void initView(View view) {
        showInitView();
        mBinding.recyclerview.setHasFixedSize(true);
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mTrackAdapter = new DownloadTrackAdapter(R.layout.listen_item_download_track);
        mTrackAdapter.bindToRecyclerView(mBinding.recyclerview);
        mTrackAdapter.setEmptyView(R.layout.common_layout_empty);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.tvMore.setOnClickListener(this);
        mBinding.llSortAll.setOnClickListener(this);
        mBinding.llDelete.setOnClickListener(this);
        mBinding.llSort.setOnClickListener(this);
        mTrackAdapter.setOnItemChildClickListener(this);
        mTrackAdapter.setOnItemClickListener(this);
        mPlayerManager.addPlayerStatusListener(playerStatusListener);
    }

    @Override
    public void initData() {
        List<XmDownloadAlbum> downloadAlbums = mDownloadManager.getDownloadAlbums(true);
        for (XmDownloadAlbum album : downloadAlbums) {
            if (album.getAlbumId() == mAlbumId) {
                mAlbum = album;
                break;
            }
        }
        Glide.with(this).load(mAlbum.getCoverUrlMiddle()).into((ImageView) mView.findViewById(R.id.iv_cover));

        mBinding.tvName.setText(mAlbum.getAlbumTitle());

        mBinding.tvSize.setText(ZhumulangmaUtil.byte2FitMemorySize(mAlbum.getDownloadTrackSize()));
        List<Track> trackInAlbum = XmDownloadManager.getInstance().getDownloadTrackInAlbum(mAlbumId, true);
        Collections.sort(trackInAlbum, ComparatorUtil.comparatorByDownloadOverTime(true));
        mBinding.tvAuthor.setText(trackInAlbum.get(0).getAnnouncer().getNickname());
        mBinding.tvTrackNum.setText(String.format(mActivity.getResources().getString(R.string.ji),mAlbum.getTrackCount()));
        mTrackAdapter.setNewData(trackInAlbum);
        clearStatus();
    }

    @Override
    public String[] onBindBarTitleText() {
        return new String[]{"下载详情"};
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_share};
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_more) {
            Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_BATCH_DOWNLOAD)
                    .withLong(KeyCode.Home.ALBUMID, mAlbumId)
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_BATCH_DOWNLOAD, (ISupportFragment) navigation)));
        } else if (id == R.id.ll_sort_all) {
            Object navigation = ARouter.getInstance().build(Constants.Router.Listen.F_DOWNLOAD_SORT)
                    .withLong(KeyCode.Home.ALBUMID, mAlbumId)
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Listen.F_DOWNLOAD_SORT, (ISupportFragment) navigation)));
        } else if (id == R.id.ll_delete) {
            Object navigation = ARouter.getInstance().build(Constants.Router.Listen.F_DOWNLOAD_DELETE)
                    .withLong(KeyCode.Home.ALBUMID, mAlbumId)
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Listen.F_DOWNLOAD_DELETE, (ISupportFragment) navigation)));
        } else if (id == R.id.ll_sort) {
            List<Track> trackInAlbum = XmDownloadManager.getInstance().getDownloadTrackInAlbum(mAlbumId, true);
            Collections.sort(trackInAlbum, ComparatorUtil.comparatorByDownloadOverTime(!mBinding.tvSort.getText().equals("正序")));
            mTrackAdapter.setNewData(trackInAlbum);
            mBinding.tvSort.setText(mBinding.tvSort.getText().equals("正序") ? "倒序" : "正序");

        }
    }

    @Override
    public  void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Listen.DOWNLOAD_SORT:
            case EventCode.Listen.DOWNLOAD_DELETE:
                if(isSupportVisible()){
                    return;
                }
                List<Track> downloadTracks = XmDownloadManager.getInstance().getDownloadTrackInAlbum(mAlbumId, true);
                Collections.sort(downloadTracks, ComparatorUtil.comparatorByUserSort(true));
                mTrackAdapter.setNewData(downloadTracks);
                mBinding.tvTrackNum.setText(String.format(mActivity.getResources().getString(R.string.ji),
                        downloadTracks.size()));
                if (mTrackAdapter.getItemCount()-mTrackAdapter.getEmptyViewCount()==0) {
                    mBinding.clActionbar.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHARE));
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        mDownloadManager.clearDownloadedTrack(mTrackAdapter.getItem(position).getDataId());
        mTrackAdapter.remove(position);
        mBinding.tvTrackNum.setText(String.format(mActivity.getResources().getString(R.string.ji),
                mTrackAdapter.getItemCount()-mTrackAdapter.getEmptyViewCount()));
        if (mTrackAdapter.getItemCount()-mTrackAdapter.getEmptyViewCount()==0) {
            mBinding.clActionbar.setVisibility(View.GONE);
        }
        mHandler.postDelayed(() -> EventBus.getDefault().post(new FragmentEvent(EventCode.Listen.DOWNLOAD_DELETE)), 100);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        XmPlayerManager.getInstance(mActivity).playList(mTrackAdapter.getData(), position);
        RouteUtil.navigateTo(Constants.Router.Home.F_PLAY_TRACK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerManager.removePlayerStatusListener(playerStatusListener);
    }

    /**
     * 更新播放进度
     */
    private void updatePlayStatus(int currPos, int duration) {
        Track track = mPlayerManager.getCurrSoundIgnoreKind(true);
        if (null == track) {
            return;
        }
        int index = mTrackAdapter.getData().indexOf(track);
        if (index != -1) {
            TextView tvHasplay = (TextView) mTrackAdapter.getViewByPosition(index, R.id.tv_hasplay);
            if (null != tvHasplay && mTrackAdapter.getItem(index).getDataId() == track.getDataId()) {
                tvHasplay.setText(getString(R.string.hasplay, 100 * currPos / duration));
            }else {
                mTrackAdapter.notifyItemChanged(index);
            }
        }
    }
    private IXmPlayerStatusListener playerStatusListener = new IXmPlayerStatusListener() {

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
        public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

        }

        @Override
        public void onBufferingStart() {

        }

        @Override
        public void onBufferingStop() {

        }

        @Override
        public void onBufferProgress(int i) {

        }

        @Override
        public void onPlayProgress(int i, int i1) {
            updatePlayStatus(i, i1);
        }

        @Override
        public boolean onError(XmPlayerException e) {
            return false;
        }

    };
}
