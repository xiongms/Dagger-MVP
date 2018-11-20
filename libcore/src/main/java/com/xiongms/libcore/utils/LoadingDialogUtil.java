package com.xiongms.libcore.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiongms.libcore.R;


/**
 * 网络请求加载框
 */

public class LoadingDialogUtil {

    private Dialog mLoadingDialog;

    private int mShowedCount = 0;

    public LoadingDialogUtil() {
    }

    private Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_loading_dialog, null);
        LinearLayout layout = v.findViewById(R.id.dialog_view);


        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

//        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mShowedCount = 0;
            }
        });



        return loadingDialog;
    }

    public void showLoadingDialog(Context context) {
        mShowedCount++;
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = createLoadingDialog(context);
            }

            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setLoadingText(String text) {
        if (mLoadingDialog != null) {
            TextView textView = (TextView) mLoadingDialog.findViewById(R.id.tv_desc);
            if (textView != null) {
                textView.setText(text);
            }
        }
    }

    public void cancelLoadingDialog() {
        mShowedCount--;
        if (mShowedCount < 0) {
            mShowedCount = 0;
        }

        if (mLoadingDialog == null) {
            return;
        }
        if (mShowedCount == 0) {
            mLoadingDialog.cancel();
        }
    }


    public void destoryLoadingDialog() {
        mShowedCount = 0;
        if (mLoadingDialog == null) {
            return;
        }
        mLoadingDialog.cancel();
        mLoadingDialog = null;
    }

}
