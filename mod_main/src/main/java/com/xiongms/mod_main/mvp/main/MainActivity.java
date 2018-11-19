package com.xiongms.mod_main.mvp.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiongms.libcore.config.RouterConfig;
import com.xiongms.libcore.base.BaseActivity;
import com.xiongms.libcore.mvp.BaseMVPActivity;
import com.xiongms.mod_main.R;
import com.xiongms.mod_main.R2;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterConfig.ROUTER_MAIN)
public class MainActivity extends BaseMVPActivity<MainPresenter> implements Contract.View {

    @BindView(R2.id.text_view)
    TextView mTextView;

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.main_activity_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.initData();
    }

    @Override
    public void setText(String name) {
        mTextView.setText(name);
    }


    @OnClick({R2.id.text_view})
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.text_view) {
            mPresenter.clickTextView();
        }
    }
}
