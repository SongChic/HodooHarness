package com.animal.harness.hodoo.hodooharness;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.animal.harness.hodoo.hodooharness.adapter.TabsPagerAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseActivity;
import com.animal.harness.hodoo.hodooharness.fragment.MainFragment;

public class MainActivity extends BaseActivity<MainActivity> {
    int[] mBtn = {
            R.drawable.tab_01,
            R.drawable.tab_02,
            R.drawable.tab_03
    };
    int[] mBtnActive = {
            R.drawable.tab_01_active,
            R.drawable.tab_02_active,
            R.drawable.tab_03
    };
    String testTitle[] = {
            "활동량 측정",
            "통계관리",
            "설정"
    };
    TabsPagerAdapter adapter;
    LinearLayout fragmentWrap;
    ViewPager viewPager;
    boolean settingFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected BaseActivity<MainActivity> getActivityClass() {
        return this;
    }

    public void init() {
        setTitleBar("활동량 측정");
        viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        fragmentWrap = findViewById(R.id.fragment_wrap);

        for ( int i = 0; i < mBtn.length; i++ ) {
            if ( i == 0 )
                tabLayout.addTab(tabLayout.newTab().setIcon(mBtnActive[i]));
            else
                tabLayout.addTab(tabLayout.newTab().setIcon(mBtn[i]));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(mBtnActive[tab.getPosition()]);
                viewPager.setCurrentItem(tab.getPosition());
                setTitleBar(testTitle[tab.getPosition()]);
                adapter.refresh(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(mBtn[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.e(TAG, String.format("onTabReselected position : %d", tab.getPosition()));
            }
        });

        adapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);
    }

    @Override
    public void setTitleBar(String titleStr) {
        super.setTitleBar(titleStr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.menu_settings :
                if ( !settingFlag ) {
                    viewPager.setVisibility(View.GONE);
                    fragmentWrap.setVisibility(View.VISIBLE);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.setting_fragment, new MainFragment());
                    ft.commit();
                    item.setIcon(R.drawable.menu_settings_active);
                    settingFlag = true;
                } else {
                    viewPager.setVisibility(View.VISIBLE);
                    fragmentWrap.setVisibility(View.GONE);
                    item.setIcon(R.drawable.menu_settings);
                    settingFlag = false;
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
