package com.xiongms.login.mvp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiongms.libcore.mvp.BaseMVPActivity;
import com.xiongms.login.R;
import com.xiongms.login.R2;
import com.xiongms.libcore.config.RouterConfig;
import com.xiongms.libcore.base.BaseActivity;
import com.xiongms.libcore.utils.ActivityUtil;
import com.xiongms.libcore.utils.StrUtil;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterConfig.ROUTER_LOGIN)
public class LoginActivity extends BaseMVPActivity<LoginPresenter> implements Contract.View {

    @BindView(R2.id.ed_phone)
    EditText mEdPhone;
    @BindView(R2.id.et_code)
    EditText mEtCode;
    @BindView(R2.id.btn_login)
    Button mBtnLogin;
    @BindView(R2.id.btn_sendsms)
    Button mBtnSendsms;

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.lg_activity_login;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        boolean isRefreshToken = getIntent().getBooleanExtra("RefreshToken", false);
        if(!isRefreshToken) {
            ActivityUtil.getInstance().clearAllActivityWithout(LoginActivity.class);
        }

        mBtnSendsms.setEnabled(false);
        mBtnSendsms.setActivated(false);

        mEdPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(StrUtil.isMobileNo(editable.toString()) && !mBtnSendsms.isActivated()) {
                    mBtnSendsms.setEnabled(true);
                } else {
                    mBtnSendsms.setEnabled(false);
                }
            }
        });


        mPresenter.initData();
    }


    @Override
    public void onBackPressed() {
        ActivityUtil.getInstance().clearAllActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R2.id.btn_login, R2.id.btn_sendsms})
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.btn_login) {
            mPresenter.login();
        } else if (i1 == R.id.btn_sendsms) {
            mPresenter.clickSendSMS();
        }
    }

    @Override
    public void setPhone(String phone) {
        mEdPhone.setText(phone);
    }

    @Override
    public String getPhone() {
        return mEdPhone.getText().toString();
    }

    @Override
    public String getCode() {
        return mEtCode.getText().toString();
    }

    @Override
    public void setSendSMSButton(boolean enable, boolean isActived, String text) {
        mBtnSendsms.setEnabled(enable);
        mBtnSendsms.setActivated(isActived);
        mBtnSendsms.setText(text);
    }

}
