package com.animal.harness.hodoo.hodooharness.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.domain.GPSData;
import com.animal.harness.hodoo.hodooharness.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.dacer.androidcharts.PieHelper;
import im.dacer.androidcharts.PieView;

import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.DEBUG;
import static com.animal.harness.hodoo.hodooharness.constant.HodooConstant.LOCATION_DB_NAME;

public class ChartActivityFragment extends BaseFragment {

    DBHelper helper;

    PieView mPieView;

    private long startDate;
    private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy.MM.dd");
    List<GPSData> gpsDatas;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_activity, container, false);
        mPieView = wrap.findViewById(R.id.pie_view);
        helper = new DBHelper(
                getContext(),
                LOCATION_DB_NAME,
                null,
                1
        );
        return wrap;
    }

    @Override
    public void onFragmentSelected() {
    }

    @Override
    public void onFragmentSelected(int position) {
        super.onFragmentSelected(position);

//        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
//        pieHelperArrayList.add(new PieHelper(50, Color.parseColor("#ee6156")));
//        pieHelperArrayList.add(new PieHelper(25, Color.parseColor("#f99088")));
//        pieHelperArrayList.add(new PieHelper(25, Color.parseColor("#fbd9d7")));
        mPieView.setOnPieClickListener(new PieView.OnPieClickListener() {
            @Override
            public void onPieClick(int index) {
                return;
            }
        });
        mPieView.showPercentLabel(false);
//        mPieView.setDate(pieHelperArrayList);
    }
    private void calculation() {
        long nextTemp= 0 ,tempDate = 0;

        try {
            tempDate = fullSdf.parse(fullSdf.format(new Date( startDate - ( ( 24 * 3 ) * 60 * 60 * 1000 ) ))).getTime() - 9 * 60 * 60 * 1000;
            nextTemp = fullSdf.parse(fullSdf.format(new Date( startDate + ( ( 24 * 3 ) * 60 * 60 * 1000 ) ))).getTime() - 9 * 60 * 60 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ( DEBUG ) Log.e(TAG, String.format("startDate : %d", startDate));

        String where = "created between " + tempDate + " and " + nextTemp;

        gpsDatas = helper.selectDBForWhere(where);
        float rest = 0, outdoor = 0, indoor = 0;

        for (int i = 0; i < gpsDatas.size(); i++) {
            if ( gpsDatas.get(i).getType() == 0 )
                outdoor++;
            else if ( gpsDatas.get(i).getType() == 1 )
                indoor++;
            else if ( gpsDatas.get(i).getType() == 2 )
                rest++;
        }


        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();

        if ( gpsDatas.size() > 0 ) {
            float total = gpsDatas.size();
            float restPer = rest / total * 100;
            float outPer = outdoor / total * 100;
            float inPer = indoor / total * 100;
            pieHelperArrayList.add(new PieHelper(restPer, Color.parseColor("#ee6156")));
            pieHelperArrayList.add(new PieHelper(inPer, Color.parseColor("#f99088")));
            pieHelperArrayList.add(new PieHelper(outPer, Color.parseColor("#fbd9d7")));
        } else {
            pieHelperArrayList.add(new PieHelper(100, Color.parseColor("#d3e5ff")));
        }

        mPieView.setDate(pieHelperArrayList);

        if ( DEBUG ) Log.e(TAG, "debug");
    }

    @Override
    public void setDate(long date) {
        super.setDate(date);
        startDate = date;
        if ( DEBUG ) Log.e(TAG, String.format("date : %d", date));
        calculation();
    }
}
