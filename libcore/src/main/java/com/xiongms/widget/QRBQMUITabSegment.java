package com.xiongms.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.QMUIInterpolatorStaticHolder;
import com.qmuiteam.qmui.R;
import com.qmuiteam.qmui.util.QMUIColorHelper;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.util.QMUILangHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUIItemViewsAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * 修改至QMUI框架中的QMUITabSegment
 * 1、由于UI设计需要实现选中时改变字体大小，从QMUI 1.1.6版本框架中提取出QMUITabSegment类进行修改
 * 2、MODE_FIXED模式下允许父控件拦截触摸事件，避免和viewpager翻页冲突
 */
public class QRBQMUITabSegment extends HorizontalScrollView {
    // mode: 自适应宽度+滚动 / 均分
    public static final int MODE_SCROLLABLE = 0;
    public static final int MODE_FIXED = 1;
    // icon position
    public static final int ICON_POSITION_LEFT = 0;
    public static final int ICON_POSITION_TOP = 1;
    public static final int ICON_POSITION_RIGHT = 2;
    public static final int ICON_POSITION_BOTTOM = 3;
    // status: 用于记录tab的改变状态
    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_PROGRESS = 1;
    private static final int STATUS_SELECTED = 2;
    /**
     * listener
     */
    private final ArrayList<OnTabSelectedListener> mSelectedListeners = new ArrayList<>();
    private View mIndicatorView;
    private int mSelectedIndex = Integer.MIN_VALUE;
    private int mPendingSelectedIndex = Integer.MIN_VALUE;
    private Container mContentLayout;
    /**
     * item的最小字体大小
     */
    private int mTabMinTextSize;
    /**
     * item的最大字体大小
     */
    private int mTabMaxTextSize;
    /**
     * 是否有Indicator
     */
    private boolean mHasIndicator = true;
    /**
     * Indicator高度
     */
    private int mIndicatorHeight;
    /**
     * indicator在顶部
     */
    private boolean mIndicatorTop = false;
    /**
     * indicator采用drawable
     */
    private Drawable mIndicatorDrawable;
    /**
     * indicator宽度跟随内容宽度
     */
    private boolean mIsIndicatorWidthFollowContent = true;
    /**
     * item normal color
     */
    private int mDefaultNormalColor;
    /**
     * item selected color
     */
    private int mDefaultSelectedColor;

    /**
     * item Indicator color
     */
    private int mIndicatorColor = 0;
    /**
     * item icon的默认位置
     */
    @IconPosition
    private int mDefaultTabIconPosition;
    /**
     * TabSegmentMode
     */
    @Mode
    private int mMode = MODE_FIXED;
    /**
     * ScrollMode下item的间隙
     */
    private int mItemSpaceInScrollMode;
    /**
     * typeface
     */
    private TypefaceProvider mTypefaceProvider;

    /**
     * 记录 ViewPager 的 scrollState
     */
    private int mViewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;

    private boolean mIsAnimating;
    private OnTabClickListener mOnTabClickListener;
    private boolean mForceIndicatorNotDoLayoutWhenParentLayout = false;
    protected OnClickListener mTabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsAnimating || mViewPagerScrollState != ViewPager.SCROLL_STATE_IDLE) {
                return;
            }
            int index = (int) v.getTag();
            Tab model = getAdapter().getItem(index);
            if (model != null) {
                selectTab(index, !mHasIndicator && !model.isDynamicChangeIconColor());
            }
            if (mOnTabClickListener != null) {
                mOnTabClickListener.onTabClick(index);
            }
        }
    };
    /**
     * 与ViewPager的协同工作
     */
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mPagerAdapterObserver;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnTabSelectedListener mViewPagerSelectedListener;
    //    private AdapterChangeListener mAdapterChangeListener;
    private boolean mIsInSelectTab = false;


    private int indicatorLineLeft = -1;
    private int indicatorLineWidth = -1;
    private boolean mHasIndicatorLine = true;
    private View mIndicatorLineView;
    private int mIndicatorLineColor;
    private int mIndicatorLineHeight;

    public QRBQMUITabSegment(Context context) {
        this(context, null);
    }


    public QRBQMUITabSegment(Context context, boolean hasIndicator) {
        this(context, null);
        mHasIndicator = hasIndicator;
    }

    public QRBQMUITabSegment(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.QMUITabSegmentStyle);
    }

    public QRBQMUITabSegment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        setHorizontalScrollBarEnabled(false);
        setClipToPadding(false);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mDefaultSelectedColor = QMUIResHelper.getAttrColor(context, R.attr.qmui_config_color_blue);
        mDefaultNormalColor = ContextCompat.getColor(context, R.color.qmui_config_color_gray_5);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QMUITabSegment, defStyleAttr, 0);
        mHasIndicator = true;
        mIndicatorHeight = getResources().getDimensionPixelSize(R.dimen.qmui_tab_segment_indicator_height);
        mTabMaxTextSize = getResources().getDimensionPixelSize(R.dimen.qmui_tab_segment_text_size);
        mTabMinTextSize = mTabMaxTextSize;
        mIndicatorTop = false;
        mDefaultTabIconPosition = ICON_POSITION_LEFT;
        mMode = MODE_FIXED;
        mItemSpaceInScrollMode = QMUIDisplayHelper.dp2px(context, 10);
        array.recycle();

        mContentLayout = new Container(context);
        addView(mContentLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (mHasIndicator) {
            createIndicatorView();
        }

        if (mHasIndicatorLine) {
            createIndicatorLineView();
        }
//        createTypefaceProvider(context, typefaceProviderName);
    }

    private void createTypefaceProvider(Context context, String className) {
        if (QMUILangHelper.isNullOrEmpty(className)) {
            return;
        }
        className = className.trim();
        if (className.length() == 0) {
            return;
        }
        className = getFullClassName(context, className);
        //noinspection TryWithIdenticalCatches
        try {
            ClassLoader classLoader;
            if (isInEditMode()) {
                classLoader = this.getClass().getClassLoader();
            } else {
                classLoader = context.getClassLoader();
            }
            Class<? extends TypefaceProvider> providerClass =
                    classLoader.loadClass(className).asSubclass(TypefaceProvider.class);
            Constructor<? extends TypefaceProvider> constructor;
            try {
                constructor = providerClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Error creating TypefaceProvider " + className, e);
            }
            constructor.setAccessible(true);
            mTypefaceProvider = constructor.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to find TypefaceProvider " + className, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate the TypefaceProvider: " + className, e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Could not instantiate the TypefaceProvider: " + className, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot access non-public constructor " + className, e);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Class is not a TypefaceProvider " + className, e);
        }
    }

    private String getFullClassName(Context context, String className) {
        if (className.charAt(0) == '.') {
            return context.getPackageName() + className;
        }
        return className;
    }

    public void setTypefaceProvider(TypefaceProvider typefaceProvider) {
        mTypefaceProvider = typefaceProvider;
    }

    public QRBQMUITabSegment addTab(Tab item) {
        mContentLayout.getTabAdapter().addItem(item);
        return this;
    }

    private TabAdapter getAdapter() {
        return mContentLayout.getTabAdapter();
    }

    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        if (mIndicatorView != null) {
            mIndicatorView.setBackgroundColor(mIndicatorColor);
        }
    }

    private void createIndicatorView() {
        if (mIndicatorView == null) {
            mIndicatorView = new View(getContext());
            mIndicatorView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, mIndicatorHeight));
            if (mIndicatorDrawable != null) {
                QMUIViewHelper.setBackgroundKeepingPadding(mIndicatorView, mIndicatorDrawable);
            } else if (mIndicatorColor != 0) {
                mIndicatorView.setBackgroundColor(mIndicatorColor);
            } else {
                mIndicatorView.setBackgroundColor(mDefaultSelectedColor);
            }
            mContentLayout.addView(mIndicatorView, 0);
            mIndicatorView.bringToFront();
        }
    }

    public void setIndicatorLineColor(int color) {
        mIndicatorLineColor = color;
        if (mIndicatorLineView != null) {
            mIndicatorLineView.setBackgroundColor(mIndicatorLineColor);
        }
    }

    public void setIndicatorLineHeight(int height) {
        mIndicatorLineHeight = height;
    }


    private void createIndicatorLineView() {
        if (mIndicatorLineView == null) {
            mIndicatorLineView = new View(getContext());
            mIndicatorLineView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, mIndicatorHeight));
            mIndicatorLineView.setBackgroundColor(mIndicatorLineColor);
            mContentLayout.addView(mIndicatorLineView, -1);

            if (mIndicatorView != null) {
                mIndicatorView.bringToFront();
            }
        }
    }

    public void setMinTabTextSize(int tabTextSize) {
        mTabMinTextSize = tabTextSize;
    }

    public void setMaxTabTextSize(int tabTextSize) {
        mTabMaxTextSize = tabTextSize;
    }

    /**
     * 清空已经存在的 Tab。
     * 一般先调用本方法清空已加上的 Tab, 然后重新 {@link #addTab(Tab)} 添加新的 Tab, 然后通过 {@link #notifyDataChanged()} 通知变动
     */
    public void reset() {
        mSelectedIndex = Integer.MIN_VALUE;
        if (mIndicatorView != null)
            mIndicatorView.setVisibility(GONE);
        mContentLayout.getTabAdapter().clear();
    }

    /**
     * 通知 QMUITabSegment 数据变动。
     * 一般先调用 {@link #reset()} 清空已加上的 Tab, 然后重新 {@link #addTab(Tab)} 添加新的 Tab, 然后通过本方法通知变动
     */
    public void notifyDataChanged() {
        getAdapter().setup();
    }

    public void addOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        if (!mSelectedListeners.contains(listener)) {
            mSelectedListeners.add(listener);
        }
    }

    public void setItemSpaceInScrollMode(int itemSpaceInScrollMode) {
        mItemSpaceInScrollMode = itemSpaceInScrollMode;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        if (mMode == MODE_FIXED) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return result;
    }

    /**
     * 设置 indicator 为自定义的 Drawable(默认跟随 Tab 的 selectedColor)
     */
    public void setIndicatorDrawable(Drawable indicatorDrawable) {
        mIndicatorDrawable = indicatorDrawable;
        if (indicatorDrawable != null) {
            mIndicatorHeight = indicatorDrawable.getIntrinsicHeight();
        }
        mContentLayout.invalidate();
    }

    /**
     * 设置 indicator的宽度是否随内容宽度变化
     */
    public void setIndicatorWidthAdjustContent(boolean indicatorWidthFollowContent) {
        mIsIndicatorWidthFollowContent = indicatorWidthFollowContent;
    }

    /**
     * 设置 indicator 的位置
     *
     * @param isIndicatorTop true 时表示 indicator 位置在 Tab 的上方, false 时表示在下方
     */
    public void setIndicatorPosition(boolean isIndicatorTop) {
        mIndicatorTop = isIndicatorTop;
    }

    /**
     * 设置是否需要显示 indicator
     *
     * @param hasIndicator 是否需要显示 indicator
     */
    public void setHasIndicator(boolean hasIndicator) {
        if (mHasIndicator != hasIndicator) {
            mHasIndicator = hasIndicator;
            if (mHasIndicator) {
                createIndicatorView();
            } else {
                mContentLayout.removeView(mIndicatorView);
                mIndicatorView = null;
            }
        }

    }


    /**
     * 设置是否需要显示 indicatorLine
     *
     * @param hasIndicatorLine 是否需要显示 indicatorLine
     */
    public void setHasIndicatorLine(boolean hasIndicatorLine) {
        if (mHasIndicatorLine != hasIndicatorLine) {
            mHasIndicatorLine = hasIndicatorLine;
            if (mHasIndicatorLine) {
                createIndicatorLineView();
            } else {
                mContentLayout.removeView(mIndicatorLineView);
                mIndicatorLineView = null;
            }
        }

    }

    public int getMode() {
        return mMode;
    }

    public void setMode(@Mode int mode) {
        if (mMode != mode) {
            mMode = mode;
            mContentLayout.invalidate();
        }
    }

    public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        mSelectedListeners.remove(listener);
    }

    public void clearOnTabSelectedListeners() {
        mSelectedListeners.clear();
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean useAdapterTitle) {
        setupWithViewPager(viewPager, useAdapterTitle, true);
    }

    /**
     * @param viewPager       需要关联的 ViewPager。
     * @param useAdapterTitle 自动根据ViewPager的adapter.getTitle取值。
     * @param autoRefresh     adapter有更改时，刷新TabSegment。
     */
    public void setupWithViewPager(@Nullable final ViewPager viewPager, boolean useAdapterTitle, boolean autoRefresh) {
        if (mViewPager != null) {
            // If we've already been setup with a ViewPager, remove us from it
            if (mOnPageChangeListener != null) {
                mViewPager.removeOnPageChangeListener(mOnPageChangeListener);
            }

//            if (mAdapterChangeListener != null) {
//                mViewPager.removeOnAdapterChangeListener(mAdapterChangeListener);
//            }
        }

        if (mViewPagerSelectedListener != null) {
            // If we already have a tab selected listener for the ViewPager, remove it
            removeOnTabSelectedListener(mViewPagerSelectedListener);
            mViewPagerSelectedListener = null;
        }

        if (viewPager != null) {
            mViewPager = viewPager;

            // Add our custom OnPageChangeListener to the ViewPager
            if (mOnPageChangeListener == null) {
                mOnPageChangeListener = new TabLayoutOnPageChangeListener(this);
            }
            viewPager.addOnPageChangeListener(mOnPageChangeListener);

            // Now we'll add a tab selected listener to set ViewPager's current item
            mViewPagerSelectedListener = new ViewPagerOnTabSelectedListener(viewPager);
            addOnTabSelectedListener(mViewPagerSelectedListener);

            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                // Now we'll populate ourselves from the pager adapter, adding an observer if
                // autoRefresh is enabled
                setPagerAdapter(adapter, useAdapterTitle, autoRefresh);
            }

            // Add a listener so that we're notified of any adapter changes
//            if (mAdapterChangeListener == null) {
//                mAdapterChangeListener = new AdapterChangeListener(useAdapterTitle);
//            }
//            mAdapterChangeListener.setAutoRefresh(autoRefresh);
//            viewPager.addOnAdapterChangeListener(mAdapterChangeListener);
        } else {
            // We've been given a null ViewPager so we need to clear out the internal state,
            // listeners and observers
            mViewPager = null;
            setPagerAdapter(null, false, false);
        }
    }

    private void dispatchTabSelected(int index) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onTabSelected(index);
        }
    }

    private void dispatchTabUnselected(int index) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onTabUnselected(index);
        }
    }

    private void dispatchTabReselected(int index) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onTabReselected(index);
        }
    }

    private void dispatchTabDoubleTap(int index) {
        for (int i = mSelectedListeners.size() - 1; i >= 0; i--) {
            mSelectedListeners.get(i).onDoubleTap(index);
        }
    }

    /**
     * 设置 Tab 正常状态下的颜色
     */
    public void setDefaultNormalColor(@ColorInt int defaultNormalColor) {
        mDefaultNormalColor = defaultNormalColor;
    }

    /**
     * 设置 Tab 选中状态下的颜色
     */
    public void setDefaultSelectedColor(@ColorInt int defaultSelectedColor) {
        mDefaultSelectedColor = defaultSelectedColor;
    }

    /**
     * @param defaultTabIconPosition
     */
    public void setDefaultTabIconPosition(@IconPosition int defaultTabIconPosition) {
        mDefaultTabIconPosition = defaultTabIconPosition;
    }

    private void preventLayoutToChangeTabColor(TextView textView, int color, Tab model, int status) {
        mForceIndicatorNotDoLayoutWhenParentLayout = true;
        changeTabColor(textView, color, model, status);
        mForceIndicatorNotDoLayoutWhenParentLayout = false;
    }

    private void changeTabTextSize(TextView textView, float textSize) {
        if (textView instanceof InnerTextView) {
            ((InnerTextView) textView).setSelectedTextSize(textSize);
        }
    }

    private void changeTabColor(TextView textView, int color, Tab model, int status) {
        changeTabColor(textView, color, model, status, false);
    }

    private void changeTabColor(TextView textView, int color, Tab model, int status, boolean preventColorChange) {
        if (!preventColorChange) {
            textView.setTextColor(color);
        }

        if (!model.isDynamicChangeIconColor()) {
            if (status == STATUS_NORMAL || model.getSelectedIcon() == null) {
                setDrawable(textView, model.getNormalIcon(), getTabIconPosition(model));
            } else if (status == STATUS_SELECTED) {
                setDrawable(textView, model.getSelectedIcon(), getTabIconPosition(model));
            }
            return;
        }

        if (!preventColorChange) {
            Drawable drawable = textView.getCompoundDrawables()[getTabIconPosition(model)];
            if (drawable == null) {
                return;
            }
            // 这里要拿textView已经set并mutate的drawable
            QMUIDrawableHelper.setDrawableTintColor(drawable, color);
            setDrawable(textView, model.getNormalIcon(), getTabIconPosition(model));
        }

    }

    public void selectTab(int index) {
        selectTab(index, false);
    }

    public void selectTab(final int index, boolean preventAnim) {
        if (mIsInSelectTab) {
            return;
        }
        mIsInSelectTab = true;
        if (mContentLayout.getTabAdapter().getSize() == 0 || mContentLayout.getTabAdapter().getSize() <= index) {
            mIsInSelectTab = false;
            return;
        }
        if (mSelectedIndex == index) {
            dispatchTabReselected(index);
            mIsInSelectTab = false;
            return;
        }

        if (mIsAnimating) {
            mPendingSelectedIndex = index;
            mIsInSelectTab = false;
            return;
        }

        TabAdapter tabAdapter = getAdapter();
        final List<TabItemView> listViews = tabAdapter.getViews();
        // 第一次设置
        if (mSelectedIndex == Integer.MIN_VALUE) {
            tabAdapter.setup();
            Tab model = tabAdapter.getItem(index);
            reLayoutIndicator(listViews, model);
            TextView selectedTv = listViews.get(index).getTextView();
            setTextViewTypeface(selectedTv, true);
            changeTabColor(selectedTv, getTabSelectedColor(model), model, STATUS_SELECTED);
            changeTabTextSize(selectedTv, mTabMaxTextSize);
            dispatchTabSelected(index);
            mSelectedIndex = index;
            mIsInSelectTab = false;
            return;
        }
        final int prev = mSelectedIndex;
        final Tab prevModel = tabAdapter.getItem(prev);
        final TabItemView prevView = listViews.get(prev);
        final Tab nowModel = tabAdapter.getItem(index);
        final TabItemView nowView = listViews.get(index);

        if (preventAnim) {
            dispatchTabUnselected(prev);
            dispatchTabSelected(index);
            setTextViewTypeface(prevView.getTextView(), false);
            setTextViewTypeface(nowView.getTextView(), true);
            changeTabColor(prevView.getTextView(), getTabNormalColor(prevModel), prevModel, STATUS_NORMAL, mViewPagerScrollState != ViewPager.SCROLL_STATE_IDLE);
            changeTabColor(nowView.getTextView(), getTabSelectedColor(nowModel), nowModel, STATUS_SELECTED, mViewPagerScrollState != ViewPager.SCROLL_STATE_IDLE);
            changeTabTextSize(prevView.getTextView(), mTabMinTextSize);
            changeTabTextSize(nowView.getTextView(), mTabMaxTextSize);
            if (getScrollX() > nowView.getLeft()) {
                smoothScrollTo(nowView.getLeft(), 0);
            } else {
                int realWidth = getWidth() - getPaddingRight() - getPaddingLeft();
                if (getScrollX() + realWidth < nowView.getRight()) {
                    smoothScrollBy(nowView.getRight() - realWidth - getScrollX(), 0);
                }
            }
            mSelectedIndex = index;
            mIsInSelectTab = false;
            reLayoutIndicator(listViews, nowModel);
            return;
        }

        final int leftDistance = nowModel.getContentLeft() - prevModel.getContentLeft();
        final int widthDistance = nowModel.getContentWidth() - prevModel.getContentWidth();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setInterpolator(QMUIInterpolatorStaticHolder.LINEAR_INTERPOLATOR);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = (float) animation.getAnimatedValue();
                if (mIndicatorView != null && listViews.size() > 1) {
                    int targetLeft = (int) (prevModel.getContentLeft() + leftDistance * animValue);
                    int targetWidth = (int) (prevModel.getContentWidth() + widthDistance * animValue);
                    if (mIndicatorDrawable == null) {
                        if (mIndicatorColor != 0) {
                            mIndicatorView.setBackgroundColor(mIndicatorColor);
                        } else {
                            mIndicatorView.setBackgroundColor(mDefaultSelectedColor);
                        }
                    }
                    mIndicatorView.layout(targetLeft, mIndicatorView.getTop(), targetLeft + targetWidth, mIndicatorView.getBottom());
                }
                int preColor = QMUIColorHelper.computeColor(getTabSelectedColor(prevModel), getTabNormalColor(prevModel), animValue);
                int nowColor = QMUIColorHelper.computeColor(getTabNormalColor(nowModel), getTabSelectedColor(nowModel), animValue);
                preventLayoutToChangeTabColor(prevView.getTextView(), preColor, prevModel, STATUS_PROGRESS);
                preventLayoutToChangeTabColor(nowView.getTextView(), nowColor, nowModel, STATUS_PROGRESS);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSelectedIndex = index;
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
                changeTabColor(nowView.getTextView(), getTabSelectedColor(nowModel), nowModel, STATUS_SELECTED);
                changeTabTextSize(nowView.getTextView(), mTabMaxTextSize);
                changeTabTextSize(prevView.getTextView(), mTabMinTextSize);
                dispatchTabSelected(index);
                dispatchTabUnselected(prev);
                setTextViewTypeface(prevView.getTextView(), false);
                setTextViewTypeface(nowView.getTextView(), true);
                mSelectedIndex = index;
                if (mPendingSelectedIndex != Integer.MIN_VALUE && mPendingSelectedIndex != mSelectedIndex) {
                    selectTab(index, false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                changeTabColor(nowView.getTextView(), getTabSelectedColor(nowModel), nowModel, STATUS_SELECTED);
                changeTabTextSize(nowView.getTextView(), mTabMaxTextSize);
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(200);
        animator.start();
        mIsInSelectTab = false;
    }

    private void reLayoutIndicator(List<TabItemView> listViews, Tab model) {
        if (mViewPagerScrollState == ViewPager.SCROLL_STATE_IDLE &&
                mIndicatorView != null && listViews.size() > 1) {
            if (mIndicatorDrawable != null) {
                QMUIViewHelper.setBackgroundKeepingPadding(mIndicatorView, mIndicatorDrawable);
            } else {
                if (mIndicatorColor != 0) {
                    mIndicatorView.setBackgroundColor(mIndicatorColor);
                } else {
                    mIndicatorView.setBackgroundColor(mDefaultSelectedColor);
                }
            }
            if (model.contentWidth > 0) {
                mIndicatorView.layout(model.contentLeft, mIndicatorView.getTop(),
                        model.contentLeft + model.contentWidth, mIndicatorView.getBottom());
            }
        }
    }

    private void setTextViewTypeface(TextView tv, boolean selected) {
        if (mTypefaceProvider == null || tv == null) {
            return;
        }
        boolean isBold = selected ? mTypefaceProvider.isSelectedTabBold() : mTypefaceProvider.isNormalTabBold();
        tv.setTypeface(null, isBold ? Typeface.BOLD : Typeface.NORMAL);
    }

    public void updateIndicatorPosition(final int index, float offsetPercent) {
        if (mIsAnimating || mIsInSelectTab || offsetPercent == 0) {
            return;
        }

        int targetIndex;
        if (offsetPercent < 0) {
            targetIndex = index - 1;
            offsetPercent = -offsetPercent;
        } else {
            targetIndex = index + 1;
        }

        TabAdapter tabAdapter = getAdapter();
        final List<TabItemView> listViews = tabAdapter.getViews();
        if (listViews.size() <= index || listViews.size() <= targetIndex) {
            return;
        }
        Tab preModel = tabAdapter.getItem(index);
        Tab targetModel = tabAdapter.getItem(targetIndex);
        TextView preTv = listViews.get(index).getTextView();
        TextView nowTv = listViews.get(targetIndex).getTextView();
        int preColor = QMUIColorHelper.computeColor(getTabSelectedColor(preModel), getTabNormalColor(preModel), offsetPercent);
        int targetColor = QMUIColorHelper.computeColor(getTabNormalColor(targetModel), getTabSelectedColor(targetModel), offsetPercent);
        preventLayoutToChangeTabColor(preTv, preColor, preModel, STATUS_PROGRESS);
        preventLayoutToChangeTabColor(nowTv, targetColor, targetModel, STATUS_PROGRESS);
        if (mIndicatorView != null && listViews.size() > 1) {
            final int leftDistance = targetModel.getContentLeft() - preModel.getContentLeft();
            final int widthDistance = targetModel.getContentWidth() - preModel.getContentWidth();
            final int targetLeft = (int) (preModel.getContentLeft() + leftDistance * offsetPercent);
            final int targetWidth = (int) (preModel.getContentWidth() + widthDistance * offsetPercent);
            if (mIndicatorDrawable == null) {
                if (mIndicatorColor != 0) {
                    mIndicatorView.setBackgroundColor(mIndicatorColor);
                } else {
                    mIndicatorView.setBackgroundColor(mDefaultSelectedColor);
                }
            }
            mIndicatorView.layout(targetLeft, mIndicatorView.getTop(), targetLeft + targetWidth, mIndicatorView.getBottom());
        }
    }

    /**
     * 改变 Tab 的文案
     *
     * @param index Tab 的 index
     * @param text  新文案
     */
    public void updateTabText(int index, String text) {
        Tab model = getAdapter().getItem(index);
        if (model == null) {
            return;
        }
        model.setText(text);
        notifyDataChanged();
    }

    /**
     * 整个 Tab 替换
     *
     * @param index 需要被替换的 Tab 的 index
     * @param model 新的 Tab
     */
    public void replaceTab(int index, Tab model) {
        try {
            getAdapter().replaceItem(index, model);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        mOnTabClickListener = onTabClickListener;
    }

    private void setDrawable(TextView tv, Drawable drawable, int iconPosition) {
        tv.setCompoundDrawables(
                iconPosition == ICON_POSITION_LEFT ? drawable : null,
                iconPosition == ICON_POSITION_TOP ? drawable : null,
                iconPosition == ICON_POSITION_RIGHT ? drawable : null,
                iconPosition == ICON_POSITION_BOTTOM ? drawable : null);
    }

    private int getTabNormalColor(Tab item) {
        int color = item.getNormalColor();
        if (color == Tab.USE_TAB_SEGMENT) {
            color = mDefaultNormalColor;
        }
        return color;
    }

    private int getTabIconPosition(Tab item) {
        int iconPosition = item.getIconPosition();
        if (iconPosition == Tab.USE_TAB_SEGMENT) {
            iconPosition = mDefaultTabIconPosition;
        }
        return iconPosition;
    }

    private int getTabSelectedColor(Tab item) {
        int color = item.getSelectedColor();
        if (color == Tab.USE_TAB_SEGMENT) {
            color = mDefaultSelectedColor;
        }
        return color;
    }

    void populateFromPagerAdapter(boolean useAdapterTitle) {
        if (mPagerAdapter == null) {
            if (useAdapterTitle) {
                reset();
            }
            return;
        }
        final int adapterCount = mPagerAdapter.getCount();
        if (useAdapterTitle) {
            reset();
            for (int i = 0; i < adapterCount; i++) {
                addTab(new Tab(mPagerAdapter.getPageTitle(i)));
            }
            notifyDataChanged();
        }

        if (mViewPager != null && adapterCount > 0) {
            final int curItem = mViewPager.getCurrentItem();
            if (curItem != mSelectedIndex && curItem < adapterCount) {
                selectTab(curItem, true);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            int paddingHor = getPaddingLeft() + getPaddingRight();
            child.measure(MeasureSpec.makeMeasureSpec(widthSize - paddingHor, MeasureSpec.EXACTLY), heightMeasureSpec);
            if (widthMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(Math.min(widthSize, child.getMeasuredWidth() + paddingHor), heightMeasureSpec);
                return;
            }
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    void setPagerAdapter(@Nullable final PagerAdapter adapter, boolean useAdapterTitle, final boolean addObserver) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            // If we already have a PagerAdapter, unregister our observer
            mPagerAdapter.unregisterDataSetObserver(mPagerAdapterObserver);
        }

        mPagerAdapter = adapter;

        if (addObserver && adapter != null) {
            // Register our observer on the new adapter
            if (mPagerAdapterObserver == null) {
                mPagerAdapterObserver = new PagerAdapterObserver(useAdapterTitle);
            }
            adapter.registerDataSetObserver(mPagerAdapterObserver);
        }

        // Finally make sure we reflect the new adapter
        populateFromPagerAdapter(useAdapterTitle);
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    private int getTabCount() {
        return getAdapter().getSize();
    }

    /**
     * 根据 index 获取对应下标的 {@link Tab} 对象
     *
     * @return index 下标对应的 {@link Tab} 对象
     */
    public Tab getTab(int index) {
        return getAdapter().getItem(index);
    }

    /**
     * 根据 index 在对应的 Tab 上显示未读数或红点
     *
     * @param index 要显示未读数或红点的 Tab 的下标
     * @param count 不为0时红点会显示该数字作为未读数,为0时只会显示一个小红点
     */
    public void showSignCountView(Context context, int index, int count) {
        Tab tab = getAdapter().getItem(index);
        tab.showSignCountView(context, count);
        notifyDataChanged();
    }

    /**
     * 根据 index 在对应的 Tab 上隐藏红点
     */
    public void hideSignCountView(int index) {
        Tab tab = getAdapter().getItem(index);
        tab.hideSignCountView();
    }

    /**
     * 获取当前的红点数值，如果没有红点则返回 0
     */
    public int getSignCount(int index) {
        Tab tab = getAdapter().getItem(index);
        return tab.getSignCount();
    }

    @IntDef(value = {MODE_SCROLLABLE, MODE_FIXED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    @IntDef(value = {ICON_POSITION_LEFT, ICON_POSITION_TOP, ICON_POSITION_RIGHT, ICON_POSITION_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IconPosition {
    }

    public interface OnTabClickListener {
        /**
         * 当某个 Tab 被点击时会触发
         *
         * @param index 被点击的 Tab 下标
         */
        void onTabClick(int index);
    }

    public interface OnTabSelectedListener {
        /**
         * 当某个 Tab 被选中时会触发
         *
         * @param index 被选中的 Tab 下标
         */
        void onTabSelected(int index);

        /**
         * 当某个 Tab 被取消选中时会触发
         *
         * @param index 被取消选中的 Tab 下标
         */
        void onTabUnselected(int index);

        /**
         * 当某个 Tab 处于被选中状态下再次被点击时会触发
         *
         * @param index 被再次点击的 Tab 下标
         */
        void onTabReselected(int index);

        /**
         * 当某个 Tab 被双击时会触发
         *
         * @param index 被双击的 Tab 下标
         */
        void onDoubleTap(int index);
    }

    public interface TypefaceProvider {

        boolean isNormalTabBold();

        boolean isSelectedTabBold();
    }

    public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<QRBQMUITabSegment> mTabSegmentRef;

        public TabLayoutOnPageChangeListener(QRBQMUITabSegment tabSegment) {
            mTabSegmentRef = new WeakReference<>(tabSegment);
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            final QRBQMUITabSegment tabSegment = mTabSegmentRef.get();
            if (tabSegment != null) {
                tabSegment.mViewPagerScrollState = state;
            }

        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset,
                                   final int positionOffsetPixels) {
            final QRBQMUITabSegment tabSegment = mTabSegmentRef.get();
            if (tabSegment != null) {
                tabSegment.updateIndicatorPosition(position, positionOffset);
            }
        }

        @Override
        public void onPageSelected(final int position) {
            final QRBQMUITabSegment tabSegment = mTabSegmentRef.get();
            if (tabSegment != null && tabSegment.getSelectedIndex() != position
                    && position < tabSegment.getTabCount()) {
                tabSegment.selectTab(position, true);
            }
        }
    }

    private static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager mViewPager;

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onTabSelected(int index) {
            mViewPager.setCurrentItem(index, false);
        }

        @Override
        public void onTabUnselected(int index) {
        }

        @Override
        public void onTabReselected(int index) {
        }

        @Override
        public void onDoubleTap(int index) {

        }
    }

    public static class Tab {
        public static final int USE_TAB_SEGMENT = Integer.MIN_VALUE;
        private int textSize = USE_TAB_SEGMENT;
        private int normalColor = USE_TAB_SEGMENT;
        private int selectedColor = USE_TAB_SEGMENT;
        private Drawable normalIcon = null;
        private Drawable selectedIcon = null;
        private int contentWidth = 0;
        private int contentLeft = 0;
        private int iconPosition = USE_TAB_SEGMENT;
        private int gravity = Gravity.CENTER;
        private CharSequence text;
        private List<View> mCustomViews;
        private int mSignCountDigits = 2;
        private TextView mSignCountTextView;
        private int mSignCountMarginLeft = 0;
        private int mSignCountMarginTop = 0;
        /**
         * 是否动态更改icon颜色，如果为true, selectedIcon将失效
         */
        private boolean dynamicChangeIconColor = true;

        public Tab(CharSequence text) {
            this.text = text;
        }


        public Tab(Drawable normalIcon, Drawable selectedIcon, CharSequence text, boolean dynamicChangeIconColor) {
            this(normalIcon, selectedIcon, text, dynamicChangeIconColor, true);
        }

        /**
         * 如果你的 icon 显示大小和实际大小不吻合:
         * 1. 设置icon 的 bounds
         * 2. 使用此构造器
         * 3. 最后一个参数（setIntrinsicSize）设置为false
         *
         * @param normalIcon             未选中态 icon
         * @param selectedIcon           选中态 icon
         * @param text                   文字
         * @param dynamicChangeIconColor 是否动态改变 icon 颜色
         * @param setIntrinsicSize       是否设置 icon 的大小为 intrinsic width 和 intrinsic height。
         */
        public Tab(Drawable normalIcon, Drawable selectedIcon, CharSequence text, boolean dynamicChangeIconColor, boolean setIntrinsicSize) {
            this.normalIcon = normalIcon;
            if (this.normalIcon != null && setIntrinsicSize) {
                this.normalIcon.setBounds(0, 0, normalIcon.getIntrinsicWidth(), normalIcon.getIntrinsicHeight());
            }
            this.selectedIcon = selectedIcon;
            if (this.selectedIcon != null && setIntrinsicSize) {
                this.selectedIcon.setBounds(0, 0, selectedIcon.getIntrinsicWidth(), selectedIcon.getIntrinsicHeight());
            }
            this.text = text;
            this.dynamicChangeIconColor = dynamicChangeIconColor;
        }

        /**
         * 设置红点中数字显示的最大位数，默认值为 2，超过这个位数以 99+ 这种形式显示。如：110 -> 99+，98 -> 98
         *
         * @param digit 数字显示的最大位数
         */
        public void setmSignCountDigits(int digit) {
            mSignCountDigits = digit;
        }

        public void setTextColor(@ColorInt int normalColor, @ColorInt int selectedColor) {
            this.normalColor = normalColor;
            this.selectedColor = selectedColor;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public CharSequence getText() {
            return text;
        }

        public void setText(CharSequence text) {
            this.text = text;
        }

        public int getContentLeft() {
            return contentLeft;
        }

        public void setContentLeft(int contentLeft) {
            this.contentLeft = contentLeft;
        }

        public int getContentWidth() {
            return contentWidth;
        }

        public void setContentWidth(int contentWidth) {
            this.contentWidth = contentWidth;
        }

        public int getIconPosition() {
            return iconPosition;
        }

        public void setIconPosition(int iconPosition) {
            this.iconPosition = iconPosition;
        }

        public int getGravity() {
            return gravity;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public int getNormalColor() {
            return normalColor;
        }

        public Drawable getNormalIcon() {
            return normalIcon;
        }

        public int getSelectedColor() {
            return selectedColor;
        }

        public Drawable getSelectedIcon() {
            return selectedIcon;
        }

        public boolean isDynamicChangeIconColor() {
            return dynamicChangeIconColor;
        }

        public void addCustomView(@NonNull View view) {
            if (mCustomViews == null) {
                mCustomViews = new ArrayList<>();
            }
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(getDefaultCustomLayoutParam());
            }
            mCustomViews.add(view);
        }

        public List<View> getCustomViews() {
            return mCustomViews;
        }

        /**
         * 设置红点的位置, 注意红点的默认位置是在内容的右侧并顶对齐
         *
         * @param marginLeft 在红点默认位置的基础上添加的 marginLeft
         * @param marginTop  在红点默认位置的基础上添加的 marginTop
         */
        public void setSignCountMargin(int marginLeft, int marginTop) {
            mSignCountMarginLeft = marginLeft;
            mSignCountMarginTop = marginTop;
            if (mSignCountTextView != null && mSignCountTextView.getLayoutParams() != null) {
                ((MarginLayoutParams) mSignCountTextView.getLayoutParams()).leftMargin = marginLeft;
                ((MarginLayoutParams) mSignCountTextView.getLayoutParams()).topMargin = marginTop;
            }
        }

        private TextView ensureSignCountView(Context context) {
            if (mSignCountTextView == null) {
                mSignCountTextView = new TextView(context, null, R.attr.qmui_tab_sign_count_view);
                RelativeLayout.LayoutParams signCountLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, QMUIResHelper.getAttrDimen(context, R.attr.qmui_tab_sign_count_view_minSize));
                signCountLp.addRule(RelativeLayout.ALIGN_TOP, R.id.qmui_tab_segment_item_id);
                signCountLp.addRule(RelativeLayout.RIGHT_OF, R.id.qmui_tab_segment_item_id);
                mSignCountTextView.setLayoutParams(signCountLp);
                addCustomView(mSignCountTextView);
            }
            // 确保在先 setMargin 后 create 的情况下 margin 会生效
            setSignCountMargin(mSignCountMarginLeft, mSignCountMarginTop);
            return mSignCountTextView;
        }

        /**
         * 显示 Tab 上的未读数或红点
         *
         * @param count 不为0时红点会显示该数字作为未读数,为0时只会显示一个小红点
         */
        public void showSignCountView(Context context, int count) {
            ensureSignCountView(context);
            mSignCountTextView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams signCountLp = (RelativeLayout.LayoutParams) mSignCountTextView.getLayoutParams();
            if (count != 0) {
                // 显示未读数
                signCountLp.height = QMUIResHelper.getAttrDimen(mSignCountTextView.getContext(), R.attr.qmui_tab_sign_count_view_minSize_with_text);
                mSignCountTextView.setLayoutParams(signCountLp);
                mSignCountTextView.setMinHeight(QMUIResHelper.getAttrDimen(mSignCountTextView.getContext(), R.attr.qmui_tab_sign_count_view_minSize_with_text));
                mSignCountTextView.setMinWidth(QMUIResHelper.getAttrDimen(mSignCountTextView.getContext(), R.attr.qmui_tab_sign_count_view_minSize_with_text));
                mSignCountTextView.setText(getNumberDigitsFormattingValue(count));
            } else {
                // 显示红点
                signCountLp.height = QMUIResHelper.getAttrDimen(mSignCountTextView.getContext(), R.attr.qmui_tab_sign_count_view_minSize);
                mSignCountTextView.setLayoutParams(signCountLp);
                mSignCountTextView.setMinHeight(QMUIResHelper.getAttrDimen(mSignCountTextView.getContext(), R.attr.qmui_tab_sign_count_view_minSize));
                mSignCountTextView.setMinWidth(QMUIResHelper.getAttrDimen(mSignCountTextView.getContext(), R.attr.qmui_tab_sign_count_view_minSize));
                mSignCountTextView.setText(null);
            }
        }

        /**
         * 隐藏 Tab 上的未读数或红点
         */
        public void hideSignCountView() {
            if (mSignCountTextView != null) {
                mSignCountTextView.setVisibility(View.GONE);
            }
        }

        /**
         * 获取该 Tab 的未读数
         */
        public int getSignCount() {
            if (mSignCountTextView == null || mSignCountTextView.getVisibility() != VISIBLE) {
                return 0;
            }
            if (!QMUILangHelper.isNullOrEmpty(mSignCountTextView.getText())) {
                return Integer.parseInt(mSignCountTextView.getText().toString());
            } else {
                return 0;
            }
        }

        private RelativeLayout.LayoutParams getDefaultCustomLayoutParam() {
            return new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        private String getNumberDigitsFormattingValue(int number) {
            if (QMUILangHelper.getNumberDigits(number) > mSignCountDigits) {
                String result = "";
                for (int digit = 1; digit <= mSignCountDigits; digit++) {
                    result += "9";
                }
                result += "+";
                return result;
            } else {
                return String.valueOf(number);
            }
        }
    }

    public class TabAdapter extends QMUIItemViewsAdapter<Tab, TabItemView> {
        public TabAdapter(ViewGroup parentView) {
            super(parentView);
        }

        @Override
        protected TabItemView createView(ViewGroup parentView) {
            return new TabItemView(getContext());
        }

        @Override
        protected void bind(Tab item, TabItemView view, int position) {
            TextView tv = view.getTextView();
            setTextViewTypeface(tv, false);
            // custom view
            List<View> mCustomViews = item.getCustomViews();
            if (mCustomViews != null && mCustomViews.size() > 0) {
                view.setTag(R.id.qmui_view_can_not_cache_tag, true);
                for (View v : mCustomViews) {
                    // 防止先 setCustomViews 然后再 updateTabText 时会重复添加 customView 导致 crash
                    if (v.getParent() == null) {
                        view.addView(v);
                    }
                }
            }
            // gravity
            if (mMode == MODE_FIXED) {
                int gravity = item.getGravity();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, (gravity & Gravity.LEFT) == Gravity.LEFT ? RelativeLayout.TRUE : 0);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL, (gravity & Gravity.CENTER) == Gravity.CENTER ? RelativeLayout.TRUE : 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, (gravity & Gravity.RIGHT) == Gravity.RIGHT ? RelativeLayout.TRUE : 0);
                tv.setLayoutParams(lp);
            }

            tv.setText(item.getText());

            // icon
            if (item.getNormalIcon() == null) {
                tv.setCompoundDrawablePadding(0);
                tv.setCompoundDrawables(null, null, null, null);
            } else {
                Drawable drawable = item.getNormalIcon();
                if (drawable != null) {
                    drawable = drawable.mutate();
                    setDrawable(tv, drawable, getTabIconPosition(item));
                    tv.setCompoundDrawablePadding(QMUIDisplayHelper.dp2px(getContext(), 4));
                } else {
                    tv.setCompoundDrawables(null, null, null, null);
                }
            }
            int textSize = item.getTextSize();
            if (textSize == Tab.USE_TAB_SEGMENT) {
                textSize = mTabMaxTextSize;
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            if (position == mSelectedIndex) {
                if (mIndicatorView != null && getViews().size() > 1) {
                    if (mIndicatorDrawable != null) {
                        QMUIViewHelper.setBackgroundKeepingPadding(mIndicatorView, mIndicatorDrawable);
                    } else {
                        if (mIndicatorColor != 0) {
                            mIndicatorView.setBackgroundColor(mIndicatorColor);
                        } else {
                            mIndicatorView.setBackgroundColor(mDefaultSelectedColor);
                        }
                    }
                }
                changeTabColor(view.getTextView(), getTabSelectedColor(item), item, STATUS_SELECTED);
                changeTabTextSize(view.getTextView(), mTabMaxTextSize);
            } else {
                changeTabColor(view.getTextView(), getTabNormalColor(item), item, STATUS_NORMAL);
                changeTabTextSize(view.getTextView(), mTabMinTextSize);
            }

            view.setTag(position);
            view.setOnClickListener(mTabOnClickListener);
        }
    }

    public class InnerTextView extends AppCompatTextView {

        public InnerTextView(Context context) {
            super(context);
        }

        public InnerTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void requestLayout() {
            if (mForceIndicatorNotDoLayoutWhenParentLayout) {
                return;
            }
            super.requestLayout();
        }

        public void setSelectedTextSize(float textSize) {
            getPaint().setTextSize(textSize);
            postInvalidate();
        }
    }

//    private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {
//        private boolean mAutoRefresh;
//        private final boolean mUseAdapterTitle;
//
//        AdapterChangeListener(boolean useAdapterTitle) {
//            mUseAdapterTitle = useAdapterTitle;
//        }
//
//        @Override
//        public void onAdapterChanged(@NonNull ViewPager viewPager,
//                                     @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
//            if (mViewPager == viewPager) {
//                setPagerAdapter(newAdapter, mUseAdapterTitle, mAutoRefresh);
//            }
//        }
//
//        void setAutoRefresh(boolean autoRefresh) {
//            mAutoRefresh = autoRefresh;
//        }
//    }

    public class TabItemView extends RelativeLayout {
        private InnerTextView mTextView;
        private GestureDetector mGestureDetector = null;

        public TabItemView(Context context) {
            super(context);
            mTextView = new InnerTextView(getContext());
            mTextView.setLines(1);
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            // 用于提供给customView布局用
            mTextView.setId(R.id.qmui_tab_segment_item_id);
            RelativeLayout.LayoutParams tvLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            addView(mTextView, tvLp);
            // 添加双击事件
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (mSelectedListeners == null) {
                        return false;
                    } else {
                        if (mIsAnimating) {
                            return false;
                        }
                        int index = (int) TabItemView.this.getTag();
                        Tab model = getAdapter().getItem(index);
                        if (model != null) {
                            dispatchTabDoubleTap(index);
                            return true;
                        }
                        return false;
                    }
                }
            });
        }

        public TextView getTextView() {
            return mTextView;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mSelectedIndex != Integer.MIN_VALUE && mMode == MODE_SCROLLABLE) {
            TabAdapter tabAdapter = getAdapter();
            final TabItemView view = tabAdapter.getViews().get(mSelectedIndex);
            if (getScrollX() > view.getLeft()) {
                scrollTo(view.getLeft(), 0);
            } else {
                int realWidth = getWidth() - getPaddingRight() - getPaddingLeft();
                if (getScrollX() + realWidth < view.getRight()) {
                    scrollBy(view.getRight() - realWidth - getScrollX(), 0);
                }
            }
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        private final boolean mUseAdapterTitle;

        PagerAdapterObserver(boolean useAdapterTitle) {
            mUseAdapterTitle = useAdapterTitle;
        }

        @Override
        public void onChanged() {
            populateFromPagerAdapter(mUseAdapterTitle);
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter(mUseAdapterTitle);
        }
    }

    private final class Container extends ViewGroup {
        private int mLastSelectedIndex = -1;
        private TabAdapter mTabAdapter;

        public Container(Context context) {
            super(context);
            mTabAdapter = new TabAdapter(this);
        }

        public TabAdapter getTabAdapter() {
            return mTabAdapter;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            List<TabItemView> childViews = mTabAdapter.getViews();
            int size = childViews.size();
            int i;

            int visibleChild = 0;
            for (i = 0; i < size; i++) {
                View child = childViews.get(i);
                if (child.getVisibility() == VISIBLE) {
                    visibleChild++;
                }
            }
            if (size == 0 || visibleChild == 0) {
                setMeasuredDimension(widthSpecSize, heightSpecSize);
                return;
            }

            int childHeight = heightSpecSize - getPaddingTop() - getPaddingBottom();
            int childWidthMeasureSpec, childHeightMeasureSpec, resultWidthSize = 0;
            if (mMode == MODE_FIXED) {
                resultWidthSize = widthSpecSize;
                int modeFixItemWidth = widthSpecSize / visibleChild;
                for (i = 0; i < size; i++) {
                    final View child = childViews.get(i);
                    if (child.getVisibility() != VISIBLE) {
                        continue;
                    }
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(modeFixItemWidth, MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            } else {
                for (i = 0; i < size; i++) {
                    final View child = childViews.get(i);
                    if (child.getVisibility() != VISIBLE) {
                        continue;
                    }
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.AT_MOST);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                    resultWidthSize += child.getMeasuredWidth() + mItemSpaceInScrollMode;
                }
                resultWidthSize -= mItemSpaceInScrollMode;
            }

            if (mIndicatorView != null) {
                ViewGroup.LayoutParams bottomLp = mIndicatorView.getLayoutParams();
                int bottomWidthMeasureSpec = MeasureSpec.makeMeasureSpec(resultWidthSize, MeasureSpec.AT_MOST);
                int bottomHeightMeasureSpec = MeasureSpec.makeMeasureSpec(bottomLp.height, MeasureSpec.EXACTLY);
                mIndicatorView.measure(bottomWidthMeasureSpec, bottomHeightMeasureSpec);
            }

            if (mIndicatorLineView != null) {
                ViewGroup.LayoutParams bottomLp = mIndicatorLineView.getLayoutParams();
                int bottomWidthMeasureSpec = MeasureSpec.makeMeasureSpec(resultWidthSize, MeasureSpec.AT_MOST);
                int bottomHeightMeasureSpec = MeasureSpec.makeMeasureSpec(bottomLp.height, MeasureSpec.EXACTLY);
                mIndicatorLineView.measure(bottomWidthMeasureSpec, bottomHeightMeasureSpec);
            }
            setMeasuredDimension(resultWidthSize, heightSpecSize);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            List<TabItemView> childViews = mTabAdapter.getViews();
            int size = childViews.size();
            int i;
            int visibleChild = 0;
            for (i = 0; i < size; i++) {
                View child = childViews.get(i);
                if (child.getVisibility() == VISIBLE) {
                    visibleChild++;
                }
            }

            if (size == 0 || visibleChild == 0) {
                return;
            }

            indicatorLineLeft = -1;
            indicatorLineWidth = -1;

            int usedLeft = getPaddingLeft();
            for (i = 0; i < size; i++) {
                TabItemView childView = childViews.get(i);
                if (childView.getVisibility() != VISIBLE) {
                    continue;
                }
                final int childMeasureWidth = childView.getMeasuredWidth();
                childView.layout(usedLeft, getPaddingTop(), usedLeft + childMeasureWidth, b - t - getPaddingBottom());


                Tab model = mTabAdapter.getItem(i);
                int oldLeft, oldWidth, newLeft, newWidth;
                oldLeft = model.getContentLeft();
                oldWidth = model.getContentWidth();
                if (mMode == MODE_FIXED && mIsIndicatorWidthFollowContent) {
                    TextView contentView = childView.getTextView();
                    newLeft = usedLeft + contentView.getLeft();
                    newWidth = contentView.getWidth();
                } else {
                    newLeft = usedLeft;
                    newWidth = childMeasureWidth;
                }
                if (oldLeft != newLeft || oldWidth != newWidth) {
                    model.setContentLeft(newLeft);
                    model.setContentWidth(newWidth);
                }
                usedLeft = usedLeft + childMeasureWidth + (mMode == MODE_SCROLLABLE ? mItemSpaceInScrollMode : 0);

                // 计算 indicator 底线的长度
                // 该底线长度范围为最左边indicatorView左边框到最右边indicatorView右边框的距离
                {
                    if (indicatorLineLeft == -1) {
                        indicatorLineLeft = newLeft;
                    }
                    indicatorLineWidth = (newLeft + newWidth - indicatorLineLeft);
                }
            }


            int index = mSelectedIndex == Integer.MIN_VALUE ? 0 : mSelectedIndex;
            Tab model = mTabAdapter.getItem(index);
            int lineLeft = model.getContentLeft();
            int lineWidth = model.getContentWidth();
            if (mIndicatorView != null) {
                if (visibleChild > 1) {
                    mIndicatorView.setVisibility(VISIBLE);
                    if (mIndicatorTop) {
                        mIndicatorView.layout(lineLeft, 0, lineLeft + lineWidth, mIndicatorHeight);
                    } else {
                        mIndicatorView.layout(lineLeft, b - t - mIndicatorHeight, lineLeft + lineWidth, b - t);
                    }
                } else {
                    mIndicatorView.setVisibility(GONE);
                }
            }

            if (mIndicatorLineView != null) {
                if (visibleChild > 1) {
                    mIndicatorLineView.setVisibility(VISIBLE);
                    if (mIndicatorTop) {
                        mIndicatorLineView.layout(indicatorLineLeft, 0, indicatorLineLeft + indicatorLineWidth, mIndicatorHeight);
                    } else {
                        mIndicatorLineView.layout(indicatorLineLeft, b - t - mIndicatorLineHeight, indicatorLineLeft + indicatorLineWidth, b - t);
                    }
                } else {
                    mIndicatorLineView.setVisibility(GONE);
                }
            }

            mLastSelectedIndex = index;
        }
    }
}
