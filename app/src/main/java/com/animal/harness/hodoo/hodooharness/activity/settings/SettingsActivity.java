package com.animal.harness.hodoo.hodooharness.activity.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseActivity;

public class SettingsActivity extends BaseActivity<SettingsActivity> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected BaseActivity<SettingsActivity> getActivityClass() {
        return this;
    }
}
