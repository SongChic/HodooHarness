package com.animal.harness.hodoo.hodooharness.fragment;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;

public class BluetoothFragment extends BaseFragment implements Runnable {
    /* view(s) */
    TextView bluetoothInfo;
    /* view(e) */

    /* variable(s) */
    private boolean findState = true;
    private Thread handler;
    /* variable(e) */

    public BluetoothFragment() {}

    @Override
    public void onFragmentSelected() {
        if ( handler == null )
            handler = new Thread(this);
        bluetoothInfo.setText("");
        bluetoothInfo.setText("블루투스를 연결 중 입니다.");
        start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_bluetooth, container, false);
        bluetoothInfo = wrap.findViewById(R.id.bluetooth_info);
        ImageButton bluetoothRefresh = wrap.findViewById(R.id.bluetooth_refresh);
        bluetoothRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !findState ) {
                    bluetoothInfo.setText("블루투스를 연결 중 입니다.");
                    findState = true;
                    start();
                }
                Log.e(TAG, "bluetoothRefresh");
            }
        });
        return wrap;
    }
    public void start() {
        if ( handler.isAlive() )
            handler.interrupt();
        bluetoothInfo.postDelayed(handler, 10000);
    }


    @Override
    public void run() {
        bluetoothInfo.setText("블루투스를 찾을 수 없습니다.\n다시 연결해 주세요.");
        findState = false;
    }
}
