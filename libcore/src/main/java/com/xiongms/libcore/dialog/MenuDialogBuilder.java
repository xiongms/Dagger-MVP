package com.xiongms.libcore.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
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

import java.util.Arrays;
import java.util.List;


/**
 *
 */
public class MenuDialogBuilder<T> extends BaseDialogBuilder {

    private LayoutInflater layoutInflater;
    private List<T> list;
    private String title;
    private int itemResId;
    private boolean enableCancel;

    private MenuDialogBuilder.OnItemViewHolder<T> onItemViewHolder;

    private MenuDialogBuilder.OnItemClickListener<T> onItemClickListener;

    public MenuDialogBuilder(Context c) {
        super(c);
        this.context = c;
        this.layoutInflater = LayoutInflater.from(this.context);
        this.enableCancel = false;
    }

    public MenuDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public MenuDialogBuilder setData(List<T> list) {
        this.list = list;
        return this;
    }

    public MenuDialogBuilder setData(T[] array) {
        this.list = Arrays.asList(array);
        return this;
    }

    public MenuDialogBuilder setOnItemClickListener(MenuDialogBuilder.OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }


    public MenuDialogBuilder setOnItemViewHolder(MenuDialogBuilder.OnItemViewHolder holder) {
        this.onItemViewHolder = holder;
        return this;
    }

    public MenuDialogBuilder setItemLayoutResId(int resId) {
        this.itemResId = resId;
        return this;
    }

    public Dialog build() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_menu, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        View viewLine1 = view.findViewById(R.id.view_line_1);
        View viewLine2 = view.findViewById(R.id.view_line_2);

        if(enableCancel) {
            viewLine2.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            viewLine2.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }

        if(title != null && title.length() > 0) {
            viewLine1.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            viewLine1.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
        }

        MenuDialogBuilder.ListViewAdapter gridViewAdapter = new MenuDialogBuilder.ListViewAdapter();
        listView.setAdapter(gridViewAdapter);


        dialog.setContentView(view);

        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
        p.width = (int) (getWindowWidth() * 0.8);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(dialog, (T) adapterView.getAdapter().getItem(i), i);
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
        void onClick(Dialog dialog, T t, int position);
    }

    public interface OnItemViewHolder<T> {
        void hold(View view, T data, int position);
    }


    public class ListViewAdapter extends BaseAdapter {

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
