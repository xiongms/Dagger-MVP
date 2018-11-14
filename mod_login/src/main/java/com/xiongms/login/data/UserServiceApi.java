package com.xiongms.login.data;

import com.google.gson.JsonObject;
import com.xiongms.libcore.bean.BaseBean;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 *
 */
public interface UserServiceApi {


    @POST("/api/Message/SendSSMS")
    Observable<String> sendSMS(@Body JsonObject json);

}
