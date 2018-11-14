package com.xiongms.libcore.network.rx;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.network.exception.ApiException;
import com.xiongms.libcore.network.exception.ExceptionCont;
import com.xiongms.libcore.utils.JsonUtil;
import com.xiongms.libcore.utils.ToastUtil;
import com.xiongms.libcore.utils.TokenBus;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.HttpException;

/**
 * Retrofit请求相关的工具类
 * @author xiongms
 * @time 2018-08-28 17:11
 */
public class RxUtils {

    private static final String TAG = RxUtils.class.getSimpleName();

    /**
     * 获取json格式requestbody
     * @param obj
     * @return
     */
    public static RequestBody createJSONRequestBody(Object obj) {
        Gson gson = BaseApplication.getInstance().getEnv().gson();
        String json = gson.toJson(obj);

        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
    }

    /**
     * 生命周期绑定
     *
     * @param lifecycle Activity
     */
    public static  ObservableTransformer bindToLifecycle(Object lifecycle) {
        if (lifecycle instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) lifecycle).bindUntilEvent(ActivityEvent.DESTROY);
        } else if (lifecycle instanceof RxFragment) {
            return ((RxFragment) lifecycle).bindUntilEvent(FragmentEvent.DESTROY_VIEW);
        } else {
            return new ObservableTransformer() {
                @Override
                public ObservableSource apply(Observable upstream) {
                    return upstream;
                }
            };
        }
    }



    /**
     * 线程调度器
     * 设置耗时操作在io线程
     * 更新ui在主线程
     */
    public static <R> ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * 检查返回值，如果token失效，抛出异常
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> handleResult() {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(Observable<T> tObservable) {
                return tObservable.flatMap(new Function<T, ObservableSource<T>>() {

                    @Override
                    public ObservableSource<T> apply(T resultEntity) throws Exception {
                        BaseBean baseBean = null;
                        if (resultEntity instanceof String) {
                            baseBean = JsonUtil.fromJson((String) resultEntity, BaseBean.class);
                        } else if (resultEntity instanceof BaseBean) {
                            baseBean = (BaseBean) resultEntity;
                        }

                        if (baseBean != null && ExceptionCont.TOKEN_ERROR == baseBean.getCode()) {
                            // 当前返回值为BaseBean格式，并且code为token失效，需要抛出异常
                            return Observable.error(new ApiException(baseBean.getMsg(), baseBean.getCode()));
                        }

                        return Observable.just(resultEntity);
                    }
                });
            }
        };
    }

    /**
     * 处理token无效
     * @return
     */
    public static Function<Observable<Throwable>, ObservableSource<?>> handleRetryWhen() {
        return new Function<Observable<Throwable>, ObservableSource<?>>() {

            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) {
                        Log.d(TAG, "异常重试:" + throwable);
                        if (throwable instanceof ApiException) {
                            ApiException apiException = (ApiException) throwable;
                            if (apiException.getCode() == ExceptionCont.TOKEN_ERROR) {
                                ToastUtil.show(apiException.getMessage());
                                return TokenBus.getInstance().getNetTokenLocked();
                            }
                        }
                        return Observable.error(throwable);
                    }
                });
            }
        };
    }

    public static <T> T getResultData(BaseBean<T> baseBean) throws ApiException{
        if (!baseBean.isSucc()) {
            throw new ApiException(baseBean.getMsg(), baseBean.getCode());
        }
        return baseBean.getData();
    }

    public static ApiException getResultException(Throwable e){
        ApiException ex;
        if(e instanceof ApiException) {
            return (ApiException)e;
        } else if (e instanceof HttpException) {             //HTTP错误
            HttpException httpExc = (HttpException) e;
            ex = new ApiException("网络错误", httpExc.code());
        } else if (e instanceof UnknownHostException) {             //HTTP错误
            ex = new ApiException("网络连接失败", ExceptionCont.EXCEPTION_CONNECT_ERROR);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException || e instanceof MalformedJsonException) {  //解析数据错误
            ex = new ApiException("解析错误", ExceptionCont.EXCEPTION_PARSE_ERROR);
        } else if (e instanceof ConnectException) {//连接网络错误
            ex = new ApiException("连接失败", ExceptionCont.EXCEPTION_CONNECT_ERROR);
        } else if (e instanceof NetworkOnMainThreadException) {//连接网络错误
            ex = new ApiException("不能在UI线程访问网络", ExceptionCont.EXCEPTION_CONNECT_ERROR);
        } else if (e instanceof SocketTimeoutException || e instanceof TimeoutException) {//网络超时
            ex = new ApiException("网络超时", ExceptionCont.EXCEPTION_TIME_OUT_ERROR);
        } else {  //未知错误
            ex = new ApiException("未知错误", ExceptionCont.EXCEPTION_UNKNOWN_ERROR);
        }
        return ex;
    }
}
