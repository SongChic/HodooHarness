package com.animal.harness.hodoo.hodooharness.activity.chart;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitleBar("통계 관리");
        setSupportActionBar(toolbar);
    }

    @Override
    protected BaseActivity<ChartActivity> getActivityClass() {
        return this;
    }

    @Override
    public void setTitleBar(String titleStr) {
        super.setTitleBar(titleStr);
    }
}
