package com.gadimi.ali.colorripple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ali on 3/13/18.
 */

public class UiPanel extends SurfaceView implements SurfaceHolder.Callback
{

    public static final String TAG = "ColorRipple";

    private Context context;
    private MainThread mainThread;
    private List<Circle> circles;
    private String[] colorArr;
    private String[] lighterColorArr;
    private int colorPositionToUse = 0;
    private Paint backgroundPaint;

    private int screenWidth = 0;
    private int screenHeight = 0;


    public UiPanel(Context context)
    {
        super(context);
        this.context = context;
        circles = new ArrayList<>();
        colorArr = context.getResources().getStringArray(R.array.colors);
        lighterColorArr = context.getResources().getStringArray(R.array.lighter_colors);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#FF6138"));
        setScreenSize();

        getHolder().addCallback(this);

//        setFocusable(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        mainThread = new MainThread(getHolder(), this);

        mainThread.begin();
        mainThread.start();

        Log.d(MainActivity.TAG, "surfaceCreated: start thread");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        try
        {
            mainThread.finish();
            mainThread.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Circle getCircle(int x, int y)
    {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(colorArr[colorPositionToUse]));

        Paint lighterPaint = new Paint();
        lighterPaint.setColor(Color.parseColor(lighterColorArr[colorPositionToUse]));

        colorPositionToUse++;
        if (colorPositionToUse == colorArr.length)
        {
            colorPositionToUse = 0;
        }

        return new Circle(x, y, paint, lighterPaint, calculateMaximumRadius(x, y));
    }

    public void updateCircles()
    {
        for (int i = 0; i < circles.size(); i++)
        {
            Circle circle = circles.get(i);
            if (circle.isDone)
            {
                backgroundPaint = circle.paint;
                circles.remove(i);
            }
            else
            {
                circle.nextStep();
            }
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);

        canvas.drawPaint(backgroundPaint);

        for (Circle circle : circles)
        {
            canvas.drawCircle(circle.x, circle.y, circle.radius, circle.paint);
            canvas.drawCircle(circle.x, circle.y, circle.clickRippleRadius, circle.clickRipllePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            int x = (int) event.getX();
            int y = (int) event.getY();
            circles.add(getCircle(x, y));
        }

        return true;
    }

    /*
           hw     w
    -------------|
    |      |     |
    |  1   |  2  |
    |      |     |
    |------|-----| hh
    |      |     |
    |  3   | 4   |
    |      |     |
    |______|_____| h

     */
    public int calculateMaximumRadius(int x, int y)
    {
        int halfWidth = screenWidth / 2;
        int halfHeight = screenHeight / 2;
        int maxRadius;

        if (x < halfWidth && y < halfHeight)
        {
            // 1
            maxRadius = (int) Math.sqrt(Math.pow(screenWidth - x, 2) + Math.pow(screenHeight - y, 2));
        }
        else if (x > halfWidth && y < halfHeight)
        {
            // 2
            maxRadius = (int) Math.sqrt(Math.pow(x, 2) + Math.pow(screenHeight - y, 2));
        }
        else if (x < halfWidth && y > halfHeight)
        {
            // 3
            maxRadius = (int) Math.sqrt(Math.pow(screenWidth - x, 2) + Math.pow(y, 2));
        }
        else
        {
            // 4
            maxRadius = (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        return maxRadius + Circle.ERROR_RATE;
    }

    void setScreenSize()
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        Log.d(MainActivity.TAG, "setScreenSize: w:" + screenWidth + "   h:" + screenHeight);
    }
}
