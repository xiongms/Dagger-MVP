package com.xiongms.libcore.network.interfaces;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public interface IGlobalConfig {

    /**
     * 获取Get请求的公共参数
     * @return
     */
    Map<String, String> commonGetParams();

    Map<String, String> commonPostParams();
}
