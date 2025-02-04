package com.cofe.solution.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class BatteryDrawable extends Drawable {

    private final Paint paint;
    private int batteryLevel = 100;

    public BatteryDrawable() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setBatteryLevel(int level) {
        this.batteryLevel = level;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();

        // Draw battery outline
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        RectF rect = new RectF(10, 10, width - 10, height - 10);
        canvas.drawRect(rect, paint);

        // Fill battery level
        paint.setStyle(Paint.Style.FILL);
        if (batteryLevel > 20) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.RED);
        }
        float fillWidth = (batteryLevel / 100f) * (width - 20);
        canvas.drawRect(10, 10, 10 + fillWidth, height - 10, paint);

        // Draw battery level text
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(batteryLevel + "%", width / 2f, height / 2f + 15, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}
