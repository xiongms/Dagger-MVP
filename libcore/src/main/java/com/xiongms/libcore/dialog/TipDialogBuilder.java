package com.xiongms.libcore.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiongms.libcore.R;


/**
 *
 */
public class TipDialogBuilder extends BaseDialogBuilder {

    private String title;
    private Spanned message;

    private String okText;
    private TipDialogBuilder.ActionListener okActionListener;

    public TipDialogBuilder(Context c) {
        super(c);
    }

    public TipDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public TipDialogBuilder setMessage(String message) {
        this.message = new SpannableString(message);
        return this;
    }


    public TipDialogBuilder setMessage(Spanned message) {
        this.message = message;
        return this;
    }


    public TipDialogBuilder setOk(String text, TipDialogBuilder.ActionListener listener) {
        this.okText = text;
        this.okActionListener = listener;
        return this;
    }

    public Dialog build() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
        TextView tvOk = (TextView) view.findViewById(R.id.btn_ok);

        if(okActionListener != null) {
            tvOk.setVisibility(View.VISIBLE);
            tvOk.setText(okText);
        } else {
            tvOk.setVisibility(View.GONE);
        }

        tvMessage.setText(message);

        if(title != null && title.length() > 0) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        dialog.setContentView(view);

        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
        p.width = (int) (getWindowWidth() * 0.8);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(okActionListener == null) {
                    dialog.cancel();
                } else {
                    okActionListener.onClick(dialog);
                }
            }
        });
        return dialog;
    }

    public interface ActionListener {
        void onClick(Dialog dialog);
    }
}
