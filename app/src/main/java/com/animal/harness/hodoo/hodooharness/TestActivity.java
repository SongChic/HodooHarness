package com.animal.harness.hodoo.hodooharness;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;

import com.animal.harness.hodoo.hodooharness.view.TestView;

import static android.support.constraint.Constraints.TAG;

public class TestActivity extends Activity {
    View testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testView = findViewById(R.id.test_view);
//        testView.setProgress(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        testView.measure(, 0);

    }
}
