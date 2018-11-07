package com.xiongms.libcore.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiongms.libcore.R;

/**
 *
 */
public class LoadViewHelper {
    private View loadDefault;
    private View loadError;
    private View loadEmpty;
    private View loadIng;

    private View currentView;

    private ViewGroup parentView;

    private ViewGroup.LayoutParams layoutParams;

    private OnRefreshListener onRefreshListener;

    private volatile static Builder builder = new Builder();

    public LoadViewHelper(@Nullable View view) {
        this.currentView = view;

        ViewGroup viewGroup = null;

        if (view.getParent() != null) {
            viewGroup = (ViewGroup) view.getParent();
        }

        if (viewGroup != null) {
            layoutParams = view.getLayoutParams();

            if (!(viewGroup instanceof FrameLayout)) {
                FrameLayout frameLayout = new FrameLayout(view.getContext());
                viewGroup.addView(frameLayout, layoutParams);

                viewGroup.removeView(view);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                frameLayout.addView(view, params);

                parentView = frameLayout;
            } else {
                parentView = viewGroup;
            }
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public Context getContext() {
        return currentView.getContext();
    }

    public View inflate(int layoutId) {
        return LayoutInflater.from(getContext()).inflate(layoutId, null);
    }

    /****
     * 显示错误页
     * 默认
     ***/
    public void showError() {
        if (loadError == null) {
            if (builder.loadError != 0) {
                loadError = inflate(builder.loadError);
                loadError.setTag(loadError.getClass().getName());

                View view = loadError.findViewById(R.id.tv_error_refresh);
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onRefreshListener != null) {
                                onRefreshListener.onClickRefresh();
                            }
                        }
                    });
                }
            }
        }

        if (loadError != null)
            showLayout(loadError);
    }


    /****
     * 显示空白页
     ***/
    public void showEmpty() {
        if (loadEmpty == null) {
            if (builder.loadEmpty != 0) {
                loadEmpty = inflate(builder.loadEmpty);
            }
            loadEmpty.setTag(loadEmpty.getClass().getName());
        }

        if (loadEmpty != null)
            showLayout(loadEmpty);
    }

    /****
     * 显示空白页
     ***/
    public void showEmpty(String msg) {
        if (loadEmpty == null) {
            if (builder.loadEmpty != 0) {
                loadEmpty = inflate(builder.loadEmpty);
            }
            loadEmpty.setTag(loadEmpty.getClass().getName());
        }

        if (loadEmpty != null) {
            showLayout(loadEmpty);
            TextView textView = loadEmpty.findViewById(R.id.tv_empty_info);
            if (textView != null) {
                textView.setText(msg);
            }
        }
    }

    /****
     * 显示默认
     ***/
    public void showDefault() {
        if (loadDefault == null) {
            if (builder.loadDefault != 0) {
                loadDefault = inflate(builder.loadDefault);
            }
            loadDefault.setTag(loadDefault.getClass().getName());
        }

        if (loadDefault != null)
            showLayout(loadDefault);
    }

    /***
     * 没有加载文本
     **/
    public void showLoading() {
        if (loadIng == null) {
            if (builder.loadIng != 0) {
                loadIng = inflate(builder.loadIng);
            }
            loadIng.setTag(loadIng.getClass().getName());
        }

        if (loadIng != null)
            showLayout(loadIng);
    }

    public void showContent() {
        showLayout(currentView);

        if (loadError != null)
            loadError.setVisibility(View.GONE);

        if (loadEmpty != null)
            loadEmpty.setVisibility(View.GONE);

        if (loadDefault != null)
            loadDefault.setVisibility(View.GONE);

        if (loadIng != null)
            loadIng.setVisibility(View.GONE);
    }

    @NonNull
    public View getLoadError() {
        return loadError;
    }

    public void setLoadError(@LayoutRes int loadErrorRes) {
        setLoadError(inflate(loadErrorRes));
    }

    public void setLoadError(@NonNull View loadError) {
        if (this.loadError != null) {
            ViewGroup viewGroup = (ViewGroup) this.loadError.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.loadError);
            }
        }
        this.loadError = loadError;
    }

    @NonNull
    public View getLoadDefault() {
        return loadDefault;
    }

    public void setLoadDefault(@LayoutRes int loadDefaultRes) {
        setLoadDefault(inflate(loadDefaultRes));
    }

    public void setLoadDefault(@NonNull View loadDefault) {
        if (this.loadDefault != null) {
            ViewGroup viewGroup = (ViewGroup) this.loadDefault.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.loadDefault);
            }
        }
        this.loadDefault = loadDefault;
    }

    @NonNull
    public View getLoadEmpty() {
        return loadEmpty;
    }

    public void setLoadEmpty(@LayoutRes int loadEmptyRes) {
        setLoadEmpty(inflate(loadEmptyRes));
    }

    public void setLoadEmpty(@NonNull View loadEmpty) {
        if (this.loadEmpty != null) {
            ViewGroup viewGroup = (ViewGroup) this.loadEmpty.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.loadEmpty);
            }
        }
        this.loadEmpty = loadEmpty;
    }

    @NonNull
    public View getLoadIng() {
        return loadIng;
    }

    public void setLoadIng(@LayoutRes int loadIngRes) {
        setLoadIng(inflate(loadIngRes));
    }

    public void setLoadIng(@NonNull View loadIng) {
        if (this.loadIng != null) {
            ViewGroup viewGroup = (ViewGroup) this.loadIng.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.loadIng);
            }
        }
        this.loadIng = loadIng;
    }

    public void release() {
    }

    public void onDestroy() {
        loadError = null;
        loadEmpty = null;
        loadIng = null;
        loadDefault = null;
    }

    public synchronized void showLayout(@NonNull View view) {
        if (parentView == null) {
            return;
        }

        int index = parentView.indexOfChild(view);
        if (index < 0) {
            parentView.addView(view);
        }

        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }

        view.bringToFront();
    }

    public interface OnRefreshListener {
        void onClickRefresh();
    }

    /***
     * 全部配置类
     * ***/
    public static final class Builder {
        //全局配置默认
        int loadDefault;
        //全局配置加载
        int loadIng;
        //全局配置为空
        int loadEmpty;
        //全局配置错误
        int loadError;

        private Builder() {

        }

        public Builder setLoadDefault(int loadDefault) {
            this.loadDefault = loadDefault;
            return this;
        }

        public Builder setLoadIng(int loadIng) {
            this.loadIng = loadIng;
            return this;
        }

        public Builder setLoadEmpty(int loadEmpty) {
            this.loadEmpty = loadEmpty;
            return this;
        }

        public Builder setLoadError(int loadError) {
            this.loadError = loadError;
            return this;
        }
    }

    public static Builder getBuilder() {
        return builder;
    }

}
