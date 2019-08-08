package com.gykj.zhumulangma.common;

import com.gykj.zhumulangma.common.net.http.RetryWithNewToken;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    static String mString;

    private static Observable t() {

        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mString = "aa";
                mString = "11";

            }
        }).doOnDispose(new Action() {

            @Override
            public void run() throws Exception {
                System.out.println(mString);
            }
        });
    }

    @Test
    public void test() {
        Single<Integer> just = Single.just(1).doOnSuccess(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });
        Single<String> just1 = Single.just("2").doOnSuccess(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
        List<Single> singles=new ArrayList<>();
        singles.add(just);
        singles.add(just1);
        Single.zipArray(new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] objects) throws Exception {
                return objects[0].toString()+objects[1].toString();
            }
        },singles.toArray(new Single[]{}))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        System.out.println(o);

                    }
                });
    }
    String s;
    @Test
    public void test1(){
        Observable<String> stringObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {

                    emitter.onNext(System.currentTimeMillis() + "");
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        })
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String aVoid) throws Exception {
                        System.out.println(aVoid);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println(11);
                    }
                }).retryWhen(new RetryWithNewToken());

        stringObservable.subscribe();
    }
}