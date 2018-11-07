package com.xiongms.libcore.config;

/**
 * 
 * @author xiongms
 * @time 2018-08-16 14:19
 */
public interface NetConfig {

    String NET_DOMAIN_NAME = "domain_name";

    String NET_RQB_DOMAIN = "https://www.baidu.com/";

    int NET_TIME_OUT_CONNECT = 60;
    int NET_TIME_OUT_READ = 60;
    int NET_TIME_OUT_WRITE = 60;

    int NET_MAX_RETRY_TIMES = 2;
}
