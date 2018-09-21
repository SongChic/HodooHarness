package com.animal.harness.hodoo.hodooharness.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.animal.harness.hodoo.hodooharness.HodooApplication;
import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.adapter.ChartAdapter;
import com.animal.harness.hodoo.hodooharness.adapter.DaysAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.databinding.ActivityChartBinding;
import com.animal.harness.hodoo.hodooharness.domain.ChartData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.dacer.androidcharts.PieHelper;

public class ChartFragment extends BaseFragment implements View.OnClickListener {
    HodooApplication application;
    ActivityChartBinding binding;

    private ChartAdapter adapter;

    int deviceWidth = 0;
    int deviceHeight = 0;
    public ChartFragment () {}

    @Override
    public void onFragmentSelected() {

//        binding.graphView.start();
    }

    public void onFragmentSelected( int position ) {
        Log.e(TAG, "ChartFragment onFragmentSelected");
        adapter.refresh(0);
    }

    public static ChartFragment newInstance() {
        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_chart, container, false);
        for ( String tabStr : getResources().getStringArray(R.array.chart_inner_tab) )
            binding.innerTab.addTab( binding.innerTab.newTab().setText(tabStr) );

        BaseFragment[] fragments = {
                new ChartInnerFragment(),
                new ChartActivityFragment()
        };
        adapter = new ChartAdapter(getFragmentManager(), fragments);
        binding.chartViewpager.setAdapter(adapter);
        binding.chartViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.innerTab));
        binding.innerTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG, String.format("", tab.getPosition()));
                binding.chartViewpager.setCurrentItem(tab.getPosition());
                adapter.refresh(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        binding.dayView.setDate( new Date().getTime() );
//        binding.dayView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                Log.e(TAG, String.format("scroll : %d", binding.dayView.getScrollX()));
//            }
//        });
        binding.leftBtn.setOnClickListener(this);
        binding.rightBtn.setOnClickListener(this);
//        binding.dayView..setOrientation(LinearLayoutManager.HORIZONTAL);
//        binding.dayView.setAdapter(new DaysAdapter(getActivity().getSupportFragmentManager(), new Date().getTime()));
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        pieHelperArrayList.add(new PieHelper(25f));
        pieHelperArrayList.add(new PieHelper(16f));
        pieHelperArrayList.add(new PieHelper(29f));
        pieHelperArrayList.add(new PieHelper(20f));
        pieHelperArrayList.add(new PieHelper(8f));
//        binding.mPieView.setDate(pieHelperArrayList);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
//        binding.graphView.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn :
//                binding.dayView.move(false);
                break;
            case R.id.right_btn :
//                binding.dayView.move(true);
                break;
        }
    }
}
