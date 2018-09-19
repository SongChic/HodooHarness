package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    int intervalTime = 1000;

    private TimeCallback mTimeCallback;

    SimpleDateFormat sdf = new SimpleDateFormat("m");
    private Thread animation = null;

    public StopWatch(Context context) {
        super(context);
        init();
    }

    public StopWatch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StopWatch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        mDeviceWidth = dm.widthPixels;
        mDeviceHeight = dm.heightPixels;
        width = height = HodooUtil.dpToPx(getContext(), 200);

        startX = (mDeviceWidth / 2) - (width / 2);
        endX = startX + width;
        startY = (mDeviceHeight / 2) - (height);
        endY = startY + height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3f);
        linePaint.setColor(Color.parseColor("#ee6156"));
        Path path = new Path();
        path.moveTo(startX + width / 2, startY - HodooUtil.dpToPx(getContext(), 12));
        path.lineTo(startX + width / 2, startY + height + HodooUtil.dpToPx(getContext(), 12));
        canvas.drawPath(path, linePaint);

        path = new Path();
        path.moveTo(startX - HodooUtil.dpToPx(getContext(), 12), startY + height / 2);
        path.lineTo(startX + width + HodooUtil.dpToPx(getContext(), 12), startY + height / 2);
        canvas.drawPath(path, linePaint);

        Paint backPaint = new Paint();
        backPaint.setColor(Color.parseColor("#19455b63"));
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(40f);


        Paint strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.parseColor("#ee6156"));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3f);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ffffff"));

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#ee6156"));



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
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(200);
        float textWidth = textPaint.measureText(sdf.format(new Date( time * 1000 )));
        canvas.drawText(sdf.format(new Date( time * 1000 )), startX + (width / 2) - (textWidth / 2), startY + (height / 2), textPaint);
        if ( mTimeCallback != null )
            mTimeCallback.onResult(time);

    }

    public void setCallback( TimeCallback callback ) {
        mTimeCallback = callback;

//        Log.e(TAG, String.format("width : %d", HodooUtil.dpToPx(getContext(), 200)));
    }
    public void start() {
        if ( animation == null ) animation = new Thread(this);
        animation.start();
        startState = true;
    }
    public void stop() {
        animation.interrupt();
        animation = null;
        startState = false;
    }
    public boolean isStart() {
        return startState;
    }


    @Override
    public void run() {
        while( true ) {
            if ( angle == 360 ) break;
            postInvalidate();
            angle += 0.1;
            time++;
            try {
                Thread.sleep(intervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
