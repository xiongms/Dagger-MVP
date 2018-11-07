package com.xiongms.libcore.network.exception;


import java.io.IOException;

/**
 * 
 * @author xiongms
 * @time 2018-08-17 12:46
 */
public class ApiException extends IOException {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ApiException(String msg) {
        super(msg);
        this.message = msg;
    }

    public ApiException(String msg, int code) {
        super(msg);
        this.message = msg;
        this.code = code;
    }


}
