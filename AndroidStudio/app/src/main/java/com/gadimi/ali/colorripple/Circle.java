package com.gadimi.ali.colorripple;

import android.graphics.Paint;

/**
 * Created by ali on 3/14/18.
 */

public class Circle
{
    public static final int ERROR_RATE = 10; // 10 pixels

    public int x;
    public int y;
    public boolean isDone = false;
    public int duration = 60; //should take this 60 frame to finish growing to maximum size

    public int radius;
    public Paint paint;
    public int speed;
    public int acceleration;
    public int maxRadius;

    public int clickRippleRadius;
    public int clickRippleMaxRadius;
    public int clickRippleSpeed;
    public int clickRippleAcceleration;
    public Paint clickRipllePaint;
    private int alpha = 255;

    public Circle(int x, int y, Paint paint, Paint lighterPaint, int maxRadius)
    {
        this.x = x;
        this.y = y;

        this.paint = paint;
        this.maxRadius = maxRadius;
        speed = (2 * maxRadius) / duration;
        acceleration = -speed / duration;

        clickRipllePaint = lighterPaint;
        clickRippleMaxRadius = 150;
        clickRippleSpeed = (2 * clickRippleMaxRadius) / duration;
        clickRippleAcceleration = -clickRippleSpeed / duration;
    }

    public void nextStep()
    {
        radius += speed;
        speed += acceleration;


        clickRippleRadius += clickRippleSpeed;
        clickRippleSpeed += clickRippleAcceleration;
        alpha -= 255 / duration;
        clickRipllePaint.setAlpha(alpha);


        if (radius >= maxRadius - ERROR_RATE)
        {
            isDone = true;
        }
    }
}
