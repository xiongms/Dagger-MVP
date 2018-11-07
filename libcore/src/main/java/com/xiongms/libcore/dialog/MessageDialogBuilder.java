package com.xiongms.libcore.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xiongms.libcore.R;

import java.util.List;


/**
 *
 */
public class MessageDialogBuilder extends BaseDialogBuilder {

    private String title;
    private Spanned message;
    private int gravity = Gravity.CENTER;

    private String okText;
    private String cancelText;
    private MessageDialogBuilder.ActionListener cancelActionListener;
    private MessageDialogBuilder.ActionListener okActionListener;

    public MessageDialogBuilder(Context c) {
        super(c);
    }

    public MessageDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }


    public MessageDialogBuilder setMessage(String message) {
        this.message = new SpannableString(message);
        return this;
    }


    public MessageDialogBuilder setMessage(Spanned message) {
        this.message = message;
        return this;
    }

    public MessageDialogBuilder setContentGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public MessageDialogBuilder setCancel(String text, MessageDialogBuilder.ActionListener listener) {
        this.cancelText = text;
        this.cancelActionListener = listener;
        return this;
    }


    public MessageDialogBuilder setOk(String text, MessageDialogBuilder.ActionListener listener) {
        this.okText = text;
        this.okActionListener = listener;
        return this;
    }

    public Dialog build() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);

        tvMessage.setGravity(gravity);

        if(cancelActionListener != null) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText(cancelText);
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        if(okActionListener != null) {
            btnOk.setVisibility(View.VISIBLE);
            btnOk.setText(okText);
        } else {
            btnOk.setVisibility(View.GONE);
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cancelActionListener == null) {
                    dialog.cancel();
                } else {
                    cancelActionListener.onClick(dialog);
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
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
