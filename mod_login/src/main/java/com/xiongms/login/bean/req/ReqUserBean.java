package com.xiongms.login.bean.req;

/**
 *
 */
public class ReqUserBean {

    String phone;
    String code;

    public ReqUserBean(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }
}
