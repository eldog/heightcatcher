package org.rhok.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.BitmapFactory.Options;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawHeightView extends View
{

    private static final int REF_LINE_COLOR = Color.argb(255, 0x02, 0x8E, 0x9B);
    private static final int PER_LINE_COLOR = Color.argb(255, 0x00, 0xC6, 0x18);
    private static final int REF_1_COLOUR = Color.argb(255, 0x13, 0x3C, 0xAC);
    private static final int REF_2_COLOUR = Color.argb(255, 0x06, 0x22, 0x70);
    private static final int PER_1_COLOUR = Color.argb(255, 0x25, 0x94, 0x33);
    private static final int PER_2_COLOUR = Color.argb(255, 0x00, 0x81, 0x11);

    private final static String TAG = "HeightCatcher";
    private final static int RADIUS = 12;
    private Paint mPaint;

    public DrawHeightActivity mActivity;

    private void setPaintColour(int color)
    {
        mPaint.setColor(color);
    }

    // This is pants, effectively the state is where the index points to in the
    // array
    // 0 = ref_xy_1
    // 1 = ref_xy_2
    // 2 = obj_xy_1
    // 3 = obj_xy_2

    public PointF[] coords = new PointF[4];
    private int coordIndex = 0;

    private Bitmap getBitmapViaInSampleSize(String imagePath, Integer sampleSize)
    {
        if (sampleSize == null)
        {
            sampleSize = 4;
        }
        Log.d(TAG, "about to decode" + imagePath);
        Options options = new Options();
        // options.inJustDecodeBounds = true;
        options.inSampleSize = sampleSize;
        Bitmap image = (Bitmap) BitmapFactory.decodeFile(imagePath, options);
        return image;
    }

    private Bitmap originalBitmap;
    private int originalWidth;
    private int originalHeight;
    private Canvas mCanvas;
    private Path mPath;
    private String mImagePath;

    private Paint mBitmapPaint;

    public DrawHeightView(Context c, AttributeSet as)
    {
        super(c, as);
    }

    public void setup(String imagePath, DrawHeightActivity parent)
    {
        mActivity = parent;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mImagePath = imagePath;
        originalBitmap = getBitmapViaInSampleSize(imagePath, 4).copy(
                Bitmap.Config.ARGB_8888, true);
        originalHeight = originalBitmap.getHeight();
        originalWidth = originalBitmap.getWidth();
        mCanvas = new Canvas(originalBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.argb(0, 0, 0, 0));

        canvas.drawBitmap(originalBitmap, 0, 0, mBitmapPaint);

        // Draw the Ref line
        if (coords[0] != null && coords[1] != null)
        {
            setPaintColour(REF_LINE_COLOR);
            canvas.drawLine(coords[0].x, coords[0].y, coords[1].x, coords[1].y,
                    mPaint);
        }

        // Draw the Person line
        if (coords[2] != null && coords[3] != null)
        {
            setPaintColour(PER_LINE_COLOR);
            canvas.drawLine(coords[2].x, coords[2].y, coords[3].x, coords[3].y,
                    mPaint);
        }

        if (coords[0] != null)
        {
            setPaintColour(REF_1_COLOUR);
            canvas.drawCircle(coords[0].x, coords[0].y, RADIUS, mPaint);
        }
        if (coords[1] != null)
        {
            setPaintColour(REF_2_COLOUR);
            canvas.drawCircle(coords[1].x, coords[1].y, RADIUS, mPaint);
        }
        if (coords[2] != null)
        {
            setPaintColour(PER_1_COLOUR);
            canvas.drawCircle(coords[2].x, coords[2].y, RADIUS, mPaint);
        }
        if (coords[3] != null)
        {
            setPaintColour(PER_2_COLOUR);
            canvas.drawCircle(coords[3].x, coords[3].y, RADIUS, mPaint);
        }

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    // Must be between < 4 and >= 0
    public void setCoordIndex(int index)
    {
        coordIndex = index;
    }

    private void touch_start(float x, float y)
    {
        coords[coordIndex] = new PointF(x, y);

        for (PointF point : coords)
        {
            if (point == null)
            {
                return;
            }
        }

        mActivity.setDoneButton(true);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
        case MotionEvent.ACTION_DOWN:
            touch_start(x, y);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            // touch_move(x, y);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            // touch_up();
            invalidate();
            break;
        }
        return false;
    }
}
