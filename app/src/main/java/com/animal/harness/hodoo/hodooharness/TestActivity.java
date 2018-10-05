package com.animal.harness.hodoo.hodooharness;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.animal.harness.hodoo.hodooharness.view.DaysHorizontalView;

import java.util.Date;

import static android.support.constraint.Constraints.TAG;

public class TestActivity extends Activity {
    DaysHorizontalView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testView = findViewById(R.id.test_view);
        testView.setDate(new Date().getTime());
        testView.setCallback(new DaysHorizontalView.Callback() {
            @Override
            public void onChange(int state, int position) {
                if ( state == DaysHorizontalView.PREV_STEP )
                    Log.e(TAG, "prev");
                else if ( state == DaysHorizontalView.NEXT_STEP )
                    Log.e(TAG, "next");
            }

            @Override
            public void onDayClick(long date) {

            }

            @Override
            public void onWeekResult(long date) {

            }
        });
//        testView.setProgress(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        testView.measure(, 0);

    }
}
