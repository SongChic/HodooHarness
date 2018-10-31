package com.animal.harness.hodoo.hodooharness.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.activity.chart.GraphView;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.domain.ChartData;
import com.animal.harness.hodoo.hodooharness.domain.GPSData;
import com.animal.harness.hodoo.hodooharness.util.DBHelper;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartInnerFragment extends BaseFragment {
    private GraphView mGraphView;
    private LinearLayout infoWrap;
    private DBHelper helper;
    private String[] cahrtTexts;
    private SimpleDateFormat sdf = new SimpleDateFormat("m");
    private TextView minView, meterView, kmhView;
    private long mStartDate;
    private int mBottomPadding = 0;

    SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy.MM.dd");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_chart, container, false);
        mGraphView = wrap.findViewById(R.id.graph_view);
        infoWrap = wrap.findViewById(R.id.info_wrap);
        init();
        return wrap;
    }
    private void init() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(HodooUtil.dpToPx(getContext(), 70), HodooUtil.dpToPx(getContext(), 70));

        cahrtTexts = getResources().getStringArray(R.array.chart_text);
        for ( int i = 0; i < 3; i++ ) {
            RelativeLayout info = new RelativeLayout(getContext());
            LinearLayout min = new LinearLayout(getContext());
            min.setLayoutParams(params);
            min.setPadding(20, 20, 20, 20);

            min.setGravity(Gravity.CENTER);
            min.setBackgroundResource(R.drawable.info_stroke_round);
            min.setOrientation(LinearLayout.VERTICAL);

            TextView num = new TextView(getContext());
            TextView text = new TextView(getContext());

            num.setGravity(Gravity.CENTER);
            text.setGravity(Gravity.CENTER);

            switch (i) {
                case 0:
                    minView = num;
                    break;
                case 1:
                    meterView = num;
                    break;
                case 2:
                    kmhView = num;
                    break;
            }
            num.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            num.setTextColor(Color.parseColor("#666666"));

            text.setText(cahrtTexts[i]);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11 );
            text.setTextColor(Color.parseColor("#aaaaaa"));

            min.addView(num);
            min.addView(text);
            info.addView(min);
            info.setBackgroundResource(R.drawable.info_round);
            info.setPadding(20, 20, 20, 20);
            infoWrap.addView(info);

            if ( i != 2 ) {
                TextView tv = new TextView(getContext());
                tv.setText(":");
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.hodoo_menu_default));
                tv.setTextSize( 50 );

                Log.e(TAG, String.format("tv height : %d", tv.getHeight()));
                infoWrap.addView(tv);
            }
        }
        calculation();

        infoWrap.bringToFront();
    }

    @Override
    public void onFragmentSelected() {
        Log.e(TAG, "onFragmentSelected");
    }

    @Override
    public void onFragmentSelected(int position) {
        super.onFragmentSelected(position);
        Log.e(TAG, "onFragmentSelected");
        calculation();
        setData();

    }
    public void calculation() {
        helper = new DBHelper(getActivity());
        long tempDate = mStartDate - ( ( 24 * 3 ) * 60 * 60 * 1000 );
        long nextTemp = mStartDate + ( ( 24 * 3 ) * 60 * 60 * 1000 );
        String where = "created between " + tempDate + " and " + nextTemp;

        List<GPSData> datas = helper.selectDBForWhere(where);

        float kmh = 0;
        long totalTime = 0;
        double totalDistance = 0;

        if ( datas == null )
            return;
        for ( int i = 0; i < datas.size(); i++ ) {
            totalTime += datas.get(i).getTotal_time();
            totalDistance += datas.get(i).getSum();
        }
        try {
            double time = ( (totalTime / 1000) / (double) 60 ) / (double) 60;
            float km = (float) (totalDistance / 1000);
            kmh = (float) (km / time);
            if(Float.isInfinite(kmh) || Float.isNaN(kmh)){
                throw new ArithmeticException();
            }
        } catch (ArithmeticException e) {
            e.printStackTrace();
            kmh = 0;
        }

        long min = totalTime / 60 / 1000;
        long hour = min / 60;
        minView.setText(String.valueOf( min ));
        meterView.setText(String.valueOf(Math.round(totalDistance)));
        kmhView.setText(String.format("%.1f", kmh));
    }
    private void setData () {
        mGraphView.initData();
        List<GPSData> gpsDatas;
        final List<ChartData> chartDatas = new ArrayList<>();
        int viewWidth = mGraphView.getMeasuredWidth();
        Log.e(TAG, String.format("width : %d", mGraphView.getMeasuredWidth()));
        int x = viewWidth / 7;

        if ( helper != null ) {
            long tempDate = 0, nextTemp = 0;
            try {
                tempDate = fullSdf.parse(fullSdf.format(new Date( mStartDate - ( ( 24 * 3 ) * 60 * 60 * 1000 ) ))).getTime() - 9 * 60 * 60 * 1000;
                nextTemp = fullSdf.parse(fullSdf.format(new Date( mStartDate + ( ( 24 * 3 ) * 60 * 60 * 1000 ) ))).getTime() - 9 * 60 * 60 * 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String where = "created between " + tempDate + " and " + nextTemp;

            gpsDatas = helper.selectDBForWhere(where);
            long compareDate = 0;
            int count = 0;
                        /* 데이터 가공 */
            List<GPSData> tempData = new ArrayList<>();
            while ( tempDate <= nextTemp ) {
                compareDate = tempDate + 24 * 60 * 60 * 1000;
                double totalDistance = 0;
                long totalTime = 0;
                for ( int i = 0; i < gpsDatas.size(); i++ ) {
                    if ( tempDate <= gpsDatas.get(i).getCreated() && compareDate > gpsDatas.get(i).getCreated() ) {
                        Log.e(TAG, "tempDate : " + fullSdf.format(new Date(tempDate)) + "/" + fullSdf.format(new Date(compareDate)) + "/" + fullSdf.format(new Date(gpsDatas.get(i).getCreated())));
                        totalDistance += gpsDatas.get(i).getSum();
                        totalTime += gpsDatas.get(i).getTotal_time();
                    }
                }
                tempData.add(GPSData.builder().total_time(totalTime).sum(totalDistance).created(tempDate).build());

                tempDate += 24 * 60 * 60 * 1000;
                count++;
            }
            if ( gpsDatas.size() == 0 ) {
                mGraphView.setDataCheck(true);
            }
            gpsDatas = tempData;

            for ( int i = 0; i < 7; i++ ) {
                if ( i < gpsDatas.size() ) {
                    chartDatas.add(ChartData.builder().x(x).y((float) gpsDatas.get(i).getSum()).build());
                } else {
                    chartDatas.add(ChartData.builder().x(x).y(0).build());
                }
                x += viewWidth / 7;
//                x += 100;
            }
        } else {
            for ( int i = 0; i < 7; i++ ) {

            }
        }

        mGraphView.setWidth( 1500, mBottomPadding );
        mGraphView.setPoint(chartDatas);
        mGraphView.start();

    }
    public void setDate ( long date ) {
        mStartDate = date;
        if ( mGraphView != null )
            setData();
        calculation();
        Log.e(TAG, "setDate");
        Log.e(TAG, fullSdf.format(new Date(date)));
    }
    public void setBottomPadding ( int bottomPadding ) {
        mBottomPadding = bottomPadding;
    }
}
