package com.gykj.zhumulangma.home.fragment;


import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.SearchHistoryBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.common.BaseActivityEvent;
import com.gykj.zhumulangma.common.mvvm.view.BaseFragment;
import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.common.util.SpeechUtil;
import com.gykj.zhumulangma.common.util.ToastUtil;
import com.gykj.zhumulangma.home.R;
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
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import me.yokeyword.fragmentation.ISupportFragment;

@Route(path = AppConstants.Router.Home.F_SEARCH)
public class SearchFragment extends BaseMvvmFragment<SearchViewModel> implements
        View.OnClickListener, SearchHistoryFragment.onSearchListener, View.OnFocusChangeListener,
        TextView.OnEditorActionListener, TextWatcher, SearchSuggestFragment.onSearchListener{

    private EditText etKeyword;
    @Autowired(name = KeyCode.Home.HOTWORD)
    public String hotword;
    private SearchSuggestFragment mSuggestFragment;
    private SpeechRecognizer mIat;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private Handler mHandler = new Handler();
    private SpeechPopup mSpeechPopup;
    private View vDialog;
    public SearchFragment() {

    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_search;
    }

    @Override
    protected void initView(View view) {
        if (StatusBarUtils.supportTransparentStatusBar()) {
            fd(R.id.cl_titlebar).setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        }
        etKeyword = fd(R.id.et_keyword);
    //    UiUtil.setEditTextInhibitInputSpace(etKeyword);
     //   UiUtil.setEditTextInhibitInputSpeChat(etKeyword);
        mSuggestFragment = (SearchSuggestFragment) ARouter.getInstance()
                .build(AppConstants.Router.Home.F_SEARCH_SUGGEST).navigation();
        mSuggestFragment.setSearchListener(this);
        etKeyword.postDelayed(() -> {
            SearchHistoryFragment historyFragment = new SearchHistoryFragment();
            historyFragment.setSearchListener(SearchFragment.this);
            loadRootFragment(R.id.fl_container, historyFragment);

            KeyboardUtils.showSoftInput(etKeyword);
        }, 300);
        //不支持x86
        mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);
        try {
            //返回json类型
            mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        } catch (Exception e) {
            e.printStackTrace();
        }
        mSpeechPopup=new SpeechPopup(mContext);
    }

    @Override
    public void initListener() {
        super.initListener();
        fd(R.id.iv_pop).setOnClickListener(this);
        fd(R.id.iv_speech).setOnClickListener(this);
        etKeyword.setOnFocusChangeListener(this);
        etKeyword.setOnEditorActionListener(this);
        etKeyword.addTextChangedListener(this);
        addDisposable(RxView.clicks(fd(R.id.tv_search))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> preSearch()));
    }

    @Override
    public void initData() {
        if (hotword != null) {
            etKeyword.setHint(hotword);
        } else {
            mViewModel._getHotWords();
        }
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_pop) {
            pop();
        } else if (id == R.id.iv_speech) {
            new RxPermissions(this).requestEach(new String[]{Manifest.permission.RECORD_AUDIO})
                    .subscribe(permission -> {
                        if (permission.granted) {
                            try {
                                mIat.startListening(mRecognizerListener);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtil.showToast("请允许应用使用麦克风权限");
                        }
                    });
        }
    }

    @Override
    public void onSearch(String keyword) {
        etKeyword.removeTextChangedListener(this);
        etKeyword.setText(keyword);
        etKeyword.addTextChangedListener(this);
        search(keyword);
    }

    private void search(String keyword) {
        SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
        searchHistoryBean.setKeyword(keyword);
        searchHistoryBean.setDatatime(System.currentTimeMillis());
        mViewModel.insertHistory(searchHistoryBean);
        etKeyword.clearFocus();
        hideSoftInput();
        if (getTopChildFragment() instanceof SearchSuggestFragment) {
            ((BaseFragment) getTopChildFragment()).pop();
        }
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_SEARCH_RESULT)
                .withString(KeyCode.Home.KEYWORD, keyword).navigation();
        if (null != navigation)
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
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            preSearch();
        }
        return false;
    }

    private void preSearch() {
        if (getTopChildFragment() instanceof SearchResultFragment) {
            return;
        }
        if (etKeyword.getText().toString().trim().length() != 0) {
            search(etKeyword.getText().toString());
        } else if (etKeyword.getHint().toString().length() != 0) {
            etKeyword.setText(etKeyword.getHint());
            search(etKeyword.getHint().toString());
        } else {
            ToastUtil.showToast("请输入要搜索的关键词");
        }

    }


    @Override
    protected boolean lazyEnable() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mIat.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        KeyboardUtils.hideSoftInput(etKeyword);
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.SHOW_GP));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            if (getTopChildFragment() instanceof SearchSuggestFragment) {
                ((BaseFragment) getTopChildFragment()).pop();
            }
        } else {
            if (!(getTopChildFragment() instanceof SearchSuggestFragment)) {
                ((BaseFragment) getTopChildFragment()).start(mSuggestFragment);
                mHandler.postDelayed(() -> mSuggestFragment.loadSuggest(s.toString()), 100);

            } else {
                mSuggestFragment.loadSuggest(s.toString());
            }
        }
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
            new XPopup.Builder(mContext).popupAnimation(PopupAnimation.NoAnimation)
                    .dismissOnTouchOutside(false).setPopupCallback(new SimpleCallback(){
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
            Log.d(TAG, "onError: "+error);
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
           if( error.getErrorCode()==10118){
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
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        Log.d(TAG, "printResult: "+resultBuffer);
        etKeyword.setText(resultBuffer.toString());
        etKeyword.setSelection(etKeyword.length());
    }
    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().post(new BaseActivityEvent<>(EventCode.MainCode.HIDE_GP));
    }
}
