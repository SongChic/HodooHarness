package com.animal.harness.hodoo.hodooharness.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothBroadcast extends BroadcastReceiver {
    private final String TAG = BluetoothBroadcast.class.getSimpleName();
    private boolean mState = false;
    public interface BluetoothOnFound {
        void onFound (BluetoothDevice device);
        void onFoundState ( boolean state );
        void onConnectedState( boolean state, BluetoothDevice device );
    }
    private BluetoothOnFound mListener;
    public BluetoothBroadcast(BluetoothOnFound listener) {
        mListener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, action);
        if ( BluetoothDevice.ACTION_FOUND.equals(action) ) {
            if ( mListener != null ) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, String.format("bond state : %d", device.getBondState()));
                mListener.onFound(device);
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            mState = true;
        } else if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) ) {
            mState = false;
        } else if ( BluetoothDevice.ACTION_ACL_CONNECTED.equals(action) || BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action) ) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            mListener.onConnectedState( BluetoothDevice.ACTION_ACL_CONNECTED.equals(action) ? true : false, device );
        }
        if ( mListener != null )
            mListener.onFoundState(mState);
    }
}
