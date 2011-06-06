package org.rhok.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class ReferenceHeightActivity extends Activity
{
    private final static String TAG = "HeightCatcher";
    private String imagePath;
    

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        
        if (intent.hasExtra(HeightCatcher.IMAGE_LOCATION))
        {
            // We better bloody have the image!
            imagePath = intent.getExtras().getString(
                    HeightCatcher.IMAGE_LOCATION);

            setContentView(new ReferenceHeightView(this));
        }
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

       
    }

    private Paint mPaint;
    private Bitmap mBackGroundBitmap;
    private Canvas  mCanvas;
    private Paint mBitmapPaint;

    class ReferenceHeightView extends SurfaceView implements
            SurfaceHolder.Callback
    {
        ReferenceHeightThread mThread;

        private Path mPath;

        public ReferenceHeightView(Context context)
        {
            super(context);
            getHolder().addCallback(this);
            mPath = new Path();
            mBackGroundBitmap = getBitmapViaInSampleSize(imagePath, 16);
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mCanvas = new Canvas(mBackGroundBitmap.copy(Bitmap.Config.ARGB_8888, true));
            mThread = new ReferenceHeightThread(getHolder(), this);
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas)
        {
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(mBackGroundBitmap, 10, 10, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        };
        
        private void touchStart(float x, float y)
        {
            Log.d(TAG, "Starting touch");
            mPath.reset();
            mPath.moveTo(x, y);
        }
        
        private void touchUp()
        {
            Log.d(TAG, "Upping touch");
            mCanvas.drawPath(mPath, mPaint);
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            Log.d(TAG, "Touch Event");
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction())
            {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            }

            return super.onTouchEvent(event);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height)
        {

        }

        public void surfaceCreated(SurfaceHolder holder)
        {
            mThread.setRunning(true);
            mThread.start();
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            // simply copied from sample application LunarLander:
            // we have to tell thread to shut down & wait for it to finish, or
            // else
            // it might touch the Surface after we return and explode
            boolean retry = true;
            mThread.setRunning(false);
            while (retry)
            {
                try
                {
                    mThread.join();
                    retry = false;
                } catch (InterruptedException e)
                {
                    // we will try it again and again...
                }
            }

        }

    }

    class ReferenceHeightThread extends Thread
    {
        private SurfaceHolder mSurfaceHolder;
        private ReferenceHeightView mReferenceHeightView;
        private boolean mRun = false;

        public ReferenceHeightThread(SurfaceHolder surfaceHolder,
                ReferenceHeightView referenceHeightView)
        {
            mSurfaceHolder = surfaceHolder;
            mReferenceHeightView = referenceHeightView;
        }

        public void setRunning(boolean run)
        {
            mRun = run;
        }

        @Override
        public void run()
        {
            Canvas c;
            while (mRun)
            {
                c = null;
                try
                {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder)
                    {
                        mReferenceHeightView.onDraw(c);
                    }
                } finally
                {
                    if (c != null)
                    {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    private Bitmap getBitmapViaInSampleSize(String imagePath, Integer sampleSize)
    {
        if (sampleSize == null)
        {
            sampleSize = 4;
        }
        Log.d(TAG, "about to decode" + imagePath);
        Options options = new Options();
        // options.inJustDecodeBounds = true;
        options.inSampleSize = 4;
        Bitmap image = (Bitmap) BitmapFactory.decodeFile(imagePath, options);
        return image;
    }

    @SuppressWarnings("unused")
    private void debugToToastAndLog(String logMessage)
    {
        Log.d(TAG, logMessage);
        Toast.makeText(ReferenceHeightActivity.this, logMessage,
                Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("unused")
    private void errorToToastAndLog(String errorMessage)
    {
        Log.e(TAG, errorMessage);
        Toast.makeText(ReferenceHeightActivity.this, "ERROR: " + errorMessage,
                Toast.LENGTH_LONG).show();
    }
}
