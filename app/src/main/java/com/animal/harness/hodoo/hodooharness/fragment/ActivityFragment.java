package com.animal.harness.hodoo.hodooharness.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;
import com.animal.harness.hodoo.hodooharness.view.StopWatch;

public class ActivityFragment extends BaseFragment implements View.OnClickListener {
    private StopWatch stopWatch;
    private Button stopWatchStart;
    private Button stopWatchReset;
    public ActivityFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //stop_watch
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_stopwatch, container, false);
        RelativeLayout btnWrap = wrap.findViewById(R.id.stop_watch_btn);
        stopWatchStart = wrap.findViewById(R.id.stop_watch_start);
        stopWatchReset = wrap.findViewById(R.id.stop_watch_reset);
        stopWatchStart.setOnClickListener(this);
        stopWatchReset.setOnClickListener(this);
        stopWatch = wrap.findViewById(R.id.stop_watch);

        return wrap;
    }

    @Override
    public void onFragmentSelected() {

    }

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_watch_start :
                @SuppressLint("ObjectAnimatorBinding") final ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(v,
                        "textColor",
                        new ArgbEvaluator(),
                        Color.WHITE,
                        Color.BLACK);
                backgroundColorAnimator.setDuration(1000);
                final TransitionDrawable background = (TransitionDrawable) v.getBackground();
                if ( stopWatch.isStart() ) {

                    stopWatchReset.animate().alpha(0).setDuration(500).withLayer().withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            stopWatchReset.setVisibility(View.GONE);
                        }
                    });
                    v.animate().translationX( 0 ).setDuration(500).withLayer();
                    stopWatch.stop();
                    background.reverseTransition(1000);
                    backgroundColorAnimator.reverse();
                    ((TextView) v).setText("시작");
                } else {
                    stopWatchReset.setVisibility(View.VISIBLE);
                    stopWatchReset.animate().alpha(1).setDuration(1000).withLayer();
                    v.animate().translationX( -(v.getWidth() / 2 + 50) ).setDuration(500).withLayer();
                    backgroundColorAnimator.start();
                    background.startTransition(1000);

                    ((TextView) v).setText("중지");
                    if ( stopWatch != null )
                        stopWatch.start();
                }
                break;
            case R.id.stop_watch_reset :
                Log.e(TAG, "stopWatchReset");
                break;
        }
    }
}
