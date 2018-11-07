package com.xiongms.widget.datetimepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuwan on 2016/9/28.
 */
public class DatePickerView extends View {

    private Context context;
    /**
     * 新增字段 控制是否首尾相接循环显示 默认为循环显示
     */
    private boolean loop = true;
    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 3f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 10;
    private List<String> mDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private Paint mPaint, nPaint;
    private float mMaxTextSize = 80;
    private float mMinTextSize = 40;
    private int mViewHeight;
    private int mViewWidth;
    private float mLastDownY;


    private Paint shadowPaint;

    private Paint dividerPaint;

    private float dividerWidth = 1f;
    private int dividerColor = 0xEEEEEE;

    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private boolean canScroll = true;
    private onSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else {
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            }
            invalidate();
        }
    };

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null) {
            mSelectListener.onSelect(mDataList.get(mCurrentSelected));
        }
    }

    public void setData(List<String> data) {
        mDataList = data;
        mCurrentSelected = data.size() / 4;
        invalidate();
    }

    /**
     * 选择选中的item的index
     *
     * @param selected the selected index
     */
    public void setSelected(int selected) {
        mCurrentSelected = selected;
        if (loop) {
            int distance = mDataList.size() / 2 - mCurrentSelected;
            if (distance < 0) {
                for (int i = 0; i < -distance; i++) {
                    moveHeadToTail();
                    mCurrentSelected--;
                }
            } else if (distance > 0) {
                for (int i = 0; i < distance; i++) {
                    moveTailToHead();
                    mCurrentSelected++;
                }
            }
        }
        invalidate();
    }

    /**
     * 选择选中的内容
     *
     * @param mSelectItem selected item
     */
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelected(i);
                break;
            }
        }
    }

    private void moveHeadToTail() {
        if (loop) {
            String head = mDataList.get(0);
            mDataList.remove(0);
            mDataList.add(head);
        }
    }

    private void moveTailToHead() {
        if (loop) {
            String tail = mDataList.get(mDataList.size() - 1);
            mDataList.remove(mDataList.size() - 1);
            mDataList.add(0, tail);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();

        isInit = true;
        invalidate();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList<>();
        //第一个paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        //第二个paint
        nPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaint.setStyle(Style.FILL);
        nPaint.setTextAlign(Align.CENTER);
        setColors(0xFF333333, 0xFF000000);

        // 阴影paint
        shadowPaint = new Paint();

        // 分割线paint
        dividerPaint = new Paint();
    }

    public void setTextSize(float maxTextSize, float minTextSize) {
        this.mMaxTextSize = maxTextSize;
        this.mMinTextSize = minTextSize;
    }

    /**
     * @param textColor         未选中的文字颜色
     * @param selectedTextColor 选中的文字颜色
     */
    public void setColors(@ColorInt int textColor, @ColorInt int selectedTextColor) {
        nPaint.setColor(textColor);
        mPaint.setColor(selectedTextColor);
    }


    public void setDivider(int color, float width) {
        this.dividerColor = color;
        this.dividerWidth = width;
        dividerPaint.setColor(dividerColor);
        dividerPaint.setStrokeWidth(dividerWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit) {
            drawData(canvas);
            drawShadowLayer(canvas);
            drawDivider(canvas);
        }
    }

    private void drawShadowLayer(Canvas canvas) {

        shadowPaint.setStyle(Paint.Style.FILL);

        // 绘制上半部分阴影
        float left = 0;
        float top = 0;
        float right = mViewWidth;
        float bottom = mViewHeight / 2.0f - MARGIN_ALPHA * mMinTextSize / 2.0f;

        Shader mShader = new LinearGradient(0, 0, 0, bottom,
                new int[]{Color.parseColor("#AFFFFFFF"),
                        Color.parseColor("#00FFFFFF")},
                null, Shader.TileMode.REPEAT);
        shadowPaint.setShader(mShader);

        canvas.drawRect(left, top, right, bottom, shadowPaint);


        // 绘制下半部分阴影
        left = 0;
        top = mViewHeight / 2.0f + MARGIN_ALPHA * mMinTextSize / 2.0f;
        right = mViewWidth;
        bottom = mViewHeight;

        mShader = new LinearGradient(0, 0, 0, bottom,
                new int[]{Color.parseColor("#00FFFFFF"),
                        Color.parseColor("#AFFFFFFF")},
                null, Shader.TileMode.REPEAT);
        shadowPaint.setShader(mShader);

        canvas.drawRect(left, top, right, bottom, shadowPaint);
    }

    private void drawDivider(Canvas canvas) {
        float startX = 0;
        float endX = startX + mViewWidth;

        float firstLineY = mViewHeight / 2.0f + MARGIN_ALPHA * mMinTextSize / 2.0f;
        float secondLineY = mViewHeight / 2.0f - MARGIN_ALPHA * mMinTextSize / 2.0f;

        dividerPaint.setColor(dividerColor);
        dividerPaint.setStrokeWidth(dividerWidth);
        canvas.drawLine(startX, firstLineY, endX, firstLineY, dividerPaint);
        canvas.drawLine(startX, secondLineY, endX, secondLineY, dividerPaint);

    }

    private void drawData(Canvas canvas) {
        // 先绘制选中的text再往上往下绘制其余的text
//        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
//        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(mMaxTextSize);
//        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        canvas.drawText(mDataList.get(mCurrentSelected), x, baseline, mPaint);
//        // 绘制上方data
//        for (int i = 1; (mCurrentSelected - i) >= 0; i++) {
//            drawOtherText(canvas, i, -1);
//        }
//        // 绘制下方data
//        for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++) {
//            drawOtherText(canvas, i, 1);
//        }

        if ((mCurrentSelected - 1) >= 0) {
            drawOtherText(canvas, 1, -1);
        }
        if ((mCurrentSelected + 1) < mDataList.size()) {
            drawOtherText(canvas, 1, 1);
        }
    }

    /**
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = MARGIN_ALPHA * mMinTextSize * position + type * mMoveLen;
//        float scale = parabola(mViewHeight / 4.0f, d);
//        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        nPaint.setTextSize(mMaxTextSize);
//        nPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        FontMetricsInt fmi = nPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected + type * position),
                (float) (mViewWidth / 2.0), baseline, nPaint);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                mMoveLen += (event.getY() - mLastDownY);
                if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
                    if (!loop && mCurrentSelected == 0) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!loop) {
                        mCurrentSelected--;
                    }
                    // 往下滑超过离开距离
                    moveTailToHead();
                    mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
                } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
                    if (mCurrentSelected == mDataList.size() - 1) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!loop) {
                        mCurrentSelected++;
                    }
                    // 往上滑超过离开距离
                    moveHeadToTail();
                    mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
                }
                mLastDownY = event.getY();
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                doUp();
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doUp() {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public interface onSelectListener {
        void onSelect(String text);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return canScroll && super.dispatchTouchEvent(event);
    }

    /**
     * 控制内容是否首尾相连
     *
     * @param isLoop loop scroll or not
     */
    public void setIsLoop(boolean isLoop) {
        loop = isLoop;
    }

}