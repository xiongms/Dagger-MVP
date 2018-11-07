package com.xiongms.libcore.network.exception;

/**
 * 
 * @author xiongms
 * @time 2018-08-17 11:56
 */
public class ExceptionCont {


    /**
     * 约定异常 这个具体规则需要与服务端或者领导商讨定义
     */

    /**
     * 登录无效
     */
    public static final int TOKEN_ERROR = 401;

    /**
     * 服务器异常
     */
    public static final int SERVER_ERROR = 501;

    /**
     * 解析错误
     */
    public static final int EXCEPTION_PARSE_ERROR = 40001;
    /**
     * 连接错误
     */
    public static final int EXCEPTION_CONNECT_ERROR = 40001;
    /**
     * 超时
     */
    public static final int EXCEPTION_TIME_OUT_ERROR = 40001;
    /**
     * 未知错误
     */
    public static final int EXCEPTION_UNKNOWN_ERROR = 40001;


}

