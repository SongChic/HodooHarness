package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.util.HodooUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysHorizontalView extends HorizontalScrollView implements View.OnTouchListener {
    private static final String TAG = DaysHorizontalView.class.getSimpleName();

    private long mDate;
    private int viewWidth;
    private int scrollVal = 0;
    private LinearLayout layout;

    SimpleDateFormat sdf = new SimpleDateFormat("E");
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
        this.setHorizontalScrollBarEnabled(false);
        this.setOnTouchListener(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setDate(long date ) {
        mDate = date;
        setView(date);
        this.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.e(TAG, String.format("scrollX : %d", scrollX));
                scrollVal = scrollX;
            }
        });
    }
    private void setView(final long date ) {
        Log.e(TAG, String.format("getWidth() : %d", HodooUtil.getDisplayMetrics(getContext()).widthPixels));
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

                    TextView tv = new TextView(getContext());
                    tv.setText( sdf.format(new Date(preTemp)) );
                    tv.setTextColor(Color.parseColor("#aaaaaa"));
                    tv.setLayoutParams(params);
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
                        DaysHorizontalView.this.scrollTo((int) finalTaget.getX() - (viewWidth * 3), 0);
                        finalTaget.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

//                DaysHorizontalView.this.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                })
                DaysHorizontalView.this.addView(layout);
                DaysHorizontalView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
    public void move( boolean state) {
        if ( state )
            this.smoothScrollTo( this.getScrollX() + viewWidth, 0 );
        else
            this.smoothScrollTo( this.getScrollX() - viewWidth, 0 );
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
//                final View view = layout.getChildAt(scrollVal / (240 * 3));
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        int index = scrollVal / viewWidth;
                        float x = layout.getChildAt(index).getX();
                        smoothScrollTo((int) layout.getChildAt(index).getX(), 0);
                        ((TextView) layout.getChildAt(index)).setTextColor(Color.parseColor("#666666"));
                    }
                });
                Log.e(TAG, "scroll end");
                break;
        }
        return false;
    }
}
