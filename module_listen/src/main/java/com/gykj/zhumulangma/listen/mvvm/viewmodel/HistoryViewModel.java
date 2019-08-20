package com.gykj.zhumulangma.listen.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gykj.zhumulangma.common.bean.PlayHistoryBean;
import com.gykj.zhumulangma.common.dao.PlayHistoryBeanDao;
import com.gykj.zhumulangma.common.event.SingleLiveEvent;
import com.gykj.zhumulangma.common.mvvm.model.BaseModel;
import com.gykj.zhumulangma.common.mvvm.model.ZhumulangmaModel;
import com.gykj.zhumulangma.common.mvvm.viewmodel.BaseViewModel;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Author: Thomas.
 * Date: 2019/8/20 13:56
 * Email: 1071931588@qq.com
 * Description:
 */
public class HistoryViewModel extends BaseViewModel<ZhumulangmaModel> {
    private SingleLiveEvent<List<PlayHistoryBean>> mHistorysSingleLiveEvent;
    private static final int PAGESIZE=20;
    private int curPage=1;
    public HistoryViewModel(@NonNull Application application, ZhumulangmaModel model) {
        super(application, model);
    }

    public void getHistory(){
       mModel.listDesc(PlayHistoryBean.class,curPage,PAGESIZE,PlayHistoryBeanDao.Properties.Datatime)
               .subscribe(playHistoryBeans -> {
           curPage++;
           getHistorySingleLiveEvent().postValue(playHistoryBeans);
       }, e->e.printStackTrace());
    }
    public void clear(){
        mModel.clear(PlayHistoryBean.class).subscribe();
    }
    public SingleLiveEvent<List<PlayHistoryBean>> getHistorySingleLiveEvent() {
        return mHistorysSingleLiveEvent=createLiveData(mHistorysSingleLiveEvent);
    }
}
