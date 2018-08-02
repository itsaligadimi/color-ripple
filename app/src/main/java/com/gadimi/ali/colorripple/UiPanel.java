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

public class UiPanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "ColorRipple";

    private Context context;
    private MainThread mainThread;
    private List<Circle> circles;
    private String[] colorArr;
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
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#4bcffa"));
        setScreenSize();

        getHolder().addCallback(this);

//        setFocusable(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mainThread = new MainThread(getHolder(), this);

        mainThread.begin();
        mainThread.start();

        Log.d(MainActivity.TAG, "surfaceCreated: start thread");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

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

    public Paint getPaint()
    {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(colorArr[colorPositionToUse]));

        colorPositionToUse++;
        if(colorPositionToUse == colorArr.length)
            colorPositionToUse = 0;

        return paint;
    }

    public void updateCircles()
    {
        for (int i = 0; i < circles.size(); i++)
        {
            if(!containsScreen(circles.get(i).radius))
            {
                circles.get(i).radius += circles.get(i).speed;
                if(circles.get(i).speed > 50)
                    circles.get(i).speed +=circles.get(i).acceleration;

                Log.d(MainActivity.TAG, "updateCircles: circle growing");
            }
            else
            {
                backgroundPaint = circles.get(i).paint;
                circles.remove(i);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawPaint(backgroundPaint);

        for(Circle circle: circles)
        {
            canvas.drawCircle(circle.x, circle.y, circle.radius, circle.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            circles.add(new Circle((int)event.getX(), (int)event.getY(), getPaint()));
        }

        return true;
    }

    boolean containsScreen(int radius)
    {
        return (screenWidth + screenHeight < radius);
    }

    void setScreenSize()
    {
        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        Log.d(MainActivity.TAG, "setScreenSize: w:" + screenWidth + "   h:"+screenHeight);
    }
}
