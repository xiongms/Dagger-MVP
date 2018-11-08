package com.xiongms.libcore.di.qualifiers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * retrofit默认baseurl
 * @author xiongms
 * @time 2018-11-08 15:41
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultBaseUrl {
}
