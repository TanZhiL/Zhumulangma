package com.gykj.zhumulangma.common.mvvm.model;

import android.app.Application;
import android.database.Cursor;

import com.gykj.zhumulangma.common.db.DBManager;
import com.gykj.zhumulangma.common.net.NetManager;
import com.gykj.zhumulangma.common.net.RxAdapter;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Author: Thomas.
 * <br/>Date: 2019/7/31 17:27
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:通用Model
 */
public class BaseModel {
    protected NetManager mNetManager;
    protected Application mApplication;
    protected DBManager mDBManager;

    public BaseModel(Application application) {
        mApplication = application;
        mNetManager = NetManager.getInstance();
        mDBManager = DBManager.getInstance(mApplication);
    }

    /**
     * 从网络中获取ResponseBody
     *
     * @param url
     * @return
     */
    public Observable<ResponseBody> getCommonBody(String url) {
        return mNetManager.getCommonService().getCommonBody(url)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    /**
     * 条件查询
     *
     * @param cls
     * @param <T>
     * @return
     */


    public <T> Observable<List<T>> list(Class<T> cls) {
        return mDBManager.list(cls, 0, 0, null, null, null)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public <T> Observable<List<T>> list(Class<T> cls, int page, int pagesize) {
        return mDBManager.list(cls, page, pagesize, null, null, null)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public <T> Observable<List<T>> listDesc(Class<T> cls, int page, int pagesize, Property desc) {
        return mDBManager.list(cls, page, pagesize, null, desc, null)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public <T> Observable<List<T>> listDesc(Class<T> cls, int page, int pagesize, Property desc,
                                            WhereCondition cond, WhereCondition... condMore) {
        return mDBManager.list(cls, page, pagesize, null, desc, cond, condMore)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());

    }

    public <T> Observable<List<T>> list(Class<T> cls, WhereCondition cond, WhereCondition... condMore) {
        return mDBManager.list(cls, 0, 0, null, null, cond, condMore)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }


    public Observable<Cursor> rawQuery(String sql, String[] selectionArgs) {
        return mDBManager.rawQuery(sql, selectionArgs)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    /**
     * 清空所有记录
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Observable<Boolean> clearAll(Class<T> cls) {
        return mDBManager.clearAll(cls)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    /**
     * 删除一条记录
     *
     * @param <T>
     * @return
     */
    public <T, K> Observable<Boolean> remove(Class<T> cls, K key) {
        return mDBManager.remove(cls, key)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    /**
     * 更新或插入一条记录
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> Observable<T> insert(T entity) {
        return mDBManager.insert(entity)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<String> getSPString(String key) {
        return mDBManager.getSPString(key)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<String> getSPString(String key, String defaultValue) {
        return mDBManager.getSPString(key, defaultValue)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Integer> getSPInt(String key) {
        return mDBManager.getSPInt(key)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Integer> getSPInt(String key, int defaultValue) {
        return mDBManager.getSPInt(key, defaultValue)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Long> getSPLong(String key) {
        return mDBManager.getSPLong(key)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Long> getSPLong(String key, long defaultValue) {
        return mDBManager.getSPLong(key, defaultValue)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Boolean> putSP(String key, String value) {
        return mDBManager.putSP(key, value)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Boolean> putSP(String key, int value) {
        return mDBManager.putSP(key, value)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Boolean> putSP(String key, Long value) {
        return mDBManager.putSP(key, value)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }


    public Observable<String> getCacheString(String key) {
        return mDBManager.getCacheString(key)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<String> getCacheString(String key, String defaultValue) {
        return mDBManager.getCacheString(key, defaultValue)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Integer> getCacheInt(String key) {
        return mDBManager.getCacheInt(key)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Integer> getCacheInt(String key, int defaultValue) {
        return mDBManager.getCacheInt(key, defaultValue)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Long> getCacheLong(String key) {
        return mDBManager.getCacheLong(key)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Long> getCacheLong(String key, long defaultValue) {
        return mDBManager.getCacheLong(key, defaultValue)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Boolean> putCache(String key, String value) {
        return mDBManager.putCache(key, value)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Boolean> putCache(String key, int value) {
        return mDBManager.putCache(key, value)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }

    public Observable<Boolean> putCache(String key, Long value) {
        return mDBManager.putCache(key, value)
                .compose(RxAdapter.exceptionTransformer())
                .compose(RxAdapter.schedulersTransformer());
    }
}
