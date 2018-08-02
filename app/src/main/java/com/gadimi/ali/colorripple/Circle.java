package com.gadimi.ali.colorripple;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by ali on 3/14/18.
 */

public class Circle {

    public Paint paint;


    public int x;
    public int y;
    public int radius = 0;  // starting radius is 0
    public int speed = 100;
    public int acceleration = -3;


    public Circle(int x, int y, Paint paint)
    {
        this.x = x;
        this.y = y;

        this.paint = paint;
    }
}
