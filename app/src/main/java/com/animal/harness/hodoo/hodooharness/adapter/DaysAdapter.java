package com.animal.harness.hodoo.hodooharness.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DaysAdapter extends PagerAdapter {
    private Context mContext;
    private List<Long> mItems;
    SimpleDateFormat sdf = new SimpleDateFormat("E");
    public DaysAdapter (Context context, List<Long> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        /* create view */
        int realPos = position % mItems.size();


        LinearLayout wrap = new LinearLayout(mContext);
        TextView tv = new TextView(mContext);
        tv.setText(sdf.format(new Date(mItems.get(realPos))));
        wrap.addView(tv);
        container.addView(wrap);

        return wrap;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
