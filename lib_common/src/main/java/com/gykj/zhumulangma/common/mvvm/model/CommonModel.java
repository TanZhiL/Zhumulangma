package com.gykj.zhumulangma.common.mvvm.model;

import android.app.Application;

import com.gykj.zhumulangma.common.AppHelper;
import com.gykj.zhumulangma.common.net.CommonService;
import com.gykj.zhumulangma.common.net.RetrofitManager;
import com.gykj.zhumulangma.common.net.http.RxAdapter;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Author: Thomas.
 * Date: 2019/7/31 17:27
 * Email: 1071931588@qq.com
 * Description:
 */
public class CommonModel extends BaseModel {
    private CommonService mCommonService;

    public CommonModel(Application application) {
        super(application);
        mCommonService = RetrofitManager.getInstance().getCommonService();
    }


    /**
     * 条件查询
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Observable<List<T>> list(Class<T> cls, int page, int pagesize, Property asc, Property desc,
                                        WhereCondition cond, WhereCondition... condMore){

        return  Observable.create(emitter -> {
            List<T> list = new ArrayList<>();
            try {

                QueryBuilder<T> builder = AppHelper.getDaoSession().queryBuilder(cls);
                if(page!=0&&pagesize!=0){
                    builder=builder.offset((page-1)*pagesize).limit(pagesize);
                }
                if(cond!=null){
                    builder=builder.where(cond,condMore);
                }
                if(asc!=null){
                    builder=builder.orderAsc(asc);
                }
                if(desc!=null){
                    builder=builder.orderDesc(desc);

                }
                list=builder.list();
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onNext(list);
            emitter.onComplete();
        }).compose(RxAdapter.schedulersTransformer());

    }
    public <T> Observable<List<T>> listRaw(Class<T> cls,String where,String... selectionArgs){
        Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(ObservableEmitter<List<T>> emitter) throws Exception {
                List<T> list = new ArrayList<>();
                try {
                    list= AppHelper.getDaoSession().queryRaw(cls, where, selectionArgs);
                } catch (Exception e) {
                    emitter.onError(e);
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        });



        return list(cls,0,0,null,null,null);

    }
    public <T> Observable<List<T>> list(Class<T> cls){

        return list(cls,0,0,null,null,null);

    }

    public <T> Observable<List<T>> list(Class<T> cls, int page, int pagesize){

        return list(cls,page,pagesize,null,null,null);

    }
    public <T> Observable<List<T>> listAsc(Class<T> cls, int page, int pagesize, Property asc){

        return list(cls,page,pagesize,asc,null,null);

    }
    public <T> Observable<List<T>> listDesc(Class<T> cls, int page, int pagesize, Property desc){

        return list(cls,page,pagesize,null,desc,null);

    }
    public <T> Observable<List<T>> listDesc(Class<T> cls, int page, int pagesize,Property desc,
                                            WhereCondition cond ,WhereCondition... condMore){

        return list(cls,page,pagesize,null,desc,cond,condMore);

    }
    public <T> Observable<List<T>> list(Class<T> cls,  WhereCondition cond, WhereCondition... condMore){

        return list(cls,0,0,null,null,cond,condMore);

    }
    /**
     * 清空所有记录
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Observable<Boolean> clearAll(Class<T> cls){
        return  Observable.create(emitter -> {
            try {
                AppHelper.getDaoSession().deleteAll(cls);
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onNext(true);
            emitter.onComplete();
        }).compose(RxAdapter.schedulersTransformer());


    }
    /**
     * 删除一条记录
     * @param <T>
     * @return
     */
    public <T,K> Observable<Boolean> remove(Class<T> cls,K key){
        return  Observable.create(emitter -> {
            try {
                AbstractDao<T, K> tkAbstractDao = (AbstractDao<T, K>) AppHelper.getDaoSession().getDao(cls);
                tkAbstractDao.deleteByKey(key);
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onNext(true);
            emitter.onComplete();
        }).compose(RxAdapter.schedulersTransformer());


    }
    /**
     * 插入一条记录
     * @param entity
     * @param <T>
     * @return
     */
    public  <T> Observable<T> insert(T entity){

        return  Observable.create(emitter -> {
            try {
                AppHelper.getDaoSession().insertOrReplace(entity);
            } catch (Exception e) {
                emitter.onError(e);
            }

            emitter.onNext(entity);
            emitter.onComplete();

        }).compose(RxAdapter.schedulersTransformer());

    }
}
