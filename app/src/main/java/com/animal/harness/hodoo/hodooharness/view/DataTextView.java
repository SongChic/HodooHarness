package com.animal.harness.hodoo.hodooharness.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;

@SuppressLint("AppCompatCustomView")
public class DataTextView extends TextView {
    private final String TAG = DataTextView.class.getSimpleName();

    private Object mData;
    private int mColor;
    private boolean mState = false;
    public DataTextView(Context context) {
        super(context);
        init();
    }

    public DataTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DataTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init () {
//        Log.e(TAG, String.format("width : %d", getWidth()));
    }
    public void setData (Object data) {
        mData = data;
    }

    public Object getData() {
        return mData;
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mColor = color;
    }
    public int getTextColor() {
        return mColor;
    }
    public void setSelectState( boolean state ) {
        mState = state;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( mState ) {
            Paint paint = new Paint();
            paint.setColor( getContext().getResources().getColor(R.color.hodoo_pink) );
            canvas.drawCircle(getWidth() / 2, 10, 10f, paint);
        }
    }
}
