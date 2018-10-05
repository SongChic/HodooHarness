package com.animal.harness.hodoo.hodooharness.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.HodooApplication;
import com.animal.harness.hodoo.hodooharness.MainActivity;
import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.adapter.ChartAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.databinding.ActivityChartBinding;
import com.animal.harness.hodoo.hodooharness.util.DBHelper;
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
    private ChartInnerFragment chartInnerFragment;


    private boolean isFABOpen = false;
    ValueAnimator  colorAnimation;
    ObjectAnimator textColorAnimation;

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

    @SuppressLint("ObjectAnimatorBinding")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        application = (HodooApplication) getActivity().getApplication();
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_chart, container, false);

        int colorFrom = getResources().getColor(R.color.hodoo_menu_active);
        int colorTo = getResources().getColor(R.color.hodoo_point_color);
        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                binding.includeLayout.fab.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
            }

        });

        textColorAnimation = ObjectAnimator.ofInt(binding.includeLayout.fab, "tint", Color.WHITE, Color.DKGRAY);
        textColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                binding.includeLayout.fab.setImageTintList(ColorStateList.valueOf((Integer) animation.getAnimatedValue()));
            }
        });


        for ( String tabStr : getResources().getStringArray(R.array.chart_inner_tab) )
            binding.innerTab.addTab( binding.innerTab.newTab().setText(tabStr) );

        final BaseFragment[] fragments = {
                new ChartInnerFragment(),
                new ChartActivityFragment()
        };
        chartInnerFragment = (ChartInnerFragment) fragments[0];
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
        binding.dayView.setDate(date);
        binding.dayView.setCallback(new DaysHorizontalView.Callback() {
            @Override
            public void onChange(int state, int position) {
                ((ChartInnerFragment) fragments[0]).setDate(binding.dayView.getDate());
            }

            @Override
            public void onDayClick(long date) {

            }

            @Override
            public void onWeekResult(long date) {

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

        binding.includeLayout.fab.setOnClickListener(this);
        binding.includeLayout.fab1.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.fab :
                if ( !isFABOpen )
                    showFABMenu();
                else
                    closeFABMenu();

                binding.includeLayout.floatWrap.setZ(50);
                break;
            case R.id.fab1 :
                AlertDialog builder = new AlertDialog.Builder(getContext())
                        .setTitle("데이터 초기화")
                        .setMessage("저장되있는 데이터가 모두 사라집니다.\n그래도 초기화 하시겠습니까?")
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                DBHelper helper = application.dbHelper;
                                helper.resetDB();
                                helper.onCreate(helper.getWritableDatabase());

                                chartInnerFragment.setDate(binding.dayView.getDate());
                                chartInnerFragment.calculation();

                                Toast.makeText(getContext(), "데이터 초기화가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                closeFABMenu();
                            }
                        })
                        .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                            }
                        }).create();
                builder.show();
                Log.e(TAG, "fab1 click");
                break;
        }
    }
    private void showFABMenu(){
        isFABOpen=true;
        binding.includeLayout.fab1.animate().alpha(1).translationY( -( binding.includeLayout.fab.getHeight() + 50 ) ).scaleX(1).scaleY(1).withLayer();
        Log.e(TAG, String.format("rotation : %f", binding.includeLayout.fab.getRotation()));
        if ( binding.includeLayout.fab.getRotation() != 0 )
            binding.includeLayout.fab.setRotation(0);
        binding.includeLayout.fab.animate().rotationBy(135).withLayer();
        colorAnimation.start();
        textColorAnimation.start();
    }

    private void closeFABMenu(){
        isFABOpen=false;
        binding.includeLayout.fab1.animate().alpha(0).translationY( 0 ).scaleX(0).scaleY(0).withLayer();

        binding.includeLayout.fab.animate().rotationBy(-(binding.includeLayout.fab.getRotation())).withLayer();
        colorAnimation.reverse();
        textColorAnimation.reverse();
    }
}
