package com.xiongms.libcore.network.rx;


import android.content.Context;

import com.google.gson.Gson;
import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.network.exception.ApiException;
import com.xiongms.libcore.network.exception.ExceptionCont;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * @author xiongms
 * @time 2018-08-17 11:57
 */
public class RxResultHelper {
    private static <T> ObservableTransformer<T, T> handleResult() {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(Observable<T> tObservable) {
                return tObservable.flatMap(new Function<T, ObservableSource<T>>() {

                    @Override
                    public ObservableSource<T> apply(T resultEntity) throws Exception {
                        BaseBean baseBean = null;
                        if (resultEntity instanceof String) {
                            Gson gson = BaseApplication.getInstance().getGson();
                            baseBean = gson.fromJson((String) resultEntity, BaseBean.class);
                        } else if (resultEntity instanceof BaseBean) {
                            baseBean = (BaseBean) resultEntity;
                        }
                        if (baseBean != null && ExceptionCont.TOKEN_ERROR == baseBean.getCode()) {
                            //添加重登录的处理，需要和服务器协商
                            return Observable.error(new ApiException(baseBean.getMsg(), baseBean.getCode()));
                        }

                        return Observable.just(resultEntity);
                    }
                });
            }
        };
    }

    /**
     * @param observable
     * @param <R>        处理请求进行转化,返回http结果
     */
    public static <R> Observable<R> getHttpObservable(Context context, Observable<R> observable) {
        return observable
                .compose(RxUtils.<R>schedulersTransformer())
                .compose(RxUtils.<R>bindToLifecycle(context))
                .compose(RxResultHelper.<R>handleResult())
                .retryWhen(RxUtils.handleRetryWhen());
    }
}
