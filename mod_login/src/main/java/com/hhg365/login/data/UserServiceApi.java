package com.hhg365.login.data;

import com.alibaba.android.arouter.facade.Postcard;
import com.google.gson.JsonObject;
import com.hhg365.login.bean.req.ReqUserBean;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.bean.Store;
import com.xiongms.libcore.bean.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 *
 */
public interface UserServiceApi {


    @POST("/api/Message/SendSSMS")
    Observable<BaseBean> sendSMS(@Body JsonObject json);

}
