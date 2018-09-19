package com.animal.harness.hodoo.hodooharness.base;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected final String TAG = getClass().getSimpleName();
    public BaseFragment(){}
    public abstract void onFragmentSelected();
    public void onFragmentSelected( int position ) {

    }
}
