package com.gykj.zhumulangma.common.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.util.CacheDoubleUtils;
import com.blankj.utilcode.util.SPUtils;
import com.gykj.zhumulangma.common.App;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author: Thomas.<br/>
 * Date: 2019/11/6 14:22<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:数据库,SP,缓存管理类
 */
public class DBManager {
    private static final boolean LOGGER = false;
    private static volatile DBManager instance;
    private DaoSession mSession;
    private SPUtils mSPUtils;
    private CacheDoubleUtils mCacheDoubleUtils;

    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    private DBManager() {
        QueryBuilder.LOG_SQL = LOGGER;
        QueryBuilder.LOG_VALUES = LOGGER;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(App.getInstance(), "zhumulangma.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mSession = daoMaster.newSession();
        mSPUtils = SPUtils.getInstance();
        mCacheDoubleUtils = CacheDoubleUtils.getInstance();
    }

    /**
     * 条件查询
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Observable<List<T>> list(Class<T> cls) {
        return list(cls, 0, 0, null, null, null);
    }

    public <T> Observable<List<T>> list(Class<T> cls, int page, int pagesize) {
        return list(cls, page, pagesize, null, null, null);
    }

    public <T> Observable<List<T>> listAsc(Class<T> cls, int page, int pagesize, Property asc) {
        return list(cls, page, pagesize, asc, null, null);
    }

    public <T> Observable<List<T>> listDesc(Class<T> cls, int page, int pagesize, Property desc) {
        return list(cls, page, pagesize, null, desc, null);
    }

    public <T> Observable<List<T>> listDesc(Class<T> cls, int page, int pagesize, Property desc,
                                            WhereCondition cond, WhereCondition... condMore) {
        return list(cls, page, pagesize, null, desc, cond, condMore);
    }

    public <T> Observable<List<T>> list(Class<T> cls, WhereCondition cond, WhereCondition... condMore) {
        return list(cls, 0, 0, null, null, cond, condMore);
    }

    public <T> Observable<List<T>> list(Class<T> cls, int page, int pagesize, Property asc, Property desc,
                                        WhereCondition cond, WhereCondition... condMore) {
        return Observable.create(emitter -> {
            try {

                QueryBuilder<T> builder = mSession.queryBuilder(cls);
                if (page != 0 && pagesize != 0) {
                    builder = builder.offset((page - 1) * pagesize).limit(pagesize);
                }
                if (cond != null) {
                    builder = builder.where(cond, condMore);
                }
                if (asc != null) {
                    builder = builder.orderAsc(asc);
                }
                if (desc != null) {
                    builder = builder.orderDesc(desc);

                }
                emitter.onNext(builder.list());
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }

        });

    }

    public Observable<Cursor> rawQuery(String sql, String[] selectionArgs) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(mSession.getDatabase().rawQuery(sql, selectionArgs));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    /**
     * 清空所有记录
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Observable<Boolean> clearAll(Class<T> cls) {
        return Observable.create(emitter -> {
            try {
                mSession.deleteAll(cls);
                emitter.onNext(true);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });


    }

    /**
     * 删除一条记录
     *
     * @param <T>
     * @return
     */
    public <T, K> Observable<Boolean> remove(Class<T> cls, K key) {
        return Observable.create(emitter -> {
            try {
                AbstractDao<T, K> tkAbstractDao = (AbstractDao<T, K>) mSession.getDao(cls);
                tkAbstractDao.deleteByKey(key);
                emitter.onNext(true);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });


    }

    /**
     * 更新或插入一条记录
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> Observable<T> insert(T entity) {
        return Observable.create(emitter -> {
            try {
                mSession.insertOrReplace(entity);
                emitter.onNext(entity);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });

    }


    public Observable<String> getSPString(String key) {
        return getSPString(key, "");
    }

    public Observable<String> getSPString(String key, String defaultValue) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(mSPUtils.getString(key, defaultValue));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Integer> getSPInt(String key) {
        return getSPInt(key, -1);
    }

    public Observable<Integer> getSPInt(String key, int defaultValue) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(mSPUtils.getInt(key, defaultValue));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Long> getSPLong(String key) {
        return getSPLong(key, -1L);
    }

    public Observable<Long> getSPLong(String key, long defaultValue) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(mSPUtils.getLong(key, defaultValue));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<String> putSP(String key, String value) {
        return Observable.create(emitter -> {
            try {
                mSPUtils.put(key, value, true);
                emitter.onNext(value);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Integer> putSP(String key, int value) {
        return Observable.create(emitter -> {
            try {
                mSPUtils.put(key, value, true);
                emitter.onNext(value);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Long> putSP(String key, Long value) {
        return Observable.create(emitter -> {
            try {
                mSPUtils.put(key, value, true);
                emitter.onNext(value);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });

    }


    public Observable<String> getCacheString(String key) {
        return getCacheString(key, "");
    }

    public Observable<String> getCacheString(String key, String defaultValue) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(mCacheDoubleUtils.getString(key, defaultValue));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Integer> getCacheInt(String key) {
        return getCacheInt(key, -1);
    }

    public Observable<Integer> getCacheInt(String key, int defaultValue) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(Integer.valueOf(mCacheDoubleUtils.getString(key, String.valueOf(defaultValue))));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Long> getCacheLong(String key) {
        return getCacheLong(key, -1L);
    }

    public Observable<Long> getCacheLong(String key, long defaultValue) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(Long.valueOf(mCacheDoubleUtils.getString(key, String.valueOf(defaultValue))));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<String> putCache(String key, String value) {
        return Observable.create(emitter -> {
            try {
                mCacheDoubleUtils.put(key, value);
                emitter.onNext(value);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Integer> putCache(String key, int value) {
        return Observable.create(emitter -> {
            try {
                mCacheDoubleUtils.put(key, value);
                emitter.onNext(value);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Long> putCache(String key, long value) {
        return Observable.create(emitter -> {
            try {
                mCacheDoubleUtils.put(key, value);
                emitter.onNext(value);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
