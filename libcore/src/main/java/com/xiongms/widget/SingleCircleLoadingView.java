package com.xiongms.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * 简单圆形加载loading圈
 * add by xiongms 2018-08-02
 */
public class SingleCircleLoadingView extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    private static final String TAG = "SingleCircleLoadingView";

    protected static final float DEFAULT_SIZE = 20.0f;
    protected static final long ANIMATION_START_DELAY = 333;
    protected static final long ANIMATION_DURATION = 1333;
//    protected static final long ANIMATION_START_DELAY = 333;
//    protected static final long ANIMATION_DURATION = 9333;

    private float mRadius;
    private float mViewWidth;
    private float mViewHeight;

    private ValueAnimator mFloatValueAnimator;

    private double mDurationTimePercent = 1.0;

    private static final int OUTER_CIRCLE_ANGLE = 320;
    //最终阶段
    private static final int FINAL_STATE = 2;
    //当前动画阶段
    private int mCurrAnimatorState = 0;
    private Paint mStrokePaint;
    private Paint mBgPaint;
    private RectF mOuterCircleRectF;
    //旋转开始角度
    private int mStartRotateAngle;
    //旋转角度
    private int mRotateAngle;

    public SingleCircleLoadingView(Context context) {
        super(context);

        init(context);
        initParams();
    }

    public SingleCircleLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
        initParams();
    }

    public SingleCircleLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
        initParams();
    }

    void init(Context context) {
        mViewWidth = dip2px(context, DEFAULT_SIZE);
        mViewHeight = dip2px(context, DEFAULT_SIZE);
        mRadius = dip2px(context, mViewHeight * 0.5f - 10);
        setMinimumHeight((int) DEFAULT_SIZE);
        setMinimumWidth((int) DEFAULT_SIZE);
        initAnimators();
    }

    private void initAnimators() {
        mFloatValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mFloatValueAnimator.setRepeatCount(Animation.INFINITE);
        mFloatValueAnimator.setDuration(getAnimationDuration());
        mFloatValueAnimator.setStartDelay(getAnimationStartDelay());
        mFloatValueAnimator.setInterpolator(new LinearInterpolator());
    }

    protected void initParams() {
        //最大尺寸
        float outR = getRadius();
        //小圆尺寸
        float inR = outR * 0.6f;
        //初始化画笔
        initPaint(inR * 0.3f);
        //旋转角度
        mStartRotateAngle = 0;
        //圆范围
        mOuterCircleRectF = new RectF();
        mOuterCircleRectF.set(getViewCenterX() - outR, getViewCenterY() - outR, getViewCenterX() + outR, getViewCenterY() + outR);
    }

    /**
     * 初始化画笔
     */
    private void initPaint(float lineWidth) {
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(lineWidth);
        mStrokePaint.setColor(Color.RED);
        mStrokePaint.setDither(true);
        mStrokePaint.setFilterBitmap(true);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setStrokeJoin(Paint.Join.ROUND);


        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(lineWidth);
        mBgPaint.setColor(Color.LTGRAY);
        mBgPaint.setDither(true);
        mBgPaint.setFilterBitmap(true);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        mRadius = getMeasuredHeight() / 2 - 15;
        initParams();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //外圆

        canvas.drawArc(mOuterCircleRectF, 0, 360, false, mBgPaint);
        canvas.drawArc(mOuterCircleRectF, mStartRotateAngle - 90, mRotateAngle, false, mStrokePaint);
        canvas.restore();
    }


    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "SingleCircleLoadingView1 onAttachedToWindow ");
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "SingleCircleLoadingView1 onDetachedFromWindow ");
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        final boolean visible = (visibility == VISIBLE) && (getVisibility() == VISIBLE);
        if (visible) {
            start();
        } else {
            stop();
        }
    }

    public void setProgressValue(float value) {
        mStartRotateAngle = 0;
        mRotateAngle = (int) (value * 360);
        invalidate();
    }

    protected void computeUpdateValue(float animatedValue) {
        // 先慢后快的动画
//        mStartRotateAngle = (int) (360 * animatedValue) % 360;
//        float changePoint = 0.6f;
//        if (animatedValue <= changePoint) {
//            mRotateAngle = -(int) ((animatedValue / changePoint) * 180);
//        } else {
//            mRotateAngle = -(int) ((1.0f - animatedValue) / (1.0f - changePoint) * 180);
//        }

        // 先快后慢的动画
        mStartRotateAngle = (int) (360 * animatedValue) % 360;
        mRotateAngle = (int) (mStartRotateAngle * 1.2f);
        if(mStartRotateAngle + mRotateAngle > 360) {
            mRotateAngle = 360 - mStartRotateAngle;
        }

    }

    public void onAnimationRepeat(Animator animation) {
//        if (++mCurrAnimatorState > FINAL_STATE) {//还原到第一阶段
//            mCurrAnimatorState = 0;
//        }
    }

    public boolean isRunning() {
        return mFloatValueAnimator.isRunning();
    }

    public void start() {
        if (mFloatValueAnimator.isStarted()) {
            return;
        }

        mFloatValueAnimator.addUpdateListener(this);
        mFloatValueAnimator.addListener(this);
        mFloatValueAnimator.setRepeatCount(Animation.INFINITE);
        mFloatValueAnimator.setDuration(getAnimationDuration());
        mFloatValueAnimator.start();
    }

    public void stop() {
        mFloatValueAnimator.removeAllUpdateListeners();
        mFloatValueAnimator.removeAllListeners();
        mFloatValueAnimator.setRepeatCount(0);
        mFloatValueAnimator.setDuration(0);
        mFloatValueAnimator.cancel();
    }

    @Override
    public final void onAnimationUpdate(ValueAnimator animation) {
        computeUpdateValue((float) animation.getAnimatedValue());
        invalidate();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }
    protected long getAnimationStartDelay() {
        return ANIMATION_START_DELAY;
    }

    protected long getAnimationDuration() {
        return ceil(ANIMATION_DURATION * mDurationTimePercent);
    }

    protected float getIntrinsicHeight() {
        return mViewHeight;
    }

    protected float getIntrinsicWidth() {
        return mViewWidth;
    }

    protected final float getViewCenterX() {
        return getIntrinsicWidth() * 0.5f;
    }

    protected final float getViewCenterY() {
        return getIntrinsicHeight() * 0.5f;
    }

    protected final float getRadius() {
        return mRadius;
    }

    protected static float dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    protected static long ceil(double value) {
        return (long) Math.ceil(value);
    }
}
