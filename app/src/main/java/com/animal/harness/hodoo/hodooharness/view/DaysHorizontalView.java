package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysHorizontalView extends RelativeLayout implements View.OnTouchListener, View.OnClickListener {
        private String TAG = DaysHorizontalView.class.getSimpleName();

        public interface Callback {
                void onChange( int state, int position );
                void onDayClick( long date );
                void onWeekResult( long date );
        }
        public interface CustomOnClickListener {
                void onClick(View v);
        }

        public static final int PREV_STEP = 0;
        public static final int NEXT_STEP = 1;

        private int SLIDE_LIMIT = 3;

        /* rolling validate(s) */
        private int target = 0;
        private int preTarget = 0;
        private int nextTarget = 0;
        private int width = 0;
        private int mViewWidth = 0;
        private int mPadding = 30;
        /* rolling validate(e) */

        /* data validation(s) */
        private SimpleDateFormat sdf = new SimpleDateFormat("E");
        private SimpleDateFormat todaySdf = new SimpleDateFormat("M/d");
        private SimpleDateFormat convertSdf = new SimpleDateFormat("yyyy.MM.dd");
        private long mDate = 0;
        /* data validation(e) */

        private Callback mCallback;
        private CustomOnClickListener onClickListener;

        private RelativeLayout wrap;
        private ImageButton mPreBtn, mNextBtn;
        private TextView today;
        private DataTextView selectedView;

        public DaysHorizontalView(Context context) {
                this(context, null);
        }

        public DaysHorizontalView(Context context, AttributeSet attrs) {
                this(context, attrs, 0);
        }

        public DaysHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                init();
        }
        private void init() {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);

                wrap = new RelativeLayout(getContext());
                wrap.setGravity(Gravity.CENTER_HORIZONTAL);
                wrap.setLayoutParams(params);
                for ( int i = 0; i < SLIDE_LIMIT; i++ ) {
                        RelativeLayout items = new RelativeLayout(getContext());
                        items.setLayoutParams(params);
                        wrap.addView(items);
                }
                nextTarget = target + 1;
                preTarget = wrap.getChildCount() - 1;

                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

                mPreBtn = new ImageButton(getContext());
                mPreBtn.setImageResource(R.drawable.arrow_left);
                mPreBtn.setBackgroundResource(outValue.resourceId);

                mNextBtn = new ImageButton(getContext());
                mNextBtn.setImageResource(R.drawable.arrow_right);
                mNextBtn.setBackgroundResource(outValue.resourceId);

                LayoutParams rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                rParams.setMargins(0, HodooUtil.dpToPx(getContext(), 10), 0, 0);
                rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mNextBtn.setLayoutParams(rParams);

                rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                rParams.setMargins(0, HodooUtil.dpToPx(getContext(), 10), 0, 0);
                mPreBtn.setLayoutParams(rParams);

                mPreBtn.setOnClickListener(this);
                mNextBtn.setOnClickListener(this);

                today = new TextView(getContext());
                today.setText("test");
                if ( mDate == 0 ) {
                        mDate = new Date().getTime();
                        setDate(mDate);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        wrap.setId(View.generateViewId());
                        today.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                }
                RelativeLayout.LayoutParams alignParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        alignParams.addRule(RelativeLayout.BELOW, wrap.getId());
                alignParams.setMargins(0, 150, 0, 0);
                alignParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                today.setLayoutParams(alignParams);


                this.addView(wrap);
                this.addView(today);
                this.addView(mNextBtn);
                this.addView(mPreBtn);
                this.setOnTouchListener(this);
        }
        public void setCallback( Callback callback ) {
                mCallback = callback;
        }
        public void setDate ( long date ) {
                Log.e(TAG, "setDate");
                try {
                        mDate = convertSdf.parse(convertSdf.format(new Date(date))).getTime();
                } catch (ParseException e) {
                        e.printStackTrace();
                }
                today.setText(todaySdf.format(new Date(mDate)));
        }


        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
                Log.e(TAG, "onLayout");
                if ( width == 0 ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                width = this.getMeasuredWidth();
                                mViewWidth = (width) / 8;

                                for (int i = 0; i < wrap.getChildCount(); i++) {
                                        wrap.getChildAt(i).setX(width);
                                        wrap.getChildAt(i).setZ(0);
                                }
                                wrap.getChildAt(target).setX(0);
                                wrap.getChildAt(preTarget).setX(-(width));
                                wrap.getChildAt(nextTarget).setX(width);
                        }
                        new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                        wrap.setLayoutParams(params);
                                        long tempDate = mDate - (24 * 3 * 60 * 60 * 1000);
                                        DisplayMetrics metrics = HodooUtil.getDisplayMetrics(getContext());
                                        mViewWidth = (metrics.widthPixels) / 8;
                                        setView(tempDate);
                                }
                        });
                }
                super.onLayout(changed, l, t, r, b);
        }
        private void anim( boolean state ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if ( !state ) {
                                wrap.getChildAt(preTarget).animate().translationX(0).withLayer().withStartAction(new Runnable() {
                                        @Override
                                        public void run() {
                                                changeDate(PREV_STEP);
                                                mCallback.onChange(PREV_STEP, preTarget);

                                        }
                                });
                                wrap.getChildAt(target).animate().translationX( width ).withLayer();
                                setPosition(false);
                                wrap.getChildAt(preTarget).setX(-(width));

                        } else {
                                if ( wrap.getChildAt(nextTarget).getX() != width )
                                        wrap.getChildAt(nextTarget).setX(width);
                                wrap.getChildAt(nextTarget).animate().translationX(0).withLayer().withStartAction(new Runnable() {
                                        @Override
                                        public void run() {
                                                changeDate(NEXT_STEP);
                                                mCallback.onChange(NEXT_STEP, nextTarget);
                                        }
                                });
                                wrap.getChildAt(target).animate().translationX( -(width) ).withLayer();
                                wrap.getChildAt(preTarget).setX(width);
                                setPosition(true);
                                wrap.getChildAt(nextTarget).animate().translationX(width).withLayer();
                                wrap.getChildAt(nextTarget).setZ(100);
                        }
                }
        }
        public void setPosition( boolean state ) {
                if ( state ) {
                        preTarget = target;
                        target = nextTarget;
                        nextTarget++;
                        if ( nextTarget > wrap.getChildCount() - 1 )
                                nextTarget = 0;
                } else {
                        nextTarget = target;
                        target = preTarget;
                        preTarget = preTarget - 1;
                        if ( preTarget < 0 )
                                preTarget = wrap.getChildCount() - 1;
                }
        }
        private void changeDate ( int state ) {

                long tempDate = 0;
                if ( state == PREV_STEP ) {
                        mDate = mDate - ( 24 * 7 * 60 * 60 * 1000 );
                } else if ( state == NEXT_STEP ) {
                        mDate = mDate + ( 24 * 7 * 60 * 60 * 1000 );
                }
                tempDate = mDate - (24 * 3 * 60 * 60 * 1000);
                setView(tempDate);
        }
        private void setView( long tempDate ) {
                LinearLayout dayWrap = new LinearLayout(getContext());
                dayWrap.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                for ( int i = 0; i < 7; i++ ) {

                        final DataTextView day = new DataTextView(getContext());
                        day.setPadding(0, 50, 0, 0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                day.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        }
                        Log.e(TAG, "tempDate" + convertSdf.format(new Date( tempDate + ( 24 * i * 60 * 60 * 1000 ) )));
                        Log.e(TAG, "mDate" + convertSdf.format(new Date( mDate )));
                        if ( tempDate + ( 24 * i * 60 * 60 * 1000 ) == mDate ) {
                                day.setSelectState(true);
                                selectedView = day;
                        }
                        day.setData(tempDate + ( 24 * i * 60 * 60 * 1000 ));
                        day.setLayoutParams(params);
                        day.setText( sdf.format( new Date( tempDate + ( 24 * i * 60 * 60 * 1000 ) ) ) );
                        day.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        selectedView.setSelectState(false);
                                        selectedView.invalidate();
                                        selectedView = (DataTextView) v;
                                        selectedView.setSelectState(true);
                                        selectedView.invalidate();
                                        today.setText(todaySdf.format(new Date((Long) day.getData())));
                                        if ( mCallback != null )
                                                mCallback.onDayClick((Long) day.getData());
                                }
                        });
                        dayWrap.addView(day);
                }
                ((RelativeLayout)wrap.getChildAt(target)).removeAllViews();
                ((RelativeLayout)wrap.getChildAt(target)).addView(dayWrap);
                today.setText(todaySdf.format(new Date(mDate)));
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
                return false;
        }
        @Override
        public void onClick(View v) {
                if ( v == mNextBtn )
                        anim(true);
                else if ( v == mPreBtn )
                        anim(false);
        }
        public long getDate () {
                return mDate;
        }
}
