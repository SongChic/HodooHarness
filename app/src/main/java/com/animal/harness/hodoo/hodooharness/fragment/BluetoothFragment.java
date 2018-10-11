package com.animal.harness.hodoo.hodooharness.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;
import com.github.ybq.android.spinkit.SpinKitView;

public class BluetoothFragment extends BaseFragment implements Runnable {
    /* view(s) */
    private TextView bluetoothInfo;
    private SpinKitView mSpinKit;
    /* view(e) */

    /* variable(s) */
    private boolean findState = true;
    private Thread handler;
    BluetoothAdapter adapter;
    private MenuItem mItem;
    int stateCount = 0;
    private boolean threadState = true;
    /* variable(e) */

    public BluetoothFragment() {}

    @Override
    public void onFragmentSelected() {
        Log.e(TAG, String.format("null check : %b", mItem != null));

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if ( adapter == null ) {
            bluetoothInfo.setText("해당 기기에서는 블루투스를\n사용 할 수 없습니다.");

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    HodooUtil.changeDrawableColor(mItem.getIcon(), ContextCompat.getColor(getContext(), R.color.hodoo_menu_default));
                }
            });
        } else {
            if ( adapter.isEnabled() ) {
                /* 블루투스 OFF */
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 2000);
            } else {
                bluetoothInfo.setText("");
                bluetoothInfo.setText("블루투스를 연결 중 입니다.");
                start();
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_bluetooth, container, false);
        bluetoothInfo = wrap.findViewById(R.id.bluetooth_info);
        mSpinKit = wrap.findViewById(R.id.spin_kit);
        ImageButton bluetoothRefresh = wrap.findViewById(R.id.bluetooth_refresh);
        bluetoothRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !findState ) {
                    bluetoothInfo.setText("블루투스를 연결 중 입니다.");
                    findState = threadState = true;
                    stateCount = 0;
                    start();
                }
                Log.e(TAG, "bluetoothRefresh");
            }
        });
        return wrap;
    }
    public void start() {
        if ( handler == null )
            handler = new Thread(this);

        if ( threadState )
            handler.interrupt();
        handler.start();
//        bluetoothInfo.postDelayed(handler, 10000);
    }
    public void setMenuIcon(MenuItem item) {
        mItem = item;
    }


    @Override
    public void run() {
//        bluetoothInfo.setText("블루투스를 찾을 수 없습니다.\n다시 연결해 주세요.");
//        findState = false;

        /* pre code (e) */

        /* new code (s) */
        while ( threadState ) {
            if ( stateCount % 2 == 0 )
                HodooUtil.changeDrawableColor(mItem.getIcon(), ContextCompat.getColor(getContext(), R.color.hodoo_menu_active));
            else
                HodooUtil.changeDrawableColor(mItem.getIcon(), ContextCompat.getColor(getContext(), R.color.hodoo_menu_default));
            if ( (stateCount / 2) >= 10 )
                threadState = false;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stateCount++;
        }
        HodooUtil.changeDrawableColor(mItem.getIcon(), ContextCompat.getColor(getContext(), R.color.hodoo_menu_default));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bluetoothInfo.setText("블루투스를 찾을 수 없습니다.\n다시 연결해 주세요.");
                mSpinKit.setVisibility(View.INVISIBLE);
                findState = false;
                handler = null;
            }
        });
        /* new code (e) */
    }
    public void stop() {

    }
}
