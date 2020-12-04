package com.gykj.zhumulangma.home.fragment;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.databinding.CommonLayoutRefreshListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchAlbumViewModel;
import com.kingja.loadsir.callback.Callback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import static com.gykj.zhumulangma.common.util.ZhumulangmaUtil.filterPaidAlbum;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/13 15:12
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索专辑
 */
public class SearchAlbumFragment extends BaseRefreshFragment<CommonLayoutRefreshListBinding, SearchAlbumViewModel, Album> {


    private AlbumAdapter mAlbumAdapter;

    public SearchAlbumFragment() {

    }


    @Override
    public int onBindLayout() {
        return R.layout.common_layout_refresh_list;
    }


    @Override
    public void initView() {
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album_line);
        mAlbumAdapter.bindToRecyclerView(mBinding.recyclerview);

    }

    @Override
    public void initListener() {
        super.initListener();
        mAlbumAdapter.setOnItemClickListener((adapter, view, position) ->
                RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId())));
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mAlbumAdapter);
    }

    @Override
    public void initData() {
        String keyword = getArguments().getString(KeyCode.Home.KEYWORD);
        mViewModel.setKeyword(keyword);
        mViewModel.init();
    }


    @Override
    public Class<SearchAlbumViewModel> onBindViewModel() {
        return SearchAlbumViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        filterPaidAlbum(mViewModel.getInitAlbumsEvent()).observe(this, albums -> mAlbumAdapter.setNewData(albums));
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
