package com.animal.harness.hodoo.hodooharness.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;

import java.util.ArrayList;

import im.dacer.androidcharts.PieHelper;
import im.dacer.androidcharts.PieView;

public class ChartActivityFragment extends BaseFragment {
    PieView mPieView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_activity, container, false);
        mPieView = wrap.findViewById(R.id.pie_view);
        return wrap;
    }

    @Override
    public void onFragmentSelected() {
    }

    @Override
    public void onFragmentSelected(int position) {
        super.onFragmentSelected(position);

        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        pieHelperArrayList.add(new PieHelper(50, Color.parseColor("#ee6156")));
        pieHelperArrayList.add(new PieHelper(25, Color.parseColor("#f99088")));
        pieHelperArrayList.add(new PieHelper(25, Color.parseColor("#fbd9d7")));
        mPieView.setOnPieClickListener(new PieView.OnPieClickListener() {
            @Override
            public void onPieClick(int index) {
                return;
            }
        });
        mPieView.showPercentLabel(false);
        mPieView.setDate(pieHelperArrayList);
//        mPieView.selectedPie(2); //optional

    }
}
