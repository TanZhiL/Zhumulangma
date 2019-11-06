package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.gykj.zhumulangma.common.Constants;
import com.gykj.zhumulangma.common.bean.SearchHistoryBean;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.SpeechUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.databinding.HomeFragmentSearchBinding;
import com.gykj.zhumulangma.home.dialog.SpeechPopup;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索页
 */
@Route(path = Constants.Router.Home.F_SEARCH)
public class SearchFragment extends BaseMvvmFragment<HomeFragmentSearchBinding, SearchViewModel> implements
        View.OnClickListener, SearchHistoryFragment.onSearchListener, View.OnFocusChangeListener,
        TextView.OnEditorActionListener, SearchSuggestFragment.onSearchListener {

    @Autowired(name = KeyCode.Home.HOTWORD)
    public String mHotword;
    private SearchSuggestFragment mSuggestFragment;
    private SearchHistoryFragment mHistoryFragment;

    private Observable<CharSequence> suggestObservable;
    private Disposable suggestDisposable;
    private SpeechRecognizer mIat;
    private SpeechPopup mSpeechPopup;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    private View vDialog;

    public SearchFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_search;
    }

    @Override
    protected void loadView() {
        super.loadView();
        clearStatus();
    }

    @Override
    protected void initView() {
        if (StatusBarUtils.supportTransparentStatusBar()) {
            mBinding.clTitlebar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        }
        //不支持x86
        mIat = SpeechRecognizer.createRecognizer(mActivity, mInitListener);
        try {
            //返回json类型
            mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        } catch (Exception e) {
            Log.w(TAG, "由于讯飞语音没有提供x86 so文件所以直接捕获此异常");
        }
        mSpeechPopup = new SpeechPopup(mActivity);
    }

    @Override
    public void initListener() {
        super.initListener();
        suggestObservable = RxTextView.textChanges(mBinding.etKeyword)
                .debounce(300, TimeUnit.MILLISECONDS)
                .skip(1)
                .doOnSubscribe(d -> {
                    suggestDisposable = d;
                    accept(d);
                })
                .doOnNext(charSequence -> {
                    if (TextUtils.isEmpty(charSequence.toString().trim())) {
                        showHideFragment(mHistoryFragment, mSuggestFragment);
                    } else {
                        showHideFragment(mSuggestFragment, mHistoryFragment);
                        mHandler.postDelayed(() -> mSuggestFragment.loadSuggest(charSequence.toString()), 150);
                    }
                });
        mBinding.ivPop.setOnClickListener(this);
        mBinding.ivSpeech.setOnClickListener(this);
        mBinding.etKeyword.setOnFocusChangeListener(this);
        suggestObservable.subscribe();
        mBinding.etKeyword.setOnEditorActionListener(this);
        RxView.clicks(mBinding.tvSearch)
                .doOnSubscribe(this)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> preSearch());
    }

    @Override
    public void initData() {

        if (findFragment(SearchHistoryFragment.class) == null) {
            mHistoryFragment = new SearchHistoryFragment();
            mHistoryFragment.setSearchListener(this);
            mSuggestFragment = new SearchSuggestFragment();
            mSuggestFragment.setSearchListener(this);
            loadMultipleRootFragment(R.id.fl_container, 0, mHistoryFragment, mSuggestFragment);
        } else {
            mHistoryFragment = findFragment(SearchHistoryFragment.class);
            mSuggestFragment = findFragment(SearchSuggestFragment.class);
        }

        showSoftInput(mBinding.etKeyword);
        if (mHotword != null) {
            mBinding.etKeyword.setHint(mHotword);
        } else {
            mViewModel.getHotWords();
        }
    }

    @Override
    public boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_pop) {
            pop();
        } else if (id == R.id.iv_speech) {
            new RxPermissions(this).request(new String[]{Manifest.permission.RECORD_AUDIO})
                    .subscribe(granted -> {
                        if (granted) {
                            try {
                                mIat.startListening(mRecognizerListener);
                            } catch (Exception e) {
                                ToastUtil.showToast(ToastUtil.LEVEL_E, "语音功能暂时不支持在PC端使用");
                            }
                        } else {
                            ToastUtil.showToast("请允许应用使用麦克风权限");
                        }
                    });
        }
    }

    @Override
    public void onSearch(String keyword) {
        hideSoftInput();
        suggestDisposable.dispose();
        mBinding.etKeyword.setText(keyword);
        suggestObservable.subscribe();
        search(keyword);
    }

    private void search(String keyword) {
        SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
        searchHistoryBean.setKeyword(keyword);
        searchHistoryBean.setDatatime(System.currentTimeMillis());
        mViewModel.insertHistory(searchHistoryBean);
        //更新显示历史搜索记录
        mBinding.etKeyword.clearFocus();

        Object navigation = mRouter.build(Constants.Router.Home.F_SEARCH_RESULT)
                .withString(KeyCode.Home.KEYWORD, keyword).navigation();
        if (null != navigation)
            ((BaseFragment) getTopChildFragment()).start((ISupportFragment) navigation);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (getTopChildFragment() instanceof SearchResultFragment) {
            ((BaseFragment) getTopChildFragment()).pop();
            showHideFragment(mHistoryFragment, mSuggestFragment);
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
        mViewModel.getHotWordsEvent().observe(this, hotWords ->
                mBinding.etKeyword.setHint(hotWords.get(0).getSearchword()));
        mViewModel.getInsertHistoryEvent().observe(this, bean -> mHistoryFragment.refreshHistory());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            preSearch();
        }
        return false;
    }

    private void preSearch() {
        if (getTopChildFragment() instanceof SearchResultFragment) {
            return;
        }
        if (mBinding.etKeyword.getText().toString().trim().length() != 0) {
            onSearch(mBinding.etKeyword.getText().toString());
        } else if (mBinding.etKeyword.getHint().toString().length() != 0) {
            mBinding.etKeyword.setText(mBinding.etKeyword.getHint());
            onSearch(mBinding.etKeyword.getHint().toString());
        } else {
            ToastUtil.showToast("请输入要搜索的关键词");
        }

    }


    @Override
    protected boolean enableLazy() {
        return false;
    }


    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        KeyboardUtils.hideSoftInput(mBinding.etKeyword);
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = code -> {
        //请点击网址https://www.xfyun.cn/document/error-code查询解决方案
        Log.d(TAG, "SpeechRecognizer init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            ToastUtil.showToast("初始化失败，错误码：" + code);
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            hideSoftInput();
            new XPopup.Builder(mActivity).popupAnimation(PopupAnimation.NoAnimation)
                    .dismissOnTouchOutside(false).setPopupCallback(new SimpleCallback() {
                @Override
                public void onCreated() {
                    super.onCreated();
                    vDialog = mSpeechPopup.getPopupImplView();
                }

                @Override
                public void onShow() {
                    super.onShow();
                    vDialog.findViewById(R.id.lav_speech).setVisibility(View.VISIBLE);
                    vDialog.findViewById(R.id.lav_loading).setVisibility(View.GONE);
                }

                @Override
                public void onDismiss() {
                    super.onDismiss();
                    mIat.cancel();
                }
            }).asCustom(mSpeechPopup).show();
        }

        @Override
        public void onError(SpeechError error) {
            Log.d(TAG, "onError: " + error);
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (error.getErrorCode() == 10118) {
                ToastUtil.showToast(error.getErrorDescription());
            }
            mSpeechPopup.dismiss();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            vDialog.findViewById(R.id.lav_speech).setVisibility(View.GONE);
            vDialog.findViewById(R.id.lav_loading).setVisibility(View.VISIBLE);
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
            mSpeechPopup.dismiss();
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    private void printResult(RecognizerResult results) {
        String text = SpeechUtil.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            stringBuilder.append(mIatResults.get(key));
        }
        mBinding.etKeyword.setText(stringBuilder.toString());
        mBinding.etKeyword.setSelection(mBinding.etKeyword.length());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mIat.destroy();
        } catch (Exception e) {
            Log.w(TAG, "由于讯飞语音没有提供x86 so文件所以直接捕获此异常");
        }
    }
}
