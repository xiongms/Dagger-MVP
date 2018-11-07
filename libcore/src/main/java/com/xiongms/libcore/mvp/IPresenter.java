package com.xiongms.libcore.mvp;

import android.app.Activity;

/**
 * 
 * @author xiongms
 * @time 2018-08-17 14:40
 */
public interface IPresenter<V> {

    void onAttach(V rootView);

    /**
     * 在框架中 {@link Activity#onDestroy()} 时会默认调用 {@link IPresenter#onDetach()}
     */
    void onDetach();
}
