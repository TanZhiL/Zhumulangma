package com.gykj.zhumulangma.listen.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.FavoriteBean;
import com.gykj.zhumulangma.common.databinding.CommonLayoutListBinding;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshActivity;
import com.gykj.zhumulangma.listen.R;
import com.gykj.zhumulangma.listen.adapter.FavoriteAdapter;
import com.gykj.zhumulangma.listen.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.listen.mvvm.viewmodel.FavoriteViewModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/16 8:45
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:我的喜欢
 */
@Route(path = Constants.Router.Listen.F_FAVORITE)
public class FavoriteActivity extends BaseRefreshActivity<CommonLayoutListBinding,FavoriteViewModel, FavoriteBean> implements
        BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    private FavoriteAdapter mFavoriteAdapter;

    @Override
    public int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    public void initView() {
        super.initView();
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerview.setHasFixedSize(true);
        mFavoriteAdapter = new FavoriteAdapter(R.layout.listen_item_favorite);
        mFavoriteAdapter.bindToRecyclerView(mBinding.recyclerview);
    }

    @Override
    public void initListener() {
        super.initListener();
        mFavoriteAdapter.setOnItemChildClickListener(this);
        mFavoriteAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout,mFavoriteAdapter);
    }


    @Override
    public void initData() {
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitFavoritesEvent().observe(this, favoriteBeans -> mFavoriteAdapter.setNewData(favoriteBeans));
    }


    @Override
    public String[] onBindBarTitleText() {
        return new String[]{"我喜欢的声音"};
    }
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        mViewModel.unlike(mFavoriteAdapter.getItem(position).getTrack());
        mFavoriteAdapter.remove(position);
        if(mFavoriteAdapter.getData().size()==0){
            showEmptyView();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Track track = mFavoriteAdapter.getItem(position).getTrack();
        mViewModel.play(track.getAlbum().getAlbumId(),track.getDataId());
    }

    @Override
    public Class<FavoriteViewModel> onBindViewModel() {
        return FavoriteViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(getApplication());
    }

}
