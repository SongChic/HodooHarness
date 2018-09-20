package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.util.HodooUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysHorizontalView extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = DaysHorizontalView.class.getSimpleName();

    private long mDate;
    private int viewWidth;
    private int scrollVal = 0;

    private HorizontalScrollView scrollView;
    private LinearLayout wrap;
    private TextView dateView;

    private LinearLayout layout;

    SimpleDateFormat sdf = new SimpleDateFormat("E");
    SimpleDateFormat dataSdf = new SimpleDateFormat("M/d");
    public DaysHorizontalView(Context context) {
        super(context);
        init();
    }

    public DaysHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DaysHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        wrap = new LinearLayout(getContext());
        scrollView = new HorizontalScrollView(getContext());
        dateView = new TextView(getContext());

        wrap.setOrientation(LinearLayout.VERTICAL);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setOnTouchListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(params);
        dateView.setGravity(Gravity.CENTER_HORIZONTAL);
        wrap.addView(scrollView);
        wrap.addView(dateView);
        this.addView(wrap);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setDate(long date ) {
        mDate = date;
        setView(date);
        scrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.e(TAG, String.format("scrollX : %d", scrollX));
                scrollVal = scrollX;
            }
        });
    }
    private void setView(final long date ) {
        Log.e(TAG, String.format("getWidth() : %d", HodooUtil.getDisplayMetrics(getContext()).widthPixels));
        dateView.setText(dataSdf.format(new Date(date)));
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e(TAG, String.format("global getWidth() : %d", getWidth()));
                layout = new LinearLayout(getContext());
                viewWidth = getWidth() / 7;
                TextView target = null;

                long preTemp = date - ( ( 24 * 15 ) * 60 * 60 *1000 );
                long nextTemp = date + ( ( 24 * 15 ) * 60 * 60 *1000 );
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                while ( preTemp <= nextTemp ) {
                    final DataTextView tv = new DataTextView(getContext());
                    tv.setText( sdf.format(new Date(preTemp)) );
                    if ( date == preTemp ) tv.setTextColor(Color.parseColor("#666666"));
                    else tv.setTextColor(Color.parseColor("#aaaaaa"));
                    tv.setLayoutParams(params);
                    tv.setData(preTemp);
                    tv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorReset();
                            ((TextView) v).setTextColor(Color.parseColor("#666666"));
                            scrollView.smoothScrollTo((int) layout.getChildAt( getIndex(v) ).getX() - (viewWidth * 3), 0);
                            dateView.setText( dataSdf.format( new Date( (Long) (((DataTextView) v).getData()) ) ) );
                            Log.e(TAG, dataSdf.format(new Date((Long) tv.getData())));
                            //colorReset()
                        }
                    });
                    if ( preTemp == date )
                        target = tv;
                    layout.addView(tv);
                    preTemp += 24 * 60 * 60 * 1000;
                }
                final TextView finalTaget = target;
                finalTaget.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.e("DaysHorizontalView", String.format("x : %f", finalTaget.getX()));
                        scrollView.scrollTo((int) finalTaget.getX() - (viewWidth * 3), 0);
                        finalTaget.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

//                DaysHorizontalView.this.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                })
                scrollView.addView(layout);
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
    public void move( boolean state) {
        if ( state )
            scrollView.smoothScrollTo( this.getScrollX() + viewWidth, 0 );
        else
            scrollView.smoothScrollTo( this.getScrollX() - viewWidth, 0 );
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
//                final View view = layout.getChildAt(scrollVal / (240 * 3));
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        colorReset();
                        int index = scrollVal / viewWidth;
                        float x = layout.getChildAt(index).getX();
                        scrollView.smoothScrollTo((int) layout.getChildAt(index).getX(), 0);
                        ((TextView) layout.getChildAt(index + 3)).setTextColor(Color.parseColor("#666666"));
                    }
                });
                Log.e(TAG, "scroll end");
                break;
        }
        return false;
    }
    private void colorReset() {
        for ( int i = 0; i < layout.getChildCount(); i++ )
            if ( ((DataTextView) layout.getChildAt(i)).getTextColor() == Color.parseColor("#666666") )
                ((DataTextView) layout.getChildAt(i)).setTextColor(Color.parseColor("#aaaaaa"));
    }
    private int getIndex( View v ) {
        for ( int i = 0; i < layout.getChildCount(); i++ )
            if ( v == layout.getChildAt(i) ) return i;
        return 0;
    }
}
