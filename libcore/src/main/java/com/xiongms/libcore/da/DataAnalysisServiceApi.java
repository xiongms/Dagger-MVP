package com.xiongms.libcore.da;

import com.google.gson.JsonObject;
import com.xiongms.libcore.bean.BaseBean;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 *
 */
public interface DataAnalysisServiceApi {

    @POST("/Api/DingdangStatistic/AddClickLog")
    Observable<BaseBean> addClickEvent(@Body JsonObject jsonObject);
}
