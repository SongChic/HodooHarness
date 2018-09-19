package com.animal.harness.hodoo.hodooharness.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.animal.harness.hodoo.hodooharness.base.BaseView;
import com.animal.harness.hodoo.hodooharness.domain.PieData;

import java.util.List;

public class PieView extends BaseView<PieView> implements Runnable {
    private List<PieData> mData;
    private Thread mAnimator;
    private final float ANGEL = 360;
    private float startAngel = -90;
    private Activity mActivity;
    private boolean state = true;

    public PieView(Context context) {
        super(context);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public PieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( mData != null ) {
            for ( int i = 0; i < mData.size(); i++ ) {

            if ( mData.get(i).getEnd() > -(ANGEL * mData.get(0).getPercent() / 100) )
                mData.get(i).setEnd( mData.get(i).getEnd() + -2 );
//                endAngel += -2;
            else state = false;

                Paint pnt = new Paint();
                pnt.setStrokeWidth(6f);
                pnt.setColor(Color.parseColor("#FF0000"));
                pnt.setStyle(Paint.Style.FILL);

                RectF rect = new RectF();


                rect.set(200, 200, 600, 600);
                //ANGEL * mData.get(0).getPercent() / 100

//                canvas.drawArc(rect, mData.get(i).getStart(), mData.get(i).getEnd(), true, pnt);
                float degrees = 270;
                float radians = (float) (degrees * (Math.PI / 180));
                canvas.drawArc(rect, 0, 270, true, pnt);

//                canvas.drawArc(rect, testStartAngel, endAngel, true, pnt);

                Paint strokePaint = new Paint();
                strokePaint.setStrokeWidth(6f);
                strokePaint.setColor(Color.parseColor("#ffffff"));
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setStrokeWidth(10f);

//                canvas.drawArc(rect, mData.get(i).getStart(), mData.get(i).getEnd(), true, strokePaint);
//                canvas.drawArc(rect, startAngel, endAngel, true, strokePaint);
//                startAngel -= (ANGEL * mData.get(0).getPercent() / 100);
            }
        }
    }
    public void setData (Activity activity, List<PieData> data ) {
        mActivity = activity;
        mData = data;
        for ( int i = 0; i < data.size(); i++ ) {
            mData.get(i).setStart( startAngel );
            startAngel = startAngel - (ANGEL * data.get(i).getPercent() / 100);
//            mData.get(i).setEnd( startAngel );

        }
//        mData = data;
        mAnimator = new Thread(this);
        mAnimator.start();
    }

    @Override
    public void run() {
        while (state) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mAnimator.interrupt();
    }
}
