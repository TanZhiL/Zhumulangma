package com.gykj.zhumulangma.home.fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.databinding.CommonLayoutRefreshListBinding;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.RouteHelper;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.SearchSuggestAdapter;
import com.gykj.zhumulangma.home.bean.SearchSuggestItem;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/18 13:58
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索下建议词页
 */
@Route(path = Constants.Router.Home.F_SEARCH_SUGGEST)
public class SearchSuggestFragment extends BaseMvvmFragment<CommonLayoutRefreshListBinding, SearchViewModel> implements
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private String mKeyword;
    private SearchSuggestAdapter mSuggestAdapter;
    private onSearchListener mSearchListener;
    private TextView tvHeader;
    @Override
    public int onBindLayout() {
        return R.layout.common_layout_refresh_list;
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
        mView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        mBinding.refreshLayout.setEnableRefresh(false);
        mBinding.refreshLayout.setEnableLoadMore(false);
        mBinding.recyclerview.setHasFixedSize(true);
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mSuggestAdapter = new SearchSuggestAdapter(null);
        mSuggestAdapter.bindToRecyclerView(mBinding.recyclerview);
        View vHeader = getLayoutInflater().inflate(R.layout.home_item_search_suggest_query, null);
        tvHeader = vHeader.findViewById(R.id.tv_label);
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
        if(!TextUtils.isEmpty(mKeyword)){
            loadSuggest();
        }
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
        if(hasInit){
            loadSuggest();
        }
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    public void loadSuggest() {
        String  s = "搜索\"" + mKeyword + "\"";
        SpannableString spannableString = new SpannableString(s);
        int start = s.indexOf("\"");
        int end = s.lastIndexOf("\"");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.colorPrimary));
        spannableString.setSpan(colorSpan, start + 1, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
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
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SearchSuggestItem item = mSuggestAdapter.getItem(position);

        if (item.itemType == SearchSuggestItem.ALBUM) {
            RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                    .withLong(KeyCode.Home.ALBUMID, item.mAlbumResult.getAlbumId()));
        } else {
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
        RouteHelper.navigateTo(mRouter.build(Constants.Router.Home.F_ALBUM_DETAIL)
                .withLong(KeyCode.Home.ALBUMID, item.mAlbumResult.getAlbumId()));
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
