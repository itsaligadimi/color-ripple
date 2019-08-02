package com.gadimi.ali.colorripple;

import android.graphics.Canvas;
import android.provider.Contacts;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by ali on 3/13/18.
 */

public class MainThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private UiPanel uiPanel;
    private Canvas canvas;

    private boolean running = false;

    private long MAX_FPS = 60;
    private long averageFps;
    private int frameCount = 0;
    private long targetTime = 1000/MAX_FPS;
    private long totalTime = 0;
    private long startTime;
    private long frameTime;
    private long waitTime;

    public MainThread(SurfaceHolder surfaceHolder, UiPanel uiPanel)
    {
        this.surfaceHolder = surfaceHolder;
        this.uiPanel = uiPanel;
    }

    @Override
    public void run() {
        super.run();


        while(running)
        {
            startTime = System.nanoTime();

            try
            {
                canvas = surfaceHolder.lockCanvas();

                synchronized (surfaceHolder)
                {
                    if(canvas != null)
                    {
                        uiPanel.updateCircles();
                        uiPanel.draw(canvas);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(canvas != null)
                {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            frameTime = ((System.nanoTime() - startTime) / 1000000);
            waitTime = targetTime - frameTime;
            try
            {
                if(waitTime > 0)
                {
                    sleep(waitTime);
                }
            }
            catch (Exception e){e.printStackTrace();}

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if(frameCount == MAX_FPS)
            {
                averageFps = 1000/((totalTime/frameCount) / 1000000);

                frameCount = 0;
                totalTime = 0;

                Log.d(MainActivity.TAG, "average framePerSecond: " + averageFps);
            }
        }
    }

    public void begin()
    {
        running = true;
    }

    public void finish()
    {
        running = false;
    }
}
