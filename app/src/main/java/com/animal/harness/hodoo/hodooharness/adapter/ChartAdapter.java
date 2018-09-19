package com.animal.harness.hodoo.hodooharness.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.animal.harness.hodoo.hodooharness.base.BaseFragment;

public class ChartAdapter extends FragmentStatePagerAdapter {
    private BaseFragment[] mLayout;
    public ChartAdapter(FragmentManager fm, BaseFragment[] layout) {
        super(fm);
        mLayout = layout;
    }

    @Override
    public Fragment getItem(int position) {
//        refresh(position);

        switch ( position ) {
            case 0 :
                mLayout[position].onFragmentSelected();
                return mLayout[position];
        }

        return mLayout[position];
    }

    @Override
    public int getCount() {
        return mLayout.length;
    }

    public void refresh( int position ) {
        mLayout[position].onFragmentSelected( position );
    }
}
