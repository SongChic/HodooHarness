package com.animal.harness.hodoo.hodooharness.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysHorizontalView extends RelativeLayout implements View.OnClickListener {

    public interface DayCallback {
        public void onResult( long date );
        public void onWeekResult( long date );
    }


    private static final String TAG = DaysHorizontalView.class.getSimpleName();

    private Context mContext;

    private LinearLayout mWrap, viewWrap;
    private TextView today;


    private ImageView mPreBtn;
    private ImageView mNextBtn;
    private int padding = 10;
    private int mViewWidth = 0;
    private DataTextView selectView;

    SimpleDateFormat sdf = new SimpleDateFormat("E");
    SimpleDateFormat dataSdf = new SimpleDateFormat("M/d");
    SimpleDateFormat testSdf = new SimpleDateFormat("yyyy.MM.dd");

    private long mDate;
    private DayCallback mCallback;

    public DaysHorizontalView(Context context) {
        super(context);
        init(null);
    }

    public DaysHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DaysHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void init(AttributeSet attr ) {
        if ( attr != null )
            setAttr(attr);
        mContext = getContext();
        DisplayMetrics metrics = HodooUtil.getDisplayMetrics(mContext);
        mViewWidth = (metrics.widthPixels / 7) - (padding * 2);



        mWrap = new LinearLayout(mContext);
        mWrap.setOrientation(LinearLayout.VERTICAL);
        mWrap.setGravity(Gravity.CENTER_HORIZONTAL);
        mWrap.setPadding(HodooUtil.pxToDp(mContext, padding), 0, HodooUtil.pxToDp(mContext, padding), 0);


        mPreBtn = new ImageView(getContext());
        mPreBtn.setImageResource(R.drawable.arrow_left);

        mNextBtn = new ImageView(getContext());
        mNextBtn.setImageResource(R.drawable.arrow_right);

//
//        preBtn.setLayoutParams(params);
//        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
////        params.addRule(RelativeLayout.ALIGN_RIGHT);
//        nextBtn.setLayoutParams(params);
//
        mPreBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewWrap = new LinearLayout(mContext);
        viewWrap.setId(View.generateViewId());

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rParams.setMargins(0, HodooUtil.dpToPx(getContext(), 10), 0, 0);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mNextBtn.setLayoutParams(rParams);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rParams.setMargins(0, HodooUtil.dpToPx(getContext(), 10), 0, 0);
        mPreBtn.setLayoutParams(rParams);

        viewWrap.setPadding(HodooUtil.dpToPx(getContext(), 20), 0, HodooUtil.dpToPx(getContext(), 20), 0);
        today = new TextView(mContext);
        today.setGravity(Gravity.CENTER_HORIZONTAL);
        today.setLayoutParams(params);
        mWrap.addView(viewWrap);
        mWrap.addView(today);
//
        this.addView(mWrap);
        this.addView(mPreBtn);
        this.addView(mNextBtn);

    }
    private void setAttr( AttributeSet attr ) {

    }

    @Override
    public void onClick(View v) {
        if ( v == mPreBtn ) {
            Log.e(TAG, "pre btn");
            changeDate(false);
        } else if ( v == mNextBtn ) {
            Log.e(TAG, "next btn");
            changeDate(true);
        }
    }
    public void setDate ( long date ) {
        setView(date);
    }
    public void setDate ( long date, DayCallback callback ) {
        mCallback = callback;
        setView(date);
    }
    private void setView ( long date ) {
        mDate = date;
        long tempDate = date - ( ( 24 * 3 ) * 60 * 60 *1000 );
        long nextTemp = date + ( ( 24 * 3 ) * 60 * 60 *1000 );

//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        viewWrap.setLayoutParams(params);
        viewWrap.setOrientation(LinearLayout.HORIZONTAL);


        while ( tempDate <= nextTemp ) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

            DataTextView tv = new DataTextView(mContext);
            tv.setData( tempDate );
            if ( tempDate == mDate ) {
                tv.setSelectState(true);
                selectView = tv;
            }
            tv.setText( sdf.format(new Date(tempDate)) );
            tv.setPadding(0, 50, 0, 0);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setLayoutParams(params);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataTextView dv = (DataTextView) v;
                    selectView.setSelectState(false);
                    selectView.invalidate();
                    if ( mCallback != null )
                        mCallback.onResult((Long) dv.getData());
                    today.setText(dataSdf.format(new Date((Long) dv.getData())));
                    dv.setSelectState(true);
                    dv.invalidate();
                    selectView = dv;

                }
            });
            viewWrap.addView(tv);
            tempDate += 24 * 60 * 60 * 1000;
        }

        today.setText(dataSdf.format(new Date(date)));
    }
    private void changeDate ( boolean state ) {
        if ( !state )
            mDate  -= ( ( 24 * 7 ) * 60 * 60 *1000 );
        else
            mDate  += ( ( 24 * 7 ) * 60 * 60 *1000 );
        if ( mCallback != null )
            mCallback.onWeekResult(mDate);
        long tempDate = mDate - ( ( 24 * 3 ) * 60 * 60 *1000 );
        long nextTemp = mDate + ( ( 24 * 3 ) * 60 * 60 *1000 );

        viewWrap.removeAllViews();
        while ( tempDate <= nextTemp ) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

            DataTextView tv = new DataTextView(mContext);
            tv.setData( tempDate );
            if ( tempDate == mDate ) {
                tv.setSelectState(true);
                selectView = tv;
            }
            tv.setText( sdf.format(new Date(tempDate)) );
            tv.setPadding(0, 50, 0, 0);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setLayoutParams(params);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataTextView dv = (DataTextView) v;
                    selectView.setSelectState(false);
                    selectView.invalidate();
                    today.setText(dataSdf.format(new Date((Long) dv.getData())));
                    dv.setSelectState(true);
                    dv.invalidate();
                    selectView = dv;

                }
            });
            viewWrap.addView(tv);
            tempDate += 24 * 60 * 60 * 1000;
        }
        today.setText(dataSdf.format(new Date(mDate)));

        Log.e(TAG, testSdf.format(new Date(mDate)));
    }

}
