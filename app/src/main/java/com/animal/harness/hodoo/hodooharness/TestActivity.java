package com.animal.harness.hodoo.hodooharness;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.animal.harness.hodoo.hodooharness.view.DaysHorizontalView;
import com.animal.harness.hodoo.hodooharness.view.TestView;

import java.util.Date;

import static android.support.constraint.Constraints.TAG;

public class TestActivity extends Activity {
    TestView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testView = findViewById(R.id.test_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        testView.measure(, 0);

    }
}
