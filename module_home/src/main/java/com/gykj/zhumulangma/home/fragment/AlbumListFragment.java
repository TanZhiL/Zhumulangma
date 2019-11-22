package com.gykj.zhumulangma.home.fragment;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.databinding.CommonLayoutListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.util.RouterUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AlbumAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AlbumListViewModel;
import com.kingja.loadsir.callback.Callback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Author: Thomas.
 * <br/>Date: 2019/8/14 10:21
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:专辑列表
 */
@Route(path = Constants.Router.Home.F_ALBUM_LIST)
public class AlbumListFragment extends BaseRefreshMvvmFragment<CommonLayoutListBinding, AlbumListViewModel, Album> implements
        BaseQuickAdapter.OnItemClickListener {
    //猜你喜欢
    public static final int LIKE = 0;
    //付费精品
    public static final int PAID = -1;
    //主播专辑
    public static final int ANNOUNCER = 999;
    @Autowired(name = KeyCode.Home.TYPE)
    public int mType;
    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long mAnnouncerId;
    @Autowired(name = KeyCode.Home.TITLE)
    public String mTitle;
    private AlbumAdapter mAlbumAdapter;


    public AlbumListFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    protected void initView() {
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.recyclerview.setHasFixedSize(true);
        mAlbumAdapter = new AlbumAdapter(R.layout.home_item_album_line);
        mAlbumAdapter.bindToRecyclerView(mBinding.recyclerview);
        setTitle(new String[]{mTitle});
    }

    @Override
    public void initListener() {
        super.initListener();
        mAlbumAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(mBinding.refreshLayout, mAlbumAdapter);
    }

    @Override
    public void initData() {
        mViewModel.setType(mType);
        mViewModel.setAnnouncerId(mAnnouncerId);
        mViewModel.init();
    }

    @Override
    public void initViewObservable() {
        mViewModel.getInitAlbumsEvent().observe(this, albums -> mAlbumAdapter.setNewData(albums));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        RouterUtil.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId()));
    }

    @Override
    public Class<AlbumListViewModel> onBindViewModel() {
        return AlbumListViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        if (mType == ANNOUNCER || mType == LIKE) {
            return null;
        }
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        RouterUtil.navigateTo(Constants.Router.Home.F_SEARCH);
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }


    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
