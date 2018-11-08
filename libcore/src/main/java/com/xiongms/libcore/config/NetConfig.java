package com.xiongms.libcore.config;

/**
 * 
 * @author xiongms
 * @time 2018-08-16 14:19
 */
public interface NetConfig {

    int NET_TIME_OUT_CONNECT = 60;
    int NET_TIME_OUT_READ = 60;
    int NET_TIME_OUT_WRITE = 60;

    int NET_MAX_RETRY_TIMES = 2;
}
