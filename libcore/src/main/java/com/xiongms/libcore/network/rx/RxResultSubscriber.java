package com.xiongms.libcore.network.rx;


import com.google.gson.JsonParseException;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.network.exception.ExceptionCont;
import com.xiongms.libcore.network.exception.ApiException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * @author xiongms
 * @time 2018-08-17 11:59
 */
public abstract class RxResultSubscriber<T> implements Observer<BaseBean<T>> {

    private String msg;

    private int code;

    @Override
    public void onNext(BaseBean<T> t) {
        if (t.isSucc()) {
            try {
                success(t);
            } catch (Exception e) {
                e.printStackTrace();
                code = t.getCode();
                msg = "数据加载异常";
                error(code, msg);
            }
        } else {
            code = t.getCode();
            msg = t.getMsg();
            error(code, msg);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        ApiException apiException = RxUtils.getResultException(e);
        error(apiException.getCode(), apiException.getMessage());
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        start();
    }

    public abstract void start();

    public abstract void error(int code, String msg);

    public abstract void success(BaseBean<T> t);
}
