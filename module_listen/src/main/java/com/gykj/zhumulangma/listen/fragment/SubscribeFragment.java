package com.gykj.zhumulangma.listen.fragment;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.SubscribeBean;
import com.gykj.zhumulangma.common.databinding.CommonLayoutListBinding;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.SubscribeAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.SubscribeViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:订阅
 */
public class SubscribeFragment extends BaseRefreshFragment<CommonLayoutListBinding, SubscribeViewModel, SubscribeBean>
        implements BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {

    private SubscribeAdapter mSubscribeAdapter;

    private View vFooter;

    public SubscribeFragment() {
    }


    @Override
    public int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void initView() {
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mSubscribeAdapter = new SubscribeAdapter(R.layout.listen_item_subscribe);
        mSubscribeAdapter.bindToRecyclerView(mBinding.recyclerview);
        View inflate = getLayoutInflater().inflate(R.layout.listen_layout_subscribe_footer, null);
        vFooter = inflate.findViewById(R.id.cl_content);
        mSubscribeAdapter.addFooterView(inflate);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSubscribeAdapter.setOnItemChildClickListener(this);
        mSubscribeAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mSubscribeAdapter.getItem(position).getAlbum().getId())));
        vFooter.setOnClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mSubscribeAdapter);
    }

    @Override
    public void initData() {
        mSubscribeAdapter.setNewData(null);
        mViewModel.getSubscribes();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitSubscribesEvent().observe(this, subscribeBeans ->
                mSubscribeAdapter.setNewData(subscribeBeans));

    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (id == R.id.iv_play) {
            mViewModel.play(String.valueOf(mSubscribeAdapter.getItem(position).getAlbumId()));
        } else if (id == R.id.iv_more) {
            EventBus.getDefault().post(new ActivityEvent(EventCode.Main.SHARE, null));
        }
    }

    @Override
    public Class<SubscribeViewModel> onBindViewModel() {
        return SubscribeViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }


    @Override
    public void onClick(View v) {
        if (v == vFooter) {
            RouteHelper.navigateTo(Constants.Router.Home.F_RANK);
        }
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case EventCode.Listen.TAB_REFRESH:
                if (mBaseLoadService.getCurrentCallback() != getInitStatus().getClass()) {
                    mBinding.recyclerview.scrollToPosition(0);
                    mBinding.refreshLayout.autoRefresh();
                }
                break;
        }
    }
}
