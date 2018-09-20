package com.animal.harness.hodoo.hodooharness.activity.chart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.animal.harness.hodoo.hodooharness.base.BaseView;
import com.animal.harness.hodoo.hodooharness.domain.ChartData;

import java.util.ArrayList;
import java.util.List;

public class GraphView extends BaseView<GraphView> implements Runnable{
    /* variable */
    private Context mContext;
    private int mDeviceWidth = 0;
    private int mPadding = 0;
    private Activity mActivity;

    private int viewHeight = 0;
    private int maxY = 0;

    public static final int LINE_TYPE_NORMAL = 0;

    /* data */
    List<ChartData> mDatas = new ArrayList<>();
    List<ChartData> mBaseDatas = new ArrayList<>();

//    DataPoint[] mBasePath = null;
//    DataPoint[] mPath = null;
    int x = 100;
    int xWidth = 0;
    int count = 600;
    private Thread animator = null;

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        viewHeight = getHeight();

    }
    public void setmActivity( Activity activity ) {
        mActivity = activity;
    }
    public void start() {
        Log.e(TAG, String.format("view height : %d", getMeasuredHeight() * 80 / 100));
        animator = new Thread(this);
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "draw");
        if ( mBaseDatas.size() == 0 ) return;

        for ( int i = 0; i < mDatas.size(); i++ ) {
            if (mBaseDatas.get(i).getY() >= mDatas.get(i).getY()) {
                mBaseDatas.get(i).setY( mBaseDatas.get(i).getY() - 8 );
            }
        }

        Paint paint = new Paint();
        Path path = new Path();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ee6156"));
        paint.setShader(new LinearGradient(0,0,0,maxY + 500, Color.parseColor("#ee6156"),0xffffffff, Shader.TileMode.CLAMP));
        paint.setDither(true);                    // set the dither to true
        paint.setStyle(Paint.Style.FILL);       // set to STOKE
        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        paint.setPathEffect(new CornerPathEffect(200) );   // set the path effect when they join.
        paint.setAntiAlias(true);
        path.moveTo(mPadding, maxY);
        for (int i = 0; i < mBaseDatas.size(); i++){
            path.lineTo(mBaseDatas.get(i).getX(), mBaseDatas.get(i).getY());
        }
        path.lineTo(mBaseDatas.size() * x, maxY);

        canvas.drawPath(path, paint);

        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#ee6156"));
        pointPaint.setAntiAlias(true);
        pointPaint.setDither(true);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeJoin(Paint.Join.ROUND);
        pointPaint.setStrokeCap(Paint.Cap.ROUND);
        pointPaint.setStrokeWidth(30);

        Paint pointStrokePaint = new Paint();
        pointStrokePaint.setColor(Color.WHITE);
        pointStrokePaint.setAntiAlias(true);
        pointStrokePaint.setDither(true);
        pointStrokePaint.setStyle(Paint.Style.STROKE);
        pointStrokePaint.setStrokeJoin(Paint.Join.ROUND);
        pointStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        pointStrokePaint.setStrokeWidth(40);

        for (int i = 0; i < mBaseDatas.size(); i++){
            canvas.drawPoint( mBaseDatas.get(i).getX(), mBaseDatas.get(i).getY(), pointStrokePaint );
            canvas.drawPoint( mBaseDatas.get(i).getX(), mBaseDatas.get(i).getY(), pointPaint );
        }
    }
    public void setWidth( int width ) {
        mDeviceWidth = width;
        maxY = getMeasuredHeight() - 200;
    }

    public void setX(final List<ChartData> datas) {
        viewHeight = getHeight();
        Log.e(TAG, String.format("viewHeight : %d", viewHeight));

        x = xWidth = (mDeviceWidth - mPadding) / datas.size();
        if ( mBaseDatas.size() == 0 ) {
            for ( int i = 0; i < mDatas.size(); i++ ) {
                if ( i == 0 ) {
                    mBaseDatas.add(ChartData.builder().x(mPadding).y(maxY).build());
                } else {
                    mBaseDatas.add(ChartData.builder().x(x).y(maxY).build());
                }
                mDatas.get(i).setY( maxY - ( maxY * (mDatas.get(i).getY() / maxY * 100) / 100 ) );
                x += xWidth;
            }
        }
    }
    public void setPoint ( ChartData data ) {
        if ( mDatas == null )
            mDatas = new ArrayList<>();
        mDatas.add(data);
        setX(mDatas);
    }
    public void setPoint ( ChartData[] data ) {
        if ( mDatas == null )
            mDatas = new ArrayList<>();
        for ( int i = 0; i < data.length; i++ )
            mDatas.add(data[i]);
        setX(mDatas);
    }
    public void setPoint ( List<ChartData> data ) {
        mDatas = data;
        setX(mDatas);
    }


    @Override
    public void run() {
        Log.e(TAG, "view in tread");
        while(count >= 0){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            count--;
        }
        animator.interrupt();

    }
}
