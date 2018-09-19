package com.animal.harness.hodoo.hodooharness;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.domain.PieData;
import com.animal.harness.hodoo.hodooharness.view.PieView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestActivity extends Activity {
    ImageView imgPointer, imgClock;
    ValueAnimator clockAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imgPointer = findViewById(R.id.imgPointer);
        imgClock = findViewById(R.id.imgClock);

        clockAnimator = animatePointer(TimeUnit.SECONDS.toMillis(60));


        imgClock.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                if (clockAnimator.isPaused()) {
                    clockAnimator.resume();
                    Toast.makeText(getApplicationContext(), "Resumed", Toast.LENGTH_SHORT).show();
                } else if (clockAnimator.isRunning()) {
                    Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
                    clockAnimator.pause();
                } else
                    clockAnimator.start();

            }
        });
    }


    private ValueAnimator animatePointer(long orbitDuration) {
        ValueAnimator anim = ValueAnimator.ofInt(0, 359);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imgPointer.getLayoutParams();
                layoutParams.circleAngle = val;
                imgPointer.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(orbitDuration);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);

        return anim;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if (clockAnimator != null) {
            if (clockAnimator.isPaused()) {
                clockAnimator.resume();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        if (clockAnimator.isRunning()) {
            clockAnimator.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clockAnimator.isRunning()) {
            clockAnimator.cancel();
        }
    }
}
