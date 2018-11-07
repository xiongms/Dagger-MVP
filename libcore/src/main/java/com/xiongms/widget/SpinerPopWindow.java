package com.xiongms.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiongms.libcore.R;

import java.util.List;

public class SpinerPopWindow<T> extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private List<T> list;
    private View targetView;

    private OnItemSelectListener<T> onItemSelectListener;

    private OnListWindowListener onListWindowListener;

    public SpinerPopWindow(Context context, View targetView, List<T> list) {
        super(context);
        inflater=LayoutInflater.from(context);
        this.targetView = targetView;
        this.list=list;

        init();
    }

    private void init(){
        View view = inflater.inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(new MyAdapter());
        targetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWidth(view.getWidth());
                showAsDropDown(view);
                if(onListWindowListener != null) {
                    onListWindowListener.onShowChange(true);
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(onItemSelectListener != null) {
                    onItemSelectListener.onSelect(list.get(i));
                }
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if(onListWindowListener != null) {
            onListWindowListener.onShowChange(false);
        }
    }

    public SpinerPopWindow setOnListWindowListener(OnListWindowListener onListWindowListener) {
        this.onListWindowListener = onListWindowListener;
        return this;
    }

    public SpinerPopWindow setOnItemSelectListener(OnItemSelectListener listener) {
        this.onItemSelectListener = listener;
        return this;
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=inflater.inflate(R.layout.spiner_item_layout, null);
                holder.tvName=(TextView) convertView.findViewById(R.id.text_view);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(getItem(position).toString());
            return convertView;
        }
    }

    private class ViewHolder{
        private TextView tvName;
    }

    public interface OnItemSelectListener<T> {
        void onSelect(T t);
    }

    public interface OnListWindowListener {
        void onShowChange(boolean isShow);
    }
}