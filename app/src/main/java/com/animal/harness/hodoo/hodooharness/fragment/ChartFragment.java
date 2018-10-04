package com.animal.harness.hodoo.hodooharness.fragment;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.animal.harness.hodoo.hodooharness.HodooApplication;
import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.adapter.ChartAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.databinding.ActivityChartBinding;
import com.animal.harness.hodoo.hodooharness.view.DaysHorizontalView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import im.dacer.androidcharts.PieHelper;

public class ChartFragment extends BaseFragment implements View.OnClickListener {
    HodooApplication application;
    ActivityChartBinding binding;

    private ChartAdapter adapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    int deviceWidth = 0;
    int deviceHeight = 0;
    public ChartFragment () {}

    @Override
    public void onFragmentSelected() {
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

        final BaseFragment[] fragments = {
                new ChartInnerFragment(),
                new ChartActivityFragment()
        };
        adapter = new ChartAdapter(getFragmentManager(), fragments);

        binding.chartViewpager.setAdapter(adapter);
        binding.chartViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.innerTab));
        binding.innerTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.chartViewpager.setCurrentItem(tab.getPosition());
                adapter.refresh(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        long date = new Date().getTime();
        ((ChartInnerFragment) fragments[0]).setDate(date);
        binding.dayView.setDate(date, new DaysHorizontalView.DayCallback() {
            @Override
            public void onResult(long date) {

            }

            @Override
            public void onWeekResult(long date) {
                ((ChartInnerFragment) fragments[0]).setDate(date);
            }
        });
        binding.dayView.post(new Runnable() {
            @Override
            public void run() {
                ((ChartInnerFragment) fragments[0]).setBottomPadding( binding.dayView.getMeasuredHeight() );
            }
        });
        binding.dayView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                Log.e(TAG, String.format("binding.dayView height : %d", binding.dayView.getHeight()));
                binding.dayView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        pieHelperArrayList.add(new PieHelper(25f));
        pieHelperArrayList.add(new PieHelper(16f));
        pieHelperArrayList.add(new PieHelper(29f));
        pieHelperArrayList.add(new PieHelper(20f));
        pieHelperArrayList.add(new PieHelper(8f));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
    }
}
