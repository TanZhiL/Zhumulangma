package com.gykj.zhumulangma.home.activity;


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
import com.gykj.zhumulangma.common.mvvm.view.status.ListSkeleton;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshActivity;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.common.util.ZhumulangmaUtil;
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
public class AlbumListActivity extends BaseRefreshActivity<CommonLayoutListBinding, AlbumListViewModel, Album> 
        implements BaseQuickAdapter.OnItemClickListener {
    //猜你喜欢
    public static final int LIKE = 0;
    //付费精品
    public static final int PAID = -1;
    //听单
    public static final int COLUMN = -2;
    //主播专辑
    public static final int ANNOUNCER = 999;
    @Autowired(name = KeyCode.Home.CATEGORY)
    public int mCategory;
    @Autowired(name = KeyCode.Home.TAG)
    public String mTag;
    @Autowired(name = KeyCode.Home.COLUMN)
    public String mColumn;
    @Autowired(name = KeyCode.Home.ANNOUNCER_ID)
    public long mAnnouncerId;
    @Autowired(name = KeyCode.Home.TITLE)
    public String mTitle;
    private AlbumAdapter mAlbumAdapter;


    public AlbumListActivity() {

    }

    @Override
    public int onBindLayout() {
        return R.layout.common_layout_list;
    }

    @Override
    public void initView() {
        super.initView();
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
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
        mViewModel.setAnnouncerId(mAnnouncerId);
        mViewModel.init(mCategory,mTag,mColumn);
    }

    @Override
    public void initViewObservable() {
        ZhumulangmaUtil.filterPaidAlbum(mViewModel.getInitAlbumsEvent()).observe(this,
                albums -> mAlbumAdapter.setNewData(albums));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, mAlbumAdapter.getItem(position).getId()));
    }

    @Override
    public Class<AlbumListViewModel> onBindViewModel() {
        return AlbumListViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(getApplication());
    }

    @Override
    public Integer[] onBindBarRightIcon() {
        if (mCategory == ANNOUNCER || mCategory == LIKE) {
            return null;
        }
        return new Integer[]{R.drawable.ic_common_search};
    }

    @Override
    public void onRight1Click(View v) {
        super.onRight1Click(v);
        RouteHelper.navigateTo(Constants.Router.Home.F_SEARCH);
    }

    @Override
    public Callback getInitStatus() {
        return new ListSkeleton();
    }
}
