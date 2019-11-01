package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.databinding.CommonLayoutRefreshLoadmoreBinding;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.SearchSuggestAdapter;
import com.gykj.zhumulangma.home.bean.SearchSuggestItem;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索下建议词页
 */
@Route(path = Constants.Router.Home.F_SEARCH_SUGGEST)
public class SearchSuggestFragment extends BaseMvvmFragment<CommonLayoutRefreshLoadmoreBinding,SearchViewModel> implements
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private String mKeyword;
    private SearchSuggestAdapter mSuggestAdapter;
    private onSearchListener mSearchListener;
    private TextView tvHeader;
    @Override
    protected int onBindLayout() {
        return R.layout.common_layout_refresh_loadmore;
    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setSwipeBackEnable(false);
    }
    @Override
    protected void initView() {
        mBinding.refreshLayout.setEnableRefresh(false);
        mBinding.refreshLayout.setEnableLoadMore(false);
        mBinding.recyclerview.setHasFixedSize(true);
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mSuggestAdapter=new SearchSuggestAdapter(null);
        mSuggestAdapter.bindToRecyclerView(mBinding.recyclerview);
        View vHeader = LayoutInflater.from(mActivity).inflate(R.layout.home_item_search_suggest_query, null);
        tvHeader=vHeader.findViewById(R.id.tv_label);
        mSuggestAdapter.setHeaderView(vHeader);
        vHeader.setOnClickListener(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSuggestAdapter.setOnItemClickListener(this);
        mSuggestAdapter.setOnItemChildClickListener(this);
    }

    @Override
    public void initData() {

    }
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    public void loadSuggest(String  s){
        mKeyword =s;
        s="搜索\""+s+"\"";
        SpannableString spannableString=new SpannableString(s);
        int start = s.indexOf("\"");
        int end = s.lastIndexOf("\"");
        ForegroundColorSpan colorSpan=new ForegroundColorSpan(mActivity.getResources().getColor(R.color.colorPrimary));
        spannableString.setSpan(colorSpan,start+1,end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvHeader.setText(spannableString);
        mViewModel.getSuggestWord(mKeyword);
    }
    @Override
    public void initViewObservable() {
        mViewModel.getWordsSingleLiveEvent()
                .observe(this, suggestItems -> mSuggestAdapter.setNewData(suggestItems));
    }
    public void setSearchListener(onSearchListener searchListener) {
        mSearchListener = searchListener;
    }

    @Override
    public Class<SearchViewModel> onBindViewModel() {
        return SearchViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SearchSuggestItem item = mSuggestAdapter.getItem(position);

        if (item.itemType== SearchSuggestItem.ALBUM){
            Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID,item.mAlbumResult.getAlbumId())
                    .navigation();
            EventBus.getDefault().post(new ActivityEvent(
                    EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
        }else {
            mSearchListener.onSearch(item.mQueryResult.getKeyword());
        }
    }

    @Override
    public void onClick(View v) {
        mSearchListener.onSearch(mKeyword);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        SearchSuggestItem item = mSuggestAdapter.getItem(position);
        mViewModel.play(String.valueOf(item.mAlbumResult.getAlbumId()));
        Object navigation = ARouter.getInstance().build(Constants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID,item.mAlbumResult.getAlbumId())
                .navigation();
        EventBus.getDefault().post(new ActivityEvent(
                EventCode.Main.NAVIGATE, new NavigateBean(Constants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
    }


    public interface onSearchListener {

        void onSearch(String keyword);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy() called");
    }
}
