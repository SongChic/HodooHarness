package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import com.animal.harness.hodoo.hodooharness.base.BaseView;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StopWatch extends BaseView<StopWatch> implements Runnable {
    public interface TimeCallback {
        void onResult(int time);
    }
    private boolean startState = false;
    private int mDeviceWidth = 0;
    private int mDeviceHeight = 0;

    int width = 0;
    int height = 0;

    int startX, endX, startY, endY = 0;

    float startAngle = 270;
    float angle = 0;
    int time = 0;
    double mTotalDistance = 0;
    int intervalTime = 1000;

    private TimeCallback mTimeCallback;

    /* minute x, y / meter x, y */
    private float minX, minY, meterX, meterY;

    /* paint (s) */
    private Paint linePaint;
    private Paint backPaint;
    private Paint strokePaint;
    private Paint paint;
    private Paint circlePaint;
    private Paint distancePaint;
    /* paint (e) */

    SimpleDateFormat sdf = new SimpleDateFormat("m");
    private Thread animation = null;

    public StopWatch(Context context) {
        this(context, null);
    }

    public StopWatch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StopWatch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        mDeviceWidth = dm.widthPixels;
        mDeviceHeight = dm.heightPixels + 400;
        width = height = HodooUtil.dpToPx(getContext(), 200);

        startX = (mDeviceWidth / 2) - (width / 2);
        endX = startX + width;
        startY = (mDeviceHeight / 2) - (height);
        endY = startY + height;
        setPaint();
    }
    private void setPaint() {
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3f);
        linePaint.setColor(Color.parseColor("#ee6156"));

        backPaint = new Paint();
        backPaint.setColor(Color.parseColor("#19455b63"));
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(40f);

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.parseColor("#ee6156"));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3f);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ffffff"));

        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#ee6156"));

        distancePaint = new Paint();
        distancePaint.setColor(Color.parseColor("#f99088"));
        distancePaint.setAntiAlias(true);
        distancePaint.setTextSize(HodooUtil.spToPx(getContext(), 27));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Path path = new Path();
        path.moveTo(startX + width / 2, startY - HodooUtil.dpToPx(getContext(), 12));
        path.lineTo(startX + width / 2, startY + height + HodooUtil.dpToPx(getContext(), 12));
        canvas.drawPath(path, linePaint);

        path = new Path();
        path.moveTo(startX - HodooUtil.dpToPx(getContext(), 12), startY + height / 2);
        path.lineTo(startX + width + HodooUtil.dpToPx(getContext(), 12), startY + height / 2);
        canvas.drawPath(path, linePaint);

        RectF rect = new RectF();
        rect.set(startX, startY, endX, endY);

        canvas.drawArc(rect, -90, 360, true, backPaint);
        canvas.drawArc(rect, -90, 360, true, paint);
        canvas.drawArc(rect, -90, 360, true, strokePaint);

        float x, y;
        float r = rect.width() / 2;

        x = rect.centerX() + ((float) Math.cos(Math.toRadians(angle + startAngle)) * r);
        y = rect.centerY() + ((float) Math.sin(Math.toRadians(angle + startAngle)) * r);

        //340, 60
        canvas.drawCircle( x, y, 20f, circlePaint);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#ee6156"));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(HodooUtil.spToPx(getContext(), 40));

        Paint minPaint = new Paint();
        minPaint.setColor(Color.parseColor("#666666"));
        minPaint.setAntiAlias(true);
        minPaint.setTextSize(HodooUtil.spToPx(getContext(), 40));
        float textWidth = textPaint.measureText(sdf.format(new Date( time * 1000 )));

        float minWidth = calcuWidth(
                new String[]{
                        time < 60 ? String.valueOf(time) : sdf.format(new Date( time * 1000 )),
                        time < 60 ? "sec" : "min"
                },
                new Paint[]{
                        textPaint,
                        minPaint
                }
        );
        canvas.drawText(time < 60 ? String.valueOf(time) : sdf.format(new Date( time * 1000 )), startX + (width / 2) - (minWidth / 2) - 20, startY + (height / 2), textPaint);
        canvas.drawText(time < 60 ? "sec" : "min", startX + (width / 2) + (minWidth / 2) - minPaint.measureText("min"), startY + (height / 2), minPaint);

        Paint meterPaint = new Paint();
        meterPaint.setColor(Color.parseColor("#666666"));
        meterPaint.setAntiAlias(true);
        meterPaint.setTextSize(HodooUtil.spToPx(getContext(), 16));

        String meterStr = "m";
        double meter = mTotalDistance;
        if ( mTotalDistance > 1000 ) {
            meterStr = "km";
            meter = meter / 1000;
        }

        float meterWidth = calcuWidth(
          new String[]{ mTotalDistance > 1000 ? String.format("%.1f", meter) : String.valueOf((int) meter), meterStr },
          new Paint[]{distancePaint, meterPaint}
        );
        canvas.drawText(
                mTotalDistance > 1000 ? String.format("%.1f", meter) : String.valueOf((int) meter), //text
                startX + (width / 2) - ( meterWidth / 2 ), //x
                startY + (height / 2) + measureHeight(sdf.format(new Date( time * 1000 )) + 20, textPaint) , //y
                distancePaint //paint
        );
        canvas.drawText(meterStr, startX + (width / 2) + ( meterWidth / 2 ) - meterPaint.measureText(meterStr), startY + (height / 2) + measureHeight(sdf.format(new Date( time * 1000 )) + 20, textPaint) , meterPaint);

        if ( mTimeCallback != null )
            mTimeCallback.onResult(time);

    }
    public int measureHeight(String text, Paint paint) {
        Rect result = new Rect();
        // Measure the text rectangle to get the height
        paint.getTextBounds(text, 0, text.length(), result);
        return result.height();
    }
    public float calcuWidth ( String[] texts, Paint[] paints) {
        float result = 0;
        for ( int i = 0; i < texts.length; i++ )
            result += paints[i].measureText(texts[i]);
        return result;
    }

    public void setCallback( TimeCallback callback ) {
        mTimeCallback = callback;

//        Log.e(TAG, String.format("width : %d", HodooUtil.dpToPx(getContext(), 200)));
    }
    public void start() {
        Log.e(TAG, "start");
        if ( animation == null ) animation = new Thread(this);
        animation.start();
        startState = true;
    }
    public void stop() {
        if ( animation != null )
            animation.interrupt();
        animation = null;
        startState = false;
    }
    public boolean isStart() {
        return startState;
    }
    public void reset() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while( angle > 0 ) {
                    postInvalidate();
                    angle -= 0.1;
                    time -= 0.001;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mTotalDistance = 0;
            }
        }.start();
    }
    public long getTime () {
        return time * 1000;
    }


    @Override
    public void run() {
        while( startState ) {
            if ( angle == 360 ) break;
            postInvalidate();
            angle += 0.1;

            try {
                Thread.sleep(intervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
        }
        startState = false;
    }
    public void setTotalDistance(double totalDistance) {
        mTotalDistance = totalDistance;
        postInvalidate();
    }
    public double getTotalDistance() {
        return mTotalDistance;
    }
}
