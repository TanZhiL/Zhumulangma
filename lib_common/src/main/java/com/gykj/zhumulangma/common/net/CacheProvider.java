package com.gykj.zhumulangma.common.net;

import com.gykj.zhumulangma.common.bean.BingBean;
import com.gykj.zhumulangma.common.net.dto.GitHubDTO;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;

/**
 * Author: Thomas.<br/>
 * Date: 2019/10/29 8:55<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:RxCache网络缓存
 */
public interface CacheProvider {

    @LifeCache(duration = 2,timeUnit = TimeUnit.MINUTES)
    Observable<GitHubDTO> getGitHub(Observable<GitHubDTO> observable);

    Observable<BingBean> getBing(Observable<BingBean> observable, EvictProvider evictProvider);
}
