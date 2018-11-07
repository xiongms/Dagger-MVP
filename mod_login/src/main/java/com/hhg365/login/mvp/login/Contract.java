package com.hhg365.login.mvp.login;

import com.xiongms.libcore.mvp.IPresenter;
import com.xiongms.libcore.mvp.IView;

/**
 *
 */
public interface Contract {

    interface View extends IView {
        void setLoaddingDialogText(String text);

        void setPhone(String phone);

        String getPhone();

        String getCode();

        void setSendSMSButton(boolean enable, boolean isActived, String text);

        void finish();
    }

    interface Presenter extends IPresenter<View> {
        void login();

        void clickSendSMS();
    }
}
