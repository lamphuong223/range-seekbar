package com.ss.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lamphuong.
 */
public class CustomView extends View {
    private static final String TAG = CustomView.class.getSimpleName();

    private final int DEFAULT_HEIGHT = 60;
    private final int DEFAULT_TICK_COUNT = 100;

    private Thumb leftThumb, rightThumb;
    private Thumb pressedThumb = null;
    private Bar bar;

    private OnRangeBarChangerListener mListener;

    private int mTickCount;
    private int mLeftIndex = 0;
    private int mRightIndex;

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomView(Context context) {
        super(context);
        init(context);
    }

    public void setOnRangeBarChangeListener(OnRangeBarChangerListener onRangeBarChangeListener) {
        mListener = onRangeBarChangeListener;
    }

    public void setTictCount(int tickCount) {
        mTickCount = tickCount;
        bar.setTickNumb(mTickCount);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        mTickCount = DEFAULT_TICK_COUNT;
        mRightIndex = mTickCount - 1;

        int thumbColor = context.getResources().getColor(R.color.colorAccent);
        Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(thumbColor);

        leftThumb = new Thumb(0, 0, thumbPaint, 12, 15);
        rightThumb = new Thumb(0, 0, thumbPaint, 12, 15);

        bar = new Bar(0, 0, 0, Color.BLACK, 1, thumbColor, 3);
        bar.setTickNumb(DEFAULT_TICK_COUNT);

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
        int height;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(heightSize, DEFAULT_HEIGHT);
        } else {
            height = DEFAULT_HEIGHT;
        }
        setMeasuredDimension(widthMeasureSpec, height);
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
        pressedThumb.setPressed(false);
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
    }

    public interface OnRangeBarChangerListener {
        void onIndexChange(CustomView customView, int leftIndex, int rightIndex);
    }

    static class Thumb {
        float x, y;
        int radius;
        Paint paint;
        boolean isPressed;
        boolean isAnimating;
        int normalRadius = 12;
        int pressedRadius = 14;
        private long startTime;
        private Paint ripplePaint;
        private DecelerateInterpolator interpolator;
        int duration;

        public Thumb(float x,
                     float y,
                     Paint thumbPaint,
                     int normalRadius,
                     int pressedRadius) {
            this.x = x;
            this.y = y;
            paint = thumbPaint;
            isPressed = false;
            isAnimating = false;
            this.normalRadius = normalRadius;
            this.pressedRadius = pressedRadius;
            ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ripplePaint.setStyle(Paint.Style.FILL);
            ripplePaint.setColor(Color.parseColor("#757575"));
            duration = 500;
        }

        public void setPressed(boolean isPressed) {
            this.isPressed = isPressed;
            isAnimating = true;
            interpolator = new DecelerateInterpolator();
            startTime = System.currentTimeMillis();
        }

        public void draw(Canvas canvas) {
            radius = isPressed ? pressedRadius : normalRadius;
            canvas.drawCircle(x, y, radius, paint);
            if (isAnimating) {
                long time = System.currentTimeMillis();
                float interpolation = interpolator.getInterpolation((time - startTime) / (float) duration);
                float size = pressedRadius * (1 - interpolation) + pressedRadius * 2.5f * interpolation;
                ripplePaint.setAlpha((int) (127 * (1 - interpolation)));
                canvas.drawCircle(x, y, size, ripplePaint);
                if (startTime + duration < time)
                    isAnimating = false;
            }

        }

        public boolean isInTargetZone(float eventX, float eventY) {
            return Math.abs(eventX - x) <= radius && Math.abs(eventY - y) <= radius;
        }

        @Override
        public String toString() {
            return "" + radius;
        }
    }

    static class Bar {
        float leftX, rightX, y;
        int tickNumb;
        Paint barPaint;
        Paint connectingPaint;

        public Bar(float leftX,
                   float rightX,
                   float y,
                   int barColor,
                   int barHeight,
                   int connectingColor,
                   int connectingHeight) {

            this.leftX = leftX;
            this.rightX = rightX;
            this.y = y;

            barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            barPaint.setStyle(Paint.Style.STROKE);
            barPaint.setStrokeWidth(barHeight);
            barPaint.setColor(barColor);

            connectingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            connectingPaint.setStyle(Paint.Style.STROKE);
            connectingPaint.setStrokeWidth(connectingHeight);
            connectingPaint.setColor(connectingColor);
        }

        public int getNearestTick(Thumb thumb) {
            float index = (thumb.x - leftX) * tickNumb / (rightX - leftX);
            return (int) index;
        }

        public void setTickNumb(int tickNumb) {
            this.tickNumb = tickNumb;
        }

        public void draw(Canvas canvas, Thumb leftThumb, Thumb rightThumb) {
            canvas.drawLine(leftX, y, rightX, y, barPaint);
            canvas.drawLine(leftThumb.x, y, rightThumb.x, y, connectingPaint);
        }
    }

}
