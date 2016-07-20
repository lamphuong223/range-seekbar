package com.lpphan.rangeseekbar;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by lamphuong.
 */
public class Bar {

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
        float tickDistance = (rightX - leftX) / (tickNumb - 1);
        return (int) ((thumb.x - leftX + tickDistance / 2) / tickDistance);
    }

    public void setTickNumb(int tickNumb) {
        this.tickNumb = tickNumb;
    }

    public void draw(Canvas canvas, Thumb leftThumb, Thumb rightThumb) {
        canvas.drawLine(leftX, y, rightX, y, barPaint);
        canvas.drawLine(leftThumb.x, y, rightThumb.x, y, connectingPaint);
    }
}
