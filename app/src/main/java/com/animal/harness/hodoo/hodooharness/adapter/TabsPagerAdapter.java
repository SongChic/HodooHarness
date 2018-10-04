package com.animal.harness.hodoo.hodooharness.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.fragment.ActivityFragment;
import com.animal.harness.hodoo.hodooharness.fragment.ChartFragment;
import com.animal.harness.hodoo.hodooharness.fragment.PatternFragment;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    String[] mTitle;
    BaseFragment[] fragments;
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public TabsPagerAdapter(FragmentManager fm, String[] title) {
        super(fm);
        mTitle = title;
    }

    @Override
    public BaseFragment getItem(int position) {
        Log.e("TAG", "getItem");
        switch (position) {
            case 0 :
                if ( fragments[0] == null ) fragments[0] = ActivityFragment.newInstance();
                return fragments[0];
            case 1 :
                if ( fragments[1] == null ) fragments[1] = ChartFragment.newInstance();
                return fragments[1];
            case 2 :
                if ( fragments[2] == null ) fragments[2] = PatternFragment.newInstance();
                return fragments[2];
        }
        return null;
    }

    @Override
    public int getCount() {
        if ( fragments == null ) fragments = new BaseFragment[3];
        return 3;
    }
    public void refresh( int position ) {
        fragments[position].onFragmentSelected(position);
    }

}
