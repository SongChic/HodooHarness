package com.animal.harness.hodoo.hodooharness.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;

public class PatternFragment extends BaseFragment {
    public PatternFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_pattern, container, false);
//        AVLoadingIndicatorView loading = wrap.findViewById(R.id.avi);
        return wrap;
    }

    @Override
    public void onFragmentSelected() {

    }

    public static PatternFragment newInstance() {
        PatternFragment fragment = new PatternFragment();
        return fragment;
    }
}
