package com.animal.harness.hodoo.hodooharness.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.animal.harness.hodoo.hodooharness.fragment.DaysFragment;

public class DaysAdapter extends FragmentStatePagerAdapter {
    private long mNow;
    public DaysAdapter(FragmentManager fm, long now) {
        super(fm);
        mNow = now;
    }

    public DaysAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
                DaysFragment fragment = new DaysFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("now", mNow );
                bundle.putInt("position", position );
                fragment.setArguments(bundle);
                return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
