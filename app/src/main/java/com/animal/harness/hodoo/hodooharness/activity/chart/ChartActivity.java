package com.animal.harness.hodoo.hodooharness.activity.chart;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.animal.harness.hodoo.hodooharness.HodooApplication;
import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.adapter.DaysAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseActivity;
import com.animal.harness.hodoo.hodooharness.databinding.ActivityChartBinding;
import com.animal.harness.hodoo.hodooharness.domain.ChartData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartActivity extends BaseActivity<ChartActivity> {
    HodooApplication application;
    ActivityChartBinding binding;
    int deviceWidth = 0;
    int deviceHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (HodooApplication) getApplicationContext();
        deviceWidth = application.getDeviceMetrics().widthPixels;
        deviceHeight = application.getDeviceMetrics().heightPixels;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chart);
        binding.setActivity(this);
        int maxNum = 0;

        List<ChartData> datas = new ArrayList<>();
        datas.add(ChartData.builder().x(100).y(600).build());
        datas.add(ChartData.builder().x(200).y(500).build());
        datas.add(ChartData.builder().x(300).y(300).build());
        datas.add(ChartData.builder().x(400).y(400).build());
        datas.add(ChartData.builder().x(500).y(100).build());
        datas.add(ChartData.builder().x(600).y(300).build());
        datas.add(ChartData.builder().x(700).y(200).build());
        datas.add(ChartData.builder().x(800).y(600).build());
//        for ( int i = 0; i < path.length; i++ ) {
//            if ( maxNum > path[i].y ) {
//                maxNum = (int) path[i].y;
//            }
//        }
        binding.graphView.setWidth(deviceWidth);
        binding.graphView.setPoint(datas);
        binding.graphView.setmActivity(this);
        binding.graphView.start();

        binding.dayView.setAdapter(new DaysAdapter(getSupportFragmentManager(), new Date().getTime()));


    }

    @Override
    protected BaseActivity<ChartActivity> getActivityClass() {
        return this;
    }

}
