package com.xiongms.libcore.da;

import android.util.Log;

import com.google.gson.JsonObject;
import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.utils.DateUtil;
import com.xiongms.libcore.utils.JsonUtil;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 数据分析
 * @author xiongms
 * @time 2018-09-11 17:03
 */
public class DataAnalysis {

    private static final String TAG = DataAnalysis.class.getSimpleName();

    private static DataAnalysis dataAnalysis;

    private DataAnalysisServiceApi dataAnalysisServiceApi;

    public static DataAnalysis getInstance() {
        if(dataAnalysis == null) {
            dataAnalysis = new DataAnalysis();
        }
        return dataAnalysis;
    }

    public DataAnalysis() {
        dataAnalysisServiceApi = BaseApplication.getInstance().getEnv().rqbRetrofit().create(DataAnalysisServiceApi.class);
    }

    public void upload(Event event) {
        JsonObject jsonObject = JsonUtil.toJsonElement(event).getAsJsonObject();

        dataAnalysisServiceApi.addClickEvent(jsonObject)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BaseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseBean baseBean) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 报表事件统计
     */
    public static void onEvent(int eventType, HashMap<String, String> params) {
        String time = DateUtil.getCurrentDate(DateUtil.dateFormatYMDHMS);

        Event event = new Event();
        event.setPosition(eventType);
        event.setSourceData(JsonUtil.toJson(params));
        event.setTime(time);

        String json = JsonUtil.toJson(event);
        Log.d(TAG, json);

        DataAnalysis.getInstance().upload(event);
    }
}
