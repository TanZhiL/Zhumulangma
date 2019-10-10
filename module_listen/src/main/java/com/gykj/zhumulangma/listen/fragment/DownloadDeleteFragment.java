package com.gykj.zhumulangma.listen.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.DownloadDeleteAdapter;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.downloadutil.ComparatorUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * Author: Thomas.
 * Date: 2019/10/10 14:51
 * Email: 1071931588@qq.com
 * Description:下载批量删除
 */
@Route(path = AppConstants.Router.Listen.F_DOWNLOAD_DELETE)
public class DownloadDeleteFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    private DownloadDeleteAdapter mDeleteAdapter;
    private XmDownloadManager mDownloadManager=XmDownloadManager.getInstance();
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
    }

    @Override
    public void initData() {
        List<Track> downloadTracks = mDownloadManager.getDownloadTracks(true);
        Collections.sort(downloadTracks, ComparatorUtil.comparatorByUserSort(true));
        mDeleteAdapter.setNewData(downloadTracks);
    }
    @Override
    protected String[] onBindBarTitleText() {
        return new String[]{"批量删除"};
    }


    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.HIDE_GP));
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.Main.SHOW_GP));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //借用Paid字段作为选中标记
        Track item = mDeleteAdapter.getItem(position);
        item.setPaid(!item.isPaid());
        ((CheckBox)mDeleteAdapter.getViewByPosition(position,R.id.cb)).setChecked(item.isPaid());
    }
}
