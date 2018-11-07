package com.xiongms.libcore.network.exception;

/**
 * 
 * @author xiongms
 * @time 2018-08-17 12:00
 */
public class ResponseThrowable extends Exception {
    public int code;
    public String message;

    public ResponseThrowable(String msg, int code) {
        super(msg);
        this.message = msg;
        this.code = code;
    }
}
