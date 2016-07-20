package com.lpphan.rangeseekbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lamphuong.
 */
public class Thumb {
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

    public Thumb(
            float x,
            float y,
            Paint thumbPaint,
            int normalRadius,
            int pressedRadius
    ) {
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

    public void onActionUp(Bar bar) {
        isPressed = false;
        int index = bar.getNearestTick(this);
        float tickDistance = (bar.rightX - bar.leftX) / (bar.tickNumb - 1);
        x = bar.leftX + tickDistance * index;
    }

    public void setIndex(Bar bar, int index) {
        float tickDistance = (bar.rightX - bar.leftX) / (bar.tickNumb - 1);
        x = bar.leftX + tickDistance * index;
    }

    public boolean isInTargetZone(float eventX, float eventY) {
        return Math.abs(eventX - x) <= radius && Math.abs(eventY - y) <= radius;
    }

}
