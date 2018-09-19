package com.animal.harness.hodoo.hodooharness.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomTabLayout extends LinearLayout {
    private LinearLayout mWrap;
    public CustomTabLayout(Context context) {
        super(context);
        init();
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init() {
        mWrap = new LinearLayout(getContext());
        this.addView(mWrap);
    }
    public void setTab( String[] tabs ) {
        for ( int i = 0; i < tabs.length; i++ ) {
            TextView tab = new TextView(getContext());
            tab.setText(tabs[i]);
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tab.setLayoutParams(params);
            mWrap.addView(tab);
        }
    }
    public void setColor ( int color ) {
        this.setBackgroundColor(color);
    }

}
