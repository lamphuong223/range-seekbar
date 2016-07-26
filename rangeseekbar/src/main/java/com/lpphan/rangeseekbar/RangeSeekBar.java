package com.lpphan.rangeseekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * RangeSeekBar
 */
@SuppressWarnings("unused")
public class RangeSeekBar extends View {

    private static final int DEFAULT_HEIGHT = 70;
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_TICK_COUNT = 100;

    private Thumb leftThumb, rightThumb;
    private Thumb pressedThumb = null;
    private Bar bar;
    private Paint thumbPaint;

    private OnRangeSeekBarChangerListener mListener;

    private int mTickCount;
    private int mLeftIndex = 0;
    private int mRightIndex;
    private int mThumbColor;
    private int mThumbNormalRadius;
    private int mThumbPressedRadius;

    public RangeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RangeSeekBar(Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs) {
        Resources resources = getResources();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);

        try {
            mTickCount = typedArray.getInteger(R.styleable.RangeSeekBar_tick_count, DEFAULT_TICK_COUNT);
            mRightIndex = mTickCount - 1;

            mThumbColor = typedArray.getColor(R.styleable.RangeSeekBar_thumb_color, getResources().getColor(R.color.thumb_default));
            mThumbNormalRadius = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBar_thumb_normal_radius, 12);
            mThumbPressedRadius = typedArray.getDimensionPixelSize(R.styleable.RangeSeekBar_thumb_pressed_radius, 16);
            mLeftIndex = typedArray.getInteger(R.styleable.RangeSeekBar_left_index, 0);
            mRightIndex = typedArray.getInteger(R.styleable.RangeSeekBar_right_index, mRightIndex);

            if (mLeftIndex < 0)
                throw new IllegalArgumentException("Left index must be >= 0");

            if (mRightIndex > mTickCount)
                throw new IllegalArgumentException("Right index must be <= tick count");

        } finally {
            typedArray.recycle();
        }
        setUp();
    }

    private void setUp() {
        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(mThumbColor);

        leftThumb = new Thumb(0, 0, thumbPaint, mThumbNormalRadius, mThumbPressedRadius);
        rightThumb = new Thumb(0, 0, thumbPaint, mThumbNormalRadius, mThumbPressedRadius);

        bar = new Bar(0, 0, 0, Color.BLACK, 1, mThumbColor, 3);
        bar.setTickNumb(mTickCount);

    }

    public void setOnRangeBarChangeListener(OnRangeSeekBarChangerListener onRangeBarChangeListener) {
        mListener = onRangeBarChangeListener;
    }

    /**
     * Set number of ticks
     *
     * @param tickCount Default is 100
     */
    public void setTickCount(int tickCount) {
        mTickCount = tickCount;
        bar.setTickNumb(mTickCount);
        invalidate();
    }


    /**
     * Set thumb's color
     *
     * @param thumbColor Default is orange
     */
    public void setThumbColor(int thumbColor) {
        mThumbColor = thumbColor;
        thumbPaint.setColor(mThumbColor);
        invalidate();
    }

    /**
     * Set thumb's normal radius
     *
     * @param thumbRadius Default is 6dp
     */
    public void setThumbNormalRadius(float thumbRadius) {
        mThumbNormalRadius = (int) (thumbRadius*getResources().getDisplayMetrics().density);
        leftThumb.radius = mThumbNormalRadius;
        rightThumb.radius = mThumbNormalRadius;
        invalidate();
    }

    /**
     * Set thumb's pressed radius
     *
     * @param thumbPressedRadius Default is 8dp
     */
    public void setThumbPressedRadius(float thumbPressedRadius) {
        mThumbPressedRadius = (int) (thumbPressedRadius*getResources().getDisplayMetrics().density);
        leftThumb.pressedRadius = mThumbPressedRadius;
        rightThumb.pressedRadius = mThumbPressedRadius;
        invalidate();
    }

    /**
     * Set index for the Left Thumb
     *
     * @param leftIndex Default is 0
     */
    public void setLeftIndex(int leftIndex) {
        if (leftIndex < 0) {
            throw new IllegalArgumentException("Left index must be >= 0");
        }
        mLeftIndex = leftIndex;
        leftThumb.setIndex(bar, mLeftIndex);
        invalidate();
    }

    /**
     * Set index for the Right thumb
     *
     * @param rightIndex Default is 99
     */
    public void setRightIndex(int rightIndex) {
        if (rightIndex > mTickCount) {
            throw new IllegalArgumentException("Left index must be <= tick count");
        }
        mRightIndex = rightIndex;
        leftThumb.setIndex(bar, mRightIndex);
        invalidate();
    }

    /**
     * Get left index
     *
     * @return int
     */
    public int getLeftIndex() {
        return mLeftIndex;
    }

    /**
     * Get right index
     *
     * @return int
     */
    public int getRightIndex() {
        return mRightIndex;
    }

    /**
     * Get number of tick
     *
     * @return int
     */
    public int getTickCount() {
        return mTickCount;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        bar.draw(canvas, leftThumb, rightThumb);
        leftThumb.draw(canvas);
        rightThumb.draw(canvas);
        if (pressedThumb != null && pressedThumb.isAnimating) {
            invalidate();
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height, width;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(heightSize, DEFAULT_HEIGHT);
        } else {
            height = DEFAULT_HEIGHT;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            width = Math.min(widthSize, DEFAULT_WIDTH);
        } else {
            width = DEFAULT_WIDTH;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressedThumb = checkThumbPressed(eventX, eventY);
                if (pressedThumb == null) {
                    return super.onTouchEvent(event);
                }
                pressedThumb.setPressed(true);
                invalidate();

                setPressed(true);
                return true;
            case MotionEvent.ACTION_MOVE:
                onActionMove(eventX);
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (pressedThumb == null) {
                    return super.onTouchEvent(event);
                }
                onActionUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onActionUp() {
        pressedThumb.onActionUp(bar);
        invalidate();
    }

    private void onActionMove(float eventX) {
        if (eventX >= bar.leftX && eventX <= bar.rightX) {
            pressedThumb.x = eventX;
            invalidate();

            if (leftThumb.x > rightThumb.x) {
                final Thumb temp = leftThumb;
                leftThumb = rightThumb;
                rightThumb = temp;
            }

            int leftIndex = bar.getNearestTick(leftThumb);
            int rightIndex = bar.getNearestTick(rightThumb);

            if (mLeftIndex != leftIndex || mRightIndex != rightIndex) {
                mLeftIndex = leftIndex;
                mRightIndex = rightIndex;

                if (mListener != null) {
                    mListener.onIndexChange(this, mLeftIndex, mRightIndex);
                }
            }

        }

    }

    private Thumb checkThumbPressed(float eventX, float eventY) {
        Thumb result = null;
        boolean isLeftThumbPressed = leftThumb.isInTargetZone(eventX, eventY);
        boolean isRightThumbPressed = rightThumb.isInTargetZone(eventX, eventY);

        if (isLeftThumbPressed && isRightThumbPressed) {
            result = (eventX / getWidth() >= 0.5f) ? leftThumb : rightThumb;
        } else if (isLeftThumbPressed) {
            result = leftThumb;
        } else if (isRightThumbPressed) {
            result = rightThumb;
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        leftThumb.x = (getPaddingLeft() + 20 + leftThumb.normalRadius / 2);
        leftThumb.y = (h + getPaddingTop() + getPaddingBottom()) / 2;
        rightThumb.y = leftThumb.y;
        rightThumb.x = (w - getPaddingRight() - 20 - rightThumb.normalRadius / 2);

        bar.leftX = leftThumb.x;
        bar.rightX = rightThumb.x;
        bar.y = leftThumb.y;

        leftThumb.setIndex(bar, mLeftIndex);
        rightThumb.setIndex(bar, mRightIndex);
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        SavedState state = new SavedState(super.onSaveInstanceState());

        state.tickCount = mTickCount;
        state.leftIndex = mLeftIndex;
        state.rightIndex = mRightIndex;
        state.thumbColor = mThumbColor;
        state.thumbNormalRadius = mThumbNormalRadius;
        state.thumbPressedRadius = mThumbPressedRadius;

        bundle.putParcelable(SavedState.STATE,state);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            SavedState savedState = bundle.getParcelable(SavedState.STATE);

            mTickCount = savedState.tickCount;
            mThumbColor = savedState.thumbColor;
            mThumbNormalRadius = savedState.thumbNormalRadius;
            mThumbPressedRadius = savedState.thumbPressedRadius;
            mLeftIndex = savedState.leftIndex;
            mRightIndex = savedState.rightIndex;

            super.onRestoreInstanceState(savedState.getSuperState());
            return;
        }
        super.onRestoreInstanceState(SavedState.EMPTY_STATE);
    }

    public interface OnRangeSeekBarChangerListener {
        void onIndexChange(RangeSeekBar rangeBar, int leftIndex, int rightIndex);
    }

    static class SavedState extends BaseSavedState {
        static final String STATE = "RangeSeekBar.STATE";

        int tickCount;
        int leftIndex;
        int rightIndex;
        int thumbColor;
        int thumbNormalRadius;
        int thumbPressedRadius;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
