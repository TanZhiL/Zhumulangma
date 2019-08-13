package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.Application;
import com.gykj.zhumulangma.common.bean.SearchHistoryBean;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;
import com.jakewharton.rxbinding3.view.RxView;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;

@Route(path = AppConstants.Router.Home.F_SEARCH)
public class SearchFragment extends BaseMvvmFragment<SearchViewModel> implements View.OnClickListener, SearchHistoryFragment.onSearchListener, View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private EditText etKeyword;
    public SearchFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_search;
    }

    @Override
    protected void initView(View view) {
        if( StatusBarUtils.supportTransparentStatusBar()){
            fd(R.id.cl_titlebar).setPadding(0, BarUtils.getStatusBarHeight(),0,0);
        }
        etKeyword=fd(R.id.et_keyword);
        KeyboardUtils.showSoftInput(etKeyword);

        SearchHistoryFragment historyFragment = new SearchHistoryFragment();
        historyFragment.setSearchListener(this);
        loadRootFragment(R.id.fl_container, historyFragment);

        etKeyword.setOnEditorActionListener(this);

    }

    @Override
    public void initListener() {
        super.initListener();
        fd(R.id.iv_pop).setOnClickListener(this);
        etKeyword.setOnFocusChangeListener(this);
        addDisposable(RxView.clicks(fd(R.id.tv_search))
        .throttleFirst(1, TimeUnit.SECONDS)
        .subscribe(unit -> preSearch()));
    }

    @Override
    public void initData() {
        mViewModel._getHotWords();
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.iv_pop){
            hideSoftInput();
            new Handler().postDelayed(() -> pop(), 200);
        }
    }

    @Override
    public void onSearch(String keyword) {
        etKeyword.setText(keyword);
        search(keyword);
    }

    private void search(String keyword) {
        SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
        searchHistoryBean.setKeyword(keyword);
        mViewModel.insertHistory(searchHistoryBean);
        etKeyword.clearFocus();
        hideSoftInput();
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_SEARCH_RESULT)
                .withString(KeyCode.Home.KEYWORD, keyword).navigation();
        if(null!=navigation)
        ((BaseFragment) getTopChildFragment()).start((ISupportFragment) navigation);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (getTopChildFragment() instanceof SearchResultFragment) {
            ((BaseFragment) getTopChildFragment()).pop();
        }
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
    public void initViewObservable() {
        mViewModel.getHotWordsSingleLiveEvent().observe(this, hotWords ->
                etKeyword.setHint(hotWords.get(0).getSearchword()));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId== EditorInfo.IME_ACTION_SEARCH ){
            preSearch();
        }
        return false;
    }

    private void preSearch() {
        if (FragmentUtils.getTop(getChildFragmentManager()) instanceof SearchResultFragment) {
            return;
        }
        if (etKeyword.getText().toString().trim().length() != 0) {
            search(etKeyword.getText().toString());
        } else if (etKeyword.getHint().toString().length() != 0) {
            etKeyword.setText(etKeyword.getHint());
            search(etKeyword.getHint().toString());
        } else {
            ToastUtil.showToast( "请输入要搜索的关键词");
        }

    }
}
