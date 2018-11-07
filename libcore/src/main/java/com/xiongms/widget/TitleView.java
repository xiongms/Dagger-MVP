package com.xiongms.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiongms.libcore.R;

/**
 * 自定义title 集成返回、标题、菜单
 * Created by xiongms on 2018/07/03.
 */

public class TitleView extends RelativeLayout {

    public static final int STYLE_BLACK = 0;
    public static final int STYLE_WHITE = 1;

    protected RelativeLayout rlContent;
    protected ImageView ivBack;
    protected TextView tvBack;
    protected TextView tvTitle;
    protected ImageView ivPlus;
    protected TextView tvMenu;
    protected View lineView;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_view_title, this);
        initView(this);
    }

    private void initView(View rootView) {
        rlContent = (RelativeLayout) rootView.findViewById(R.id.rl_content);
        ivBack = (ImageView) rootView.findViewById(R.id.iv_back);
        tvBack = (TextView) rootView.findViewById(R.id.tv_back);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        ivPlus = (ImageView) rootView.findViewById(R.id.iv_plus);
        tvMenu = (TextView) rootView.findViewById(R.id.tv_menu);
        lineView = rootView.findViewById(R.id.line_view);

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof Activity) {
                    ((Activity) v.getContext()).finish();
                }
            }
        });
        tvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof Activity) {
                    ((Activity) v.getContext()).finish();
                }
            }
        });
    }

    public void setStyle(int style) {
        switch (style) {
            case STYLE_BLACK:
                rlContent.setBackgroundColor(Color.parseColor("#111111"));
                tvTitle.setTextColor(Color.WHITE);
                tvMenu.setTextColor(Color.WHITE);
                ivBack.setImageResource(R.drawable.iv_back_white);
                lineView.setVisibility(GONE);
                break;
            case STYLE_WHITE:
                rlContent.setBackgroundColor(Color.WHITE);
                tvTitle.setTextColor(getResources().getColor(R.color.text_black));
                tvMenu.setTextColor(getResources().getColor(R.color.text_black));
                ivBack.setImageResource(R.drawable.ic_back);
                lineView.setVisibility(VISIBLE);
                break;
        }
    }

    public void setOnBackListener(OnClickListener listener) {
        tvBack.setOnClickListener(listener);
        ivBack.setOnClickListener(listener);
    }

    public void setBackText(String text) {
        tvBack.setVisibility(VISIBLE);
        tvBack.setText(text);
    }

    public TextView getBackText() {
        return tvBack;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setBackVisibility(int visibility) {
        ivBack.setVisibility(visibility);
    }

    public void setMenuImgVisibility(int visibility) {
        ivPlus.setVisibility(visibility);
    }

    public void setMenuImgClickListener(OnClickListener listener) {
        ivPlus.setOnClickListener(listener);
    }

    public void setMenuText(String text) {
        tvMenu.setText(text);
    }

    public void setMenuVisibility(int visibility) {
        tvMenu.setVisibility(visibility);
    }

    public void setMenuClickListener(OnClickListener listener) {
        tvMenu.setOnClickListener(listener);
    }

    public void setMenuImgIcon(int resId) {
        ivPlus.setImageResource(resId);
    }

    public TextView getTvMenu() {
        return tvMenu;
    }
}
