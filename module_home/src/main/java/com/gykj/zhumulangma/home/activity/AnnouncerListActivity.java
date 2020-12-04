package com.gykj.zhumulangma.home.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.databinding.CommonLayoutRefreshListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshActivity;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerListViewModel;
import com.kingja.loadsir.callback.Callback;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;


/**
 * Author: Thomas.
 * <br/>Date: 2019/9/11 11:40
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:主播列表
 */

@Route(path = Constants.Router.Home.F_ANNOUNCER_LIST)
public class AnnouncerListActivity extends BaseRefreshActivity<CommonLayoutRefreshListBinding, AnnouncerListViewModel, Announcer>
        implements OnLoadMoreListener {

    @Autowired(name = KeyCode.Home.CATEGORY_ID)
    public long mCategoryId;
    @Autowired(name = KeyCode.Home.TITLE)
    public String mTitle;
    private AnnouncerAdapter mAnnouncerAdapter;

    @Override
    public int onBindLayout() {
        return R.layout.common_layout_refresh_list;
    }

    @Override
    public void initView() {
        super.initView();
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerview.setHasFixedSize(true);
        mAnnouncerAdapter = new AnnouncerAdapter(R.layout.home_item_announcer);
        mAnnouncerAdapter.bindToRecyclerView(mBinding.recyclerview);
        setTitle(new String[]{mTitle});
    }

    @Override
    public void initListener() {
        super.initListener();
        mAnnouncerAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ANNOUNCER_DETAIL)
                .withLong(KeyCode.Home.ANNOUNCER_ID, mAnnouncerAdapter.getItem(position).getAnnouncerId())
                .withString(KeyCode.Home.ANNOUNCER_NAME, mAnnouncerAdapter.getItem(position).getNickname())));
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mAnnouncerAdapter);
    }


    @Override
    public void initData() {
        mViewModel.setCategoryId(mCategoryId);
        mViewModel.init();
    }


    @Override
    public void initViewObservable() {
        mViewModel.getInitAnnouncersEvent().observe(this, announcers -> mAnnouncerAdapter.setNewData(announcers));
    }


    @Override
    public Class<AnnouncerListViewModel> onBindViewModel() {
        return AnnouncerListViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(getApplication());
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
