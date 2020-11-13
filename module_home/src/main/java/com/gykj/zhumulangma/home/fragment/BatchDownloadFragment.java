package com.gykj.zhumulangma.home.fragment;

import androidx.lifecycle.ViewModelProvider;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.common.util.SystemUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.DownloadTrackAdapter;
import com.gykj.zhumulangma.home.databinding.HomeFragmentBatchDownloadBinding;
import com.gykj.zhumulangma.home.dialog.TrackPagerPopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.BatchDownloadViewModel;
import com.kingja.loadsir.callback.Callback;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.DownloadState;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.exception.AddDownloadException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/12 8:49
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:批量下载
 */

@Route(path = Constants.Router.Home.F_BATCH_DOWNLOAD)
public class BatchDownloadFragment extends BaseRefreshFragment<HomeFragmentBatchDownloadBinding, BatchDownloadViewModel, Track> implements
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener, TrackPagerPopup.onPopupDismissingListener {


    @Autowired(name = KeyCode.Home.ALBUMID)
    public long mAlbumId;
    private long mTotalSize;
    private DownloadTrackAdapter mDownloadTrackAdapter;
    private TrackPagerPopup mPagerPopup;
    private int mTotalCount;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_batch_download;
    }

    @Override
    protected void initView() {
        mDownloadTrackAdapter = new DownloadTrackAdapter(R.layout.home_item_batch_download);
        mBinding.includeList.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.includeList.recyclerview.setHasFixedSize(true);
        mDownloadTrackAdapter.bindToRecyclerView(mBinding.includeList.recyclerview);
        mPagerPopup = new TrackPagerPopup(mActivity, this);
        mPagerPopup.setDismissingListener(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        mDownloadTrackAdapter.setOnItemClickListener(this);
        mBinding.cbAll.setOnClickListener(this);
        mBinding.llSelect.setOnClickListener(this);
        mBinding.tvBatchDownload.setOnClickListener(this);

    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.includeList.refreshLayout, mDownloadTrackAdapter);
    }

    @Override
    public void initData() {
        mViewModel.setAlbumId(mAlbumId);
        String sort = "time_desc";
        mViewModel.setSort(sort);
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitTracksEvent().observe(this, tracks -> {
            mTotalCount = tracks.getTotalCount();
            setPager(mTotalCount);
            mDownloadTrackAdapter.setNewData(tracks.getTracks());
            //更新全选按钮
            mBinding.cbAll.setChecked(CollectionUtils.isSubCollection(
                    mDownloadTrackAdapter.getData(), mDownloadTrackAdapter.getSelectedTracks()));
        });
    }

    @Override
    protected void onRefreshSucc(List<Track> list) {
        mDownloadTrackAdapter.addData(0, list);
        //更新全选按钮
        mBinding.cbAll.setChecked(CollectionUtils.isSubCollection(
                mDownloadTrackAdapter.getData(), mDownloadTrackAdapter.getSelectedTracks()));
    }

    @Override
    protected void onLoadMoreSucc(List<Track> list) {
        super.onLoadMoreSucc(list);
        //更新全选按钮
        mBinding.cbAll.setChecked(CollectionUtils.isSubCollection(
                mDownloadTrackAdapter.getData(), mDownloadTrackAdapter.getSelectedTracks()));
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.HIDE_GP));
    }

    @Override
    protected void onRevisible() {
        super.onRevisible();
        mDownloadTrackAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHOW_GP));
    }

    @Override
    public String[] onBindBarTitleText() {
        return new String[]{"批量下载"};
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        return new Integer[]{R.drawable.ic_common_download};
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mDownloadTrackAdapter) {
            Track track = mDownloadTrackAdapter.getItem(position);
            if (XmDownloadManager.getInstance().getSingleTrackDownloadStatus(track.getDataId()) != DownloadState.NOADD) {
                return;
            }
            CheckBox checkBox = view.findViewById(R.id.cb);
            checkBox.setChecked(!checkBox.isChecked());
            boolean checked = checkBox.isChecked();

            if (checked) {
                mTotalSize += track.getDownloadSize();
                mDownloadTrackAdapter.getSelectedTracks().add(track);
            } else {
                mTotalSize -= track.getDownloadSize();
                mDownloadTrackAdapter.getSelectedTracks().remove(track);
            }
            if (mDownloadTrackAdapter.getSelectedTracks().size() > 0) {
                mBinding.tvMemory.setVisibility(View.VISIBLE);
                mBinding.tvMemory.setText(getString(R.string.selected_num,
                        mDownloadTrackAdapter.getSelectedTracks().size(), ZhumulangmaUtil.byte2FitMemorySize(mTotalSize),
                        SystemUtil.getRomTotalSize(mActivity)));
                mBinding.tvBatchDownload.setEnabled(true);
                mBinding.tvBatchDownload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mBinding.tvMemory.setVisibility(View.GONE);
                mBinding.tvBatchDownload.setEnabled(false);
                mBinding.tvBatchDownload.setBackgroundColor(getResources().getColor(R.color.colorHint));
            }
            //更新全选按钮
            mBinding.cbAll.setChecked(CollectionUtils.isSubCollection(
                    mDownloadTrackAdapter.getData(), mDownloadTrackAdapter.getSelectedTracks()));
        } else {
            mPagerPopup.dismissWith(() -> mViewModel.getTrackList(position + 1));
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ll_select == id) {
            switchPager();
        } else if (id == R.id.cb_all) {
            boolean isChecked = ((CheckBox) v).isChecked();
            List<Track> tracks = mDownloadTrackAdapter.getData();
            List<Track> selectedTracks = mDownloadTrackAdapter.getSelectedTracks();
            if (isChecked) {
                for (int i = 0; i < tracks.size(); i++) {
                    if (!selectedTracks.contains(tracks.get(i)) && XmDownloadManager.getInstance()
                            .getSingleTrackDownloadStatus(tracks.get(i).getDataId()) == DownloadState.NOADD) {
                        mTotalSize += tracks.get(i).getDownloadSize();
                        selectedTracks.add(tracks.get(i));
                        mDownloadTrackAdapter.notifyItemChanged(i);
                    }
                }
            } else {
                for (int i = 0; i < tracks.size(); i++) {
                    if (selectedTracks.contains(tracks.get(i))) {
                        mTotalSize -= tracks.get(i).getDownloadSize();
                        selectedTracks.remove(tracks.get(i));
                        mDownloadTrackAdapter.notifyItemChanged(i);
                    }
                }
            }

            if (mDownloadTrackAdapter.getSelectedTracks().size() > 0) {
                mBinding.tvMemory.setVisibility(View.VISIBLE);
                mBinding.tvMemory.setText(getString(R.string.selected_num,
                        mDownloadTrackAdapter.getSelectedTracks().size(), ZhumulangmaUtil.byte2FitMemorySize(mTotalSize),
                        SystemUtil.getRomTotalSize(mActivity)));
                mBinding.tvBatchDownload.setEnabled(true);
                mBinding.tvBatchDownload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mBinding.tvMemory.setVisibility(View.GONE);
                mBinding.tvBatchDownload.setEnabled(false);
                mBinding.tvBatchDownload.setBackgroundColor(getResources().getColor(R.color.colorHint));
            }

        } else if (R.id.tv_batch_download == id) {
            List<Track> selectedTracks = mDownloadTrackAdapter.getSelectedTracks();
            if (CollectionUtils.isEmpty(selectedTracks)) {
                return;
            }
            Collections.sort(selectedTracks, (o1, o2) ->
                    Integer.compare(o2.getOrderPositionInAlbum(), o1.getOrderPositionInAlbum()));
            List<Long> trackIds = new ArrayList<>();
            for (Track selectedTrack : selectedTracks) {
                trackIds.add(selectedTrack.getDataId());
            }
            XmDownloadManager.getInstance().downloadTracks(trackIds, true, new IDoSomethingProgress<AddDownloadException>() {
                @Override
                public void begin() {

                }

                @Override
                public void success() {
                    mTotalSize = 0;
                    mBinding.tvMemory.setVisibility(View.GONE);
                    mBinding.tvBatchDownload.setEnabled(false);
                    mBinding.cbAll.setChecked(false);
                    mBinding.tvBatchDownload.setBackgroundColor(getResources().getColor(R.color.colorHint));
                    ToastUtil.showToast(ToastUtil.LEVEL_S, "已加入下载队列");
                    selectedTracks.clear();
                    mDownloadTrackAdapter.notifyDataSetChanged();
                }

                @Override
                public void fail(AddDownloadException e) {
                    if (e.getCode() == AddDownloadException.CODE_NULL) {
                        ToastUtil.showToast("参数不能为null");
                    } else if (e.getCode() == AddDownloadException.CODE_MAX_OVER) {
                        ToastUtil.showToast("批量下载个数超过最大值");
                    } else if (e.getCode() == AddDownloadException.CODE_NOT_FIND_TRACK) {
                        ToastUtil.showToast("不能找到相应的声音");
                    } else if (e.getCode() == AddDownloadException.CODE_MAX_DOWNLOADING_COUNT) {
                        ToastUtil.showToast("同时下载的音频个数不能超过500");
                    } else if (e.getCode() == AddDownloadException.CODE_DISK_OVER) {
                        ToastUtil.showToast("磁盘已满");
                    } else if (e.getCode() == AddDownloadException.CODE_MAX_SPACE_OVER) {
                        ToastUtil.showToast("下载的音频超过了设置的最大空间");
                    } else if (e.getCode() == AddDownloadException.CODE_NO_PAY_SOUND) {
                        ToastUtil.showToast("下载的付费音频中有没有支付");
                    }
                }
            });

        }
    }


    @Override
    public Class<BatchDownloadViewModel> onBindViewModel() {
        return BatchDownloadViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        RouterUtil.navigateTo(mRouter.build(Constants.Router.Listen.F_DOWNLOAD)
                .withInt(KeyCode.Listen.TAB_INDEX, 2));
    }

    /**
     * 改变分页器选中状态
     */
    private void changePageStatus() {
        for (int i = 0; i < mPagerPopup.getPagerAdapter().getData().size(); i++) {
            Iterator<Track> iterator = mDownloadTrackAdapter.getSelectedTracks().iterator();
            View ivSelected = mPagerPopup.getPagerAdapter().getViewByPosition(i, R.id.iv_selected);
            if (ivSelected != null) {
                ivSelected.setVisibility(View.GONE);
                while (iterator.hasNext()) {
                    Track next = iterator.next();
                    int orderPositionInAlbum = next.getOrderPositionInAlbum() + 1;
                    if (orderPositionInAlbum <= mTotalCount - i * BatchDownloadViewModel.PAGESIEZ
                            && orderPositionInAlbum > mTotalCount - (i + 1) * BatchDownloadViewModel.PAGESIEZ) {
                        ivSelected.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            } else {
                mDownloadTrackAdapter.notifyItemChanged(i);
            }
            TextView tvPage = (TextView) mPagerPopup.getPagerAdapter().getViewByPosition(i, R.id.tv_page);
            if (tvPage != null) {
                if (mViewModel.getUpTrackPage() <= i && i <= mViewModel.getCurTrackPage() - 2) {
                    tvPage.setBackgroundResource(R.drawable.shap_home_pager_selected);
                    tvPage.setTextColor(Color.WHITE);
                } else {
                    tvPage.setBackgroundResource(R.drawable.shap_home_pager_defualt);
                    tvPage.setTextColor(getResources().getColor(R.color.textColorPrimary));
                }
            } else {
                mPagerPopup.getPagerAdapter().notifyItemChanged(i);
            }
        }
    }

    /**
     * 切换分页器是否显示
     */
    private void switchPager() {
        if (mPagerPopup.isShow()) {
            mPagerPopup.dismiss();
        } else {
            mBinding.ivSelectPage.animate().rotation(-90).setDuration(200);
            new XPopup.Builder(mActivity).atView(mBinding.clActionbar).setPopupCallback(new SimpleCallback() {
                @Override
                public void onCreated() {
                    super.onCreated();
                    mPagerPopup.getRvPager().setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            changePageStatus();
                        }
                    });
                }

                @Override
                public void beforeShow() {
                    super.beforeShow();
                    changePageStatus();
                }
            }).popupPosition(PopupPosition.Bottom)
                    .asCustom(mPagerPopup).show();
        }

    }

    /**
     * 设置分页数据
     *
     * @param totalcount
     */
    private void setPager(int totalcount) {
        int pagesize = 50;
        mBinding.tvPagecount.setText(getString(R.string.pagecount, totalcount));
        List<String> list = new ArrayList<>();
        for (int i = 0; i < totalcount / pagesize; i++) {
            list.add(totalcount - (i * pagesize) + "~" + (totalcount - ((i + 1) * pagesize) + 1));
        }
        if (totalcount % pagesize != 0) {
            list.add(totalcount - totalcount / pagesize * pagesize + "~1");
        }


        mPagerPopup.getPagerAdapter().setNewData(list);
    }


    @Override
    public void onDismissing() {
        mBinding.ivSelectPage.animate().rotation(90).setDuration(200);
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
