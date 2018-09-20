package com.animal.harness.hodoo.hodooharness.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class DataTextView extends TextView {
    private Object mData;
    private int mColor;
    public DataTextView(Context context) {
        super(context);
    }

    public DataTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DataTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
}
