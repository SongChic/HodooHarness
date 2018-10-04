package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class TestView extends AppCompatSeekBar {
    private final boolean TEST = false;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.seekBarStyle);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init() {
        /* 뷰의 크기에 영향을 받으며 아래와 같이 패딩을 설정하면 캔버스를 넓게 사용할 수 있습니다. */
        this.setPadding(0, 100, 0, 100);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        /* 테스트1 */
        /* 시크바 라인 관련 */
        Paint paint = new Paint();
        paint.setColor(0xffff7700);
        paint.setStrokeWidth(11);

        paint = new Paint();
        paint.setColor(0xffff7700);

        paint.setStrokeWidth(2);

        canvas.drawLine(
                0,100,0, 0, paint);
        canvas.drawLine(
                100,100,100, 0, paint);
        canvas.drawLine(
                200,100,200, 0, paint);
        canvas.drawLine(
                300,100,300, 0, paint);
        canvas.drawLine(
                400,100,400, 0, paint);

        /* 테스트2 */
        /* 패스 관련 */
        paint = new Paint();
        paint.setColor(0xffff7700);
        paint.setStrokeWidth(11);
        paint.setStyle(Paint.Style.STROKE);

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(600, 600);
        path.lineTo(800, 800);
        path.close();

        canvas.drawPath(path, paint);

        super.onDraw(canvas);

    }


}
