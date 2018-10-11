package com.animal.harness.hodoo.hodooharness;

import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.animal.harness.hodoo.hodooharness.adapter.TabsPagerAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseActivity;
import com.animal.harness.hodoo.hodooharness.fragment.BluetoothFragment;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;
import com.animal.harness.hodoo.hodooharness.util.TabLayoutUtils;

public class MainActivity extends BaseActivity<MainActivity> {
    TypedArray mBtnArr, mBtnActiveArr;
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
    String titles[] = {
            "활동량 측정",
            "통계관리",
            "활동패턴"
    };
    private String oldTitle = "";
    TabsPagerAdapter adapter;
    LinearLayout fragmentWrap;
    FrameLayout overlay;
    TabLayout mTabLayout;

    private BluetoothFragment bluetoothFragment;
    ViewPager viewPager;
    boolean settingFlag = false;

    private Handler anim;
    private boolean animState = false;
    private int mSelectPosition = 0;

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

        mBtnArr = this.getResources().obtainTypedArray(R.array.main_btn_default);
        mBtnActiveArr = this.getResources().obtainTypedArray(R.array.main_btn_default);

        setTitleBar("활동량 측정");
        viewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);
        fragmentWrap = findViewById(R.id.fragment_wrap);

        for ( int i = 0; i < mBtn.length; i++ ) {
            if ( i == 0 )
                mTabLayout.addTab(mTabLayout.newTab().setIcon(mBtnActive[i]));
            else
                mTabLayout.addTab(mTabLayout.newTab().setIcon(mBtn[i]));
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mSelectPosition = tab.getPosition();
                tab.setIcon(mBtnActive[tab.getPosition()]);
                viewPager.setCurrentItem(tab.getPosition());
                setTitleBar(titles[tab.getPosition()]);
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
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        viewPager.setCurrentItem(0);
        bluetoothFragment = new BluetoothFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.setting_fragment, bluetoothFragment);
        ft.commit();
    }

    @Override
    public void setTitleBar(String titleStr) {
        super.setTitleBar(titleStr);
        Log.e(TAG, "titleStr : " + titleStr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            /* menu button click (s) */
            case R.id.menu_bluetooth:
                int color = ContextCompat.getColor(this, R.color.hodoo_menu_default);
                Drawable d = item.getIcon();
                String title = "";
                if ( !settingFlag ) {
                    viewPager.setVisibility(View.GONE);
                    fragmentWrap.setVisibility(View.VISIBLE);
                    bluetoothFragment.setMenuIcon(item);
                    bluetoothFragment.onFragmentSelected();
                    color = ContextCompat.getColor(this, R.color.hodoo_menu_active);
                    settingFlag = true;
                    oldTitle = this.getNowTitle();
                    title = "설정";
                    TabLayoutUtils.enableTabs( mTabLayout, false );
                    mTabLayout.getTabAt(mSelectPosition).setIcon(mBtn[mSelectPosition]);
                } else {
                    viewPager.setVisibility(View.VISIBLE);
                    fragmentWrap.setVisibility(View.GONE);
                    settingFlag = false;
                    title = oldTitle;
                    TabLayoutUtils.enableTabs( mTabLayout, true );
                    mTabLayout.getTabAt(mSelectPosition).setIcon(mBtnActive[mSelectPosition]);
                }
//                HodooUtil.changeDrawableColor( d, color);
                this.setTitleBar(title);
                break;
            /* menu button click (e) */

            /* menu setting click (s) */
            case R.id.menu_settings :
                break;
            /* menu setting click (e) */
        }
        return super.onOptionsItemSelected(item);
    }
}
