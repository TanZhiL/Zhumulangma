package com.gykj.zhumulangma.listen.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.DownloadDeleteAdapter;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.IDoSomethingProgress;
import com.ximalaya.ting.android.sdkdownloader.exception.BaseRuntimeException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Thomas.
 * <br/>Date: 2019/10/10 14:51
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:下载批量删除
 */
@Route(path = Constants.Router.Listen.F_DOWNLOAD_DELETE)
public class DownloadDeleteFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Autowired(name = KeyCode.Listen.ALBUMID)
    public long mAlbumId;

    private DownloadDeleteAdapter mDeleteAdapter;
    private XmDownloadManager mDownloadManager = XmDownloadManager.getInstance();
    private List<Long> mSelectedIds = new ArrayList<>();

    @Override
    protected int onBindLayout() {
        return R.layout.listen_fragment_download_delete;
    }

    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = fd(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        mDeleteAdapter = new DownloadDeleteAdapter(R.layout.listen_item_download_delete);
        mDeleteAdapter.bindToRecyclerView(recyclerView);

    }

    @Override
    public void initListener() {
        super.initListener();
        mDeleteAdapter.setOnItemClickListener(this);
        fd(R.id.ll_delete).setOnClickListener(this);
        ((CheckBox) fd(R.id.cb_all)).setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {
        List<Track> tracks;
        if (mAlbumId == 0) {
            tracks = mDownloadManager.getDownloadTracks(true);
        } else {
            tracks = mDownloadManager.getDownloadTrackInAlbum(mAlbumId, true);
        }
        for (Track downloadTrack : tracks) {
            downloadTrack.setPaid(false);
        }
        mDeleteAdapter.setNewData(tracks);
    }

    @Override
    public String[] onBindBarTitleText() {
        return new String[]{"批量删除"};
    }


    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.HIDE_GP));
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHOW_GP));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //借用Paid字段作为选中标记
        Track item = mDeleteAdapter.getItem(position);
        item.setPaid(!item.isPaid());
        ((CheckBox) mDeleteAdapter.getViewByPosition(position, R.id.cb)).setChecked(item.isPaid());
        if (item.isPaid()) {
            mSelectedIds.add(item.getDataId());
        } else {
            mSelectedIds.remove(item.getDataId());
        }
        updateButton();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_delete) {
            if (mSelectedIds.size() == 0) {
                return;
            } else {
                mDownloadManager.batchClearDownloadedTracks(mSelectedIds, new IDoSomethingProgress() {
                    @Override
                    public void begin() {
                        showLoadingView(null);
                    }

                    @Override
                    public void success() {
                        initData();
                        clearStatus();
                        mSelectedIds.clear();
                        updateButton();
                        EventBus.getDefault().post(new FragmentEvent(EventCode.Listen.DOWNLOAD_DELETE));
                    }

                    @Override
                    public void fail(BaseRuntimeException e) {
                        e.printStackTrace();
                        clearStatus();
                        ToastUtil.showToast(ToastUtil.LEVEL_E, e.getLocalizedMessage());
                    }
                });
            }
        }
    }

    private void updateButton() {
        fd(R.id.ll_delete).setBackgroundColor(mSelectedIds.size() > 0 ? mActivity.getResources().getColor(R.color.colorPrimary) :
                mActivity.getResources().getColor(R.color.colorHint));
        ((CheckBox) fd(R.id.cb_all)).setChecked(mDeleteAdapter.getItemCount() != 0 && mSelectedIds.size() == mDeleteAdapter.getItemCount());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mSelectedIds.clear();
        for (int i = 0; i < mDeleteAdapter.getData().size(); i++) {
            mDeleteAdapter.getData().get(i).setPaid(isChecked);
            CheckBox checkBox = (CheckBox) mDeleteAdapter.getViewByPosition(i, R.id.cb);
            if (null != checkBox) {
                checkBox.setChecked(isChecked);
            } else {
                mDeleteAdapter.notifyItemChanged(i);
            }
            if (isChecked) {
                mSelectedIds.add(mDeleteAdapter.getData().get(i).getDataId());
            }
        }
        updateButton();
    }
}
