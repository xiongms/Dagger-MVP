package com.xiongms.libcore.network.rx;

import io.reactivex.Observable;

/**
 * @author xiongms
 * @time 2018-08-17 11:57
 */
public class RxResultHelper {


    /**
     * @param observable
     * @param <R>        处理请求进行转化,返回http结果
     */
    /**
     * 网络请求预处理
     * @param lifecycle 绑定生命周期对象
     * @param observable 请求任务
     * @param <R> 返回值类型
     * @return
     */
    public static <R> Observable<R> getHttpObservable(Object lifecycle, Observable<R> observable) {
        return observable
                .compose(RxUtils.<R>schedulersTransformer())
                .compose(RxUtils.<R>bindToLifecycle(lifecycle))
                .compose(RxUtils.<R>handleResult())
                .retryWhen(RxUtils.handleRetryWhen());
    }
}
