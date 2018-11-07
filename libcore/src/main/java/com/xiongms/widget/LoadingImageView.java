package com.xiongms.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.xiongms.libcore.R;

/**
 *
 */
public class LoadingImageView extends android.support.v7.widget.AppCompatImageView {

    private final String TAG = "LoadingImageView";

    private AnimationDrawable animationDrawable;

    public LoadingImageView(Context context) {
        super(context);
        init();
    }

    public LoadingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setImageResource(R.drawable.loading_drawable_anim);

        animationDrawable = (AnimationDrawable) getDrawable();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow " + this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow " + this);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        final boolean visible = (visibility == VISIBLE) && (getVisibility() == VISIBLE);
        Log.d(TAG, "onVisibilityChanged visible=" + visible + "  " + this);
        if (visible) {
            animationDrawable.start();
        } else {
            animationDrawable.stop();
        }
    }
}
