package com.animal.harness.hodoo.hodooharness.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChartInnerFragment extends BaseFragment {
    private GraphView mGraphView;
    private LinearLayout infoWrap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_chart, container, false);
        mGraphView = wrap.findViewById(R.id.graph_view);
        infoWrap = wrap.findViewById(R.id.info_wrap);
//        infoWrap.setBackgroundResource(R.drawable.info_round);
        init();
        return wrap;
    }
    private void init() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(HodooUtil.dpToPx(getContext(), 70), HodooUtil.dpToPx(getContext(), 70));
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

            num.setText("22");
            num.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            num.setTextColor(Color.parseColor("#666666"));

            text.setText("MINUTES");
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11 );
            text.setTextColor(Color.parseColor("#aaaaaa"));

            min.addView(num);
            min.addView(text);
            info.addView(min);
            info.setBackgroundResource(R.drawable.info_round);
            info.setPadding(20, 20, 20, 20);

            infoWrap.addView(info);
        }
//
        infoWrap.bringToFront();
    }

    @Override
    public void onFragmentSelected() {
        Log.e(TAG, "onFragmentSelected");
    }

    @Override
    public void onFragmentSelected(int position) {
        super.onFragmentSelected(position);
        List<ChartData> datas = new ArrayList<>();
        datas.add(ChartData.builder().x(0).y(200).build());
        datas.add(ChartData.builder().x(200).y(500).build());
        datas.add(ChartData.builder().x(300).y(300).build());
        datas.add(ChartData.builder().x(400).y(400).build());
        datas.add(ChartData.builder().x(500).y(100).build());
        datas.add(ChartData.builder().x(600).y(300).build());
        datas.add(ChartData.builder().x(700).y(200).build());
        datas.add(ChartData.builder().x(800).y(600).build());

        mGraphView.setWidth( 1500 );
        mGraphView.setPoint(datas);
        mGraphView.setmActivity( getActivity() );
        mGraphView.start();

    }
}
