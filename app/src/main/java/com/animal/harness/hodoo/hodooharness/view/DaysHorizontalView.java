package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysHorizontalView extends HorizontalScrollView {
    private static final String TAG = DaysHorizontalView.class.getSimpleName();
    private long mDate;
    private int viewWidth;
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
    }
    public void setDate( long date ) {
        mDate = date;
        setView(date);
    }
    private void setView(final long date ) {
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LinearLayout layout = new LinearLayout(getContext());
                viewWidth = getWidth() / 7;
                TextView target = null;

                long preTemp = date - ( ( 24 * 15 ) * 60 * 60 *1000 );
                long nextTemp = date + ( ( 24 * 15 ) * 60 * 60 *1000 );
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                while ( preTemp <= nextTemp ) {

                    TextView tv = new TextView(getContext());
                    tv.setText( sdf.format(new Date(preTemp)) );
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

}
