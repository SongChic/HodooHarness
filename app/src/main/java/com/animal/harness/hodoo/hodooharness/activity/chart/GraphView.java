package com.animal.harness.hodoo.hodooharness.activity.chart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseView;
import com.animal.harness.hodoo.hodooharness.domain.ChartData;

import java.util.ArrayList;
import java.util.List;

public class GraphView extends BaseView<GraphView> implements Runnable {
    /* variable */
    private Context mContext;
    private int mDeviceWidth = 0;
    private int mPadding = 0;
    private Activity mActivity;

    private int viewHeight = 0;
    private int minY = 0;
    private int maxY = 0;

    private boolean animState = false;

    public static final int LINE_TYPE_NORMAL = 0;
    public static final int MAX_Y = 600;

    /* data */
    private boolean mDataCheckState = false;
    List<ChartData> mDatas = new ArrayList<>();
    List<ChartData> mBaseDatas = new ArrayList<>();

//    DataPoint[] mBasePath = null;
//    DataPoint[] mPath = null;
    int x = 100;
    int xWidth = 0;
    int count = 600;
    private Thread animator = null;

    /* tooltip */
    private int tooltipPos = -1;
    private boolean tooltipState = false;
    private float tx = 0, ty = 0;

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
    public void initData() {
        mBaseDatas = new ArrayList<>();
        if ( animator != null ) {
            animator.interrupt();
            animator = null;
            count = 600;
        }


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
        paint.setShader(new LinearGradient(0,0,0, minY + 500, Color.parseColor("#ee6156"),0xffffffff, Shader.TileMode.CLAMP));
        paint.setDither(true);                    // set the dither to true
        paint.setStyle(Paint.Style.FILL);       // set to STOKE
        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        paint.setPathEffect(new CornerPathEffect(200) );   // set the path effect when they join.
        paint.setAntiAlias(true);
        path.moveTo(mPadding, minY);
        for (int i = 0; i < mBaseDatas.size(); i++){
            path.lineTo(mBaseDatas.get(i).getX(), mBaseDatas.get(i).getY());
        }
        path.lineTo(mBaseDatas.size() * x, minY);

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
//            float y = mBaseDatas.get(i).getY() != maxY / 100 * 100  ? mBaseDatas.get(i).getY() + 100 : mBaseDatas.get(i).getY();
            canvas.drawPoint( mBaseDatas.get(i).getX(), mBaseDatas.get(i).getY(), pointStrokePaint );
            canvas.drawPoint( mBaseDatas.get(i).getX(), mBaseDatas.get(i).getY(), pointPaint );
        }
        if ( tooltipState ) {
            /* 안쪽면 */
            Paint tPaint = new Paint();
            tPaint.setAntiAlias(true);
            tPaint.setColor(Color.WHITE);
            tPaint.setStrokeWidth(2);
            RectF rectF = new RectF(tx - 100, ty - 100, tx + 100, ty);
            canvas.drawRoundRect(rectF, 10, 10, tPaint);

            /* 바깥쪽 선 */
            tPaint = new Paint();
            tPaint.setAntiAlias(true);
            tPaint.setStyle(Paint.Style.STROKE);
            tPaint.setStrokeWidth(2);
            tPaint.setColor(mContext.getResources().getColor(R.color.hodoo_menu_active));
            canvas.drawRoundRect(rectF, 10, 10, tPaint);

            /* 삼각형 */
            tPaint = new Paint();
            tPaint.setStyle(Paint.Style.FILL);
            tPaint.setColor(mContext.getResources().getColor(R.color.hodoo_menu_active));
            path = new Path();
            path.moveTo(tx - 20, ty);
            path.lineTo((tx - 20) + 20, ty + 20);
            path.lineTo((tx - 20) + 40, ty);
            path.close();
//            path.lineTo(tx + 50, ty + 50);
            path.close();
            canvas.drawPath(path, tPaint);
        }

        /* 데이터가 없을 시 */
        if ( mDataCheckState ) {
            canvas.drawText("데이터가 없습니다.", 300, 300, new Paint());
        }

    }
    public void setWidth( int width, int bottomPadding ) {
        mDeviceWidth = width;
        minY = getMeasuredHeight() - bottomPadding;
    }

    public void setX(final List<ChartData> datas) {
        Log.e(TAG, String.format("viewHeight : %d", viewHeight));

        x = xWidth = (mDeviceWidth - mPadding) / 8;
        if ( mBaseDatas.size() == 0 ) {
            for ( int i = 0; i < mDatas.size(); i++ ) {
                mBaseDatas.add(ChartData.builder().x(x).y(minY).build());
                mDatas.get(i).setY( minY - ( minY * (mDatas.get(i).getY() / minY * 100) / 100 ) );
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
        viewHeight = getHeight();
        count = maxY = viewHeight / 2;

        /* 데이터 가공 */
        float max = 0;

        for ( int i = 0; i < data.size(); i++ )
            if ( max < data.get(i).getY() )
                max = data.get(i).getY();

        //MAX_Y
        for ( int i = 0; i < data.size(); i++ )
            data.get(i).setY( maxY * ((data.get(i).getY()) / max * 100) / 100 );

        Log.e(TAG, String.format("max : %f", max));
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX(), touchY = event.getY();

        for ( int i = 0; i < mBaseDatas.size(); i++ ) {
            if ( (mBaseDatas.get(i).getX() - 5 <= touchX  && mBaseDatas.get(i).getX() + 5 >= touchX) &&
                    mBaseDatas.get(i).getY() - 5 <= touchY  && mBaseDatas.get(i).getY() + 5 >= touchY) {

                if ( tooltipPos == i || tooltipPos < 0 || !tooltipState )
                    tooltipState = tooltipState ? false : true;
                tooltipPos = i;
                tx = mBaseDatas.get(i).getX();
                ty = mBaseDatas.get(i).getY() - 50;
                postInvalidate();
                break;
            }
        }
        Log.e(TAG, String.format("tooltipPos : %d", tooltipPos));

        Log.e(TAG, "touch");
        return super.onTouchEvent(event);
    }
    public void setTooltipPos( int position ) {
        tx = mBaseDatas.get(position).getX();
        ty = mBaseDatas.get(position).getY();
        postInvalidate();
    }
    public void setDataCheck( boolean checkState ) {
        mDataCheckState = checkState;
    }

}
