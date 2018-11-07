package com.xiongms.libcore.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * activity管理工具
 * @author xiongms
 * @time 2018-08-16 11:14
 */
public class ActivityUtil {

    // 采用弱引用持有 Activity ，避免造成 内存泄露
    private WeakReference<Activity> sCurrentActivityWeakRef;

    private List<Activity> activityList = new LinkedList<Activity>();
    private static ActivityUtil instance;

    private ActivityUtil() {
    }

    /**
     * 单例模式中获取唯一的AbActivityManager实例.
     *
     * @return
     */
    public static ActivityUtil getInstance() {
        if (null == instance) {
            instance = new ActivityUtil();
        }
        return instance;
    }


    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<>(activity);
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    /**
     * 添加Activity到容器中.
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        synchronized (activityList) {
            activityList.add(activity);
        }
    }

    /**
     * 移除Activity从容器中.
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        synchronized (activityList) {
            activityList.remove(activity);
        }
    }

    /**
     * 遍历所有Activity并finish.
     */
    public void clearAllActivity() {
        synchronized (activityList) {
            for (int i = 0; i < activityList.size(); i++) {
                Activity activity = activityList.get(i);
                activityList.remove(activity);
                i--;
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 结束指定Activity
     *
     * @param clazz
     */
    public void finishActivity(Class clazz) {
        synchronized (activityList) {
            for (int i = 0; i < activityList.size(); i++) {
                Activity activity = activityList.get(i);
                if(clazz.isInstance(activity)) {
                    activityList.remove(activity);
                    i--;
                    if (activity != null) {
                        activity.finish();
                    }
                }

            }
        }
    }


    /**
     * 结束指定Activity
     *
     */
    public boolean isContainActivity(Class clazz) {
        synchronized (activityList) {
            for (int i = 0; i < activityList.size(); i++) {
                Activity activity = activityList.get(i);
                if(clazz.isInstance(activity)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * finish掉除activity外的所有Activity
     */
    public void clearAllActivityWithout(Class<?> cls) {
        synchronized (activityList) {
            for (int i = 0; i < activityList.size(); i++) {
                Activity activity1 = activityList.get(i);
                if(!(activity1.getClass().equals(cls))) {
                    activityList.remove(activity1);
                    i--;
                    if (activity1 != null) {
                        activity1.finish();
                    }
                }
            }
        }
    }
}
