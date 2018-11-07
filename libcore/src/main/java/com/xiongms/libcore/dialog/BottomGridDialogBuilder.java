package com.xiongms.libcore.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


import com.xiongms.libcore.R;

import java.util.List;


/**
 *
 */
public class BottomGridDialogBuilder<T> extends BaseDialogBuilder{

    private LayoutInflater layoutInflater;
    private List<T> list;
    private String title;
    private int itemResId;
    private int numColumns;

    private OnItemViewHolder<T> onItemViewHolder;

    private OnItemClickListener<T> onItemClickListener;

    public BottomGridDialogBuilder(Context c) {
        super(c);
        this.layoutInflater = LayoutInflater.from(this.context);
        this.numColumns = 3;
    }

    public BottomGridDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public BottomGridDialogBuilder setData(List<T> list) {
        this.list = list;
        return this;
    }

    public BottomGridDialogBuilder setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        return this;
    }

    public BottomGridDialogBuilder setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }


    public BottomGridDialogBuilder setOnItemViewHolder(OnItemViewHolder holder) {
        this.onItemViewHolder = holder;
        return this;
    }

    public BottomGridDialogBuilder setItemLayoutResId(int resId) {
        this.itemResId = resId;
        return this;
    }

    public Dialog build() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_grid, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);
        tvTitle.setText(title);

        gridView.setNumColumns(numColumns);

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(dialog, (T) adapterView.getAdapter().getItem(i));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        return dialog;
    }

    public interface OnItemClickListener<T> {
        void onClick(Dialog dialog, T t);
    }

    public interface OnItemViewHolder<T> {
        void hold(View view, T data, int position);
    }


    public class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public T getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            T item = getItem(position);

            if (convertView == null) {
                convertView = layoutInflater.inflate(itemResId, parent, false);
            }

            if (onItemViewHolder != null) {
                onItemViewHolder.hold(convertView, item, position);
            }

            return convertView;
        }
    }
}
