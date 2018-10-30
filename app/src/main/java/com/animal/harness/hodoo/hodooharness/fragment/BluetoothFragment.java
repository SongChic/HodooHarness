package com.animal.harness.hodoo.hodooharness.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.adapter.BluetoothListAdapter;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.broadcast.BluetoothBroadcast;
import com.animal.harness.hodoo.hodooharness.constant.HodooConstant;
import com.animal.harness.hodoo.hodooharness.databinding.FragmentBluetoothBinding;
import com.animal.harness.hodoo.hodooharness.domain.BluetoothItem;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;
import com.github.ybq.android.spinkit.SpinKitView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class BluetoothFragment extends BaseFragment implements Runnable {

    private FragmentBluetoothBinding binding;


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
    private String mConnectBtName;

    private BluetoothManager mBluetoothManager;
    BluetoothBroadcast broadcast;

    private BluetoothListAdapter btListAdapter;
    private List<String> btListtitle;
    private List<List<BluetoothItem>> btItems;
    private List<BluetoothItem> items;
    private List<BluetoothItem> pairList;
    /* variable(e) */

    private BluetoothDevice mConnectDevice;

    public BluetoothFragment() {}

    @Override
    public void onFragmentSelected() {
        Log.e(TAG, String.format("null check : %b", mItem != null));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bluetooth, container, false);
        binding.setFragment(this);

        RelativeLayout wrap = (RelativeLayout) binding.getRoot();
        bluetoothInfo = wrap.findViewById(R.id.bluetooth_info);
        mSpinKit = wrap.findViewById(R.id.spin_kit);

        adapter = BluetoothAdapter.getDefaultAdapter();
        if ( adapter == null ) {
            bluetoothInfo.setText("해당 기기에서는 블루투스를\n사용 할 수 없습니다.");
//            HodooUtil.changeDrawableColor(mItem.getIcon(), ContextCompat.getColor(getContext(), R.color.hodoo_menu_default));
            Toast.makeText(getContext(), "블루투스를 사용 할 수 없는 기기입니다.", Toast.LENGTH_SHORT).show();
        } else {
            if ( !adapter.isEnabled() ) {
                /* 블루투스 OFF */
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, HodooConstant.BLUETOOTH_REQUEST_CODE);
            } else {
                init();
                checkConnected();
                setBtAdapter();
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.e(TAG, "onCreateOptionsMenu");
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

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(broadcast);
    }
    public void onClick ( View v ) {
        switch ( v.getId() ) {
            case R.id.bluetooth_refresh :
                pairList.clear();
                items.clear();
                getPairList();
                btListAdapter.notifyDataSetChanged();

                if ( !adapter.isDiscovering() )
                    adapter.startDiscovery();
                break;
        }
    }
    public void getPairList () {
        Set pairDevices = adapter.getBondedDevices();
        //페어링된 장치가 있으면
        if(pairDevices.size()>0){
            if ( pairList.size() > 0 )
                pairList.clear();
            for(BluetoothDevice device : adapter.getBondedDevices()) {
                //페어링된 장치 이름과, MAC주소를 가져올 수 있다.
                BluetoothItem item = BluetoothItem.builder().name( device.getName() != null ? device.getName() : device.getAddress() ).address(device.getAddress()).device(device).build();
                pairList.add(item);
            }
        }
    }
    private void setBtAdapter() {
        btListAdapter = new BluetoothListAdapter(getContext(), new int[]{
                R.layout.bluetooth_list_parent,
                R.layout.bluetooth_list_item
        }, btListtitle, btItems);
        binding.bluetoothList.setAdapter(btListAdapter);
        binding.bluetoothList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, final int childPosition, long id) {
                if ( groupPosition == 0 ) {
                    final BluetoothDevice device = pairList.get(childPosition).getDevice();
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle(device.getName() != null ? device.getName() : device.getAddress() )
                            .setMessage("해당 디바이스를 해제하시겠습니까?")
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pairList.get(childPosition).setStateStr("해제중입니다.");
                                    btListAdapter.notifyDataSetChanged();
                                    removePairing(device.getAddress());
                                    PairingThread pairing = new PairingThread(device, false);
                                    pairing.start();
                                }
                            })
                            .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();

                }
                else if ( groupPosition == 1 ) {
                    boolean bondState = false;
                    items.get(childPosition).setStateStr("연결중입니다.");
                    btListAdapter.notifyDataSetChanged();
                    try {
                        BluetoothDevice device = items.get(childPosition).getDevice();
                        bondState = createBond(device);
                        if ( bondState ) {
                            PairingThread pairing = new PairingThread(device, true);
                            pairing.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                return false;
            }
        });
        broadcast = new BluetoothBroadcast(new BluetoothBroadcast.BluetoothOnFound() {
            @Override
            public void onFound(BluetoothDevice device) {
                if ( btItems.get(1).size() > 0 ) {
                    for (int i = 0; i < btItems.get(1).size(); i++)
                        if (btItems.get(1).get(i).getName().equals(device.getName() != null ? device.getName() : device.getAddress()))
                            return;
                }
                BluetoothItem item = BluetoothItem.builder().name( device.getName() != null ? device.getName() : device.getAddress() ).address(device.getAddress()).device(device).build();
                btItems.get(1).add(item);
                btListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFoundState(boolean state) {
                if ( state ) {
                    bluetoothInfo.setText("블루투스를 검색 중 입니다.");
                    binding.spinKit.setVisibility(View.VISIBLE);
                } else {
                    bluetoothInfo.setText("블루투스 검색 완료");
                    binding.spinKit.setVisibility(View.INVISIBLE);
                }

            }
            @Override
            public void onConnectedState(boolean state, BluetoothDevice device) {
                for ( int i = 0; i < pairList.size(); i++ ) {
                    if ( pairList.get(i).getAddress().equals( device.getAddress() ) ) {
                        pairList.get(i).setState(state);
                    }
                }
                btListAdapter.notifyDataSetChanged();
                Log.e(TAG, String.format("state : %b, device address" + device.getAddress(), state));
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        getActivity().registerReceiver(broadcast, filter);
        if ( !adapter.isDiscovering() )
            adapter.startDiscovery();


        for ( int i = 0; i < btListAdapter.getGroupCount(); i++ )
            binding.bluetoothList.expandGroup(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, String.format("requestCode : %d", requestCode));
        if ( requestCode == HodooConstant.BLUETOOTH_REQUEST_CODE ) {
            if ( resultCode == RESULT_OK ) {
                init();
                checkConnected();
                setBtAdapter();
            }
        }

    }
    public void checkConnected() {
        // true == headset connected && connected headset is support hands free
        int state = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET);
        if (state != BluetoothProfile.STATE_CONNECTED)
            return;

        try {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(getContext(), serviceListener, BluetoothProfile.HEADSET);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener()
    {
        @Override
        public void onServiceDisconnected(int profile)
        {

        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            for (BluetoothDevice device : proxy.getConnectedDevices()) {
                mConnectBtName = device.getName();
                mConnectDevice = device;
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }
    };
    private void init ( ) {
        btListtitle = new ArrayList<>();
        btListtitle.add("페어링된 디바이스");
        btListtitle.add("연결 가능한 디바이스");

        items = new ArrayList<>();
        btItems = new ArrayList<>();
        pairList = new ArrayList<>();

        btItems.add(pairList);
        btItems.add(items);
        getPairList();
    }
    public synchronized boolean createBond(BluetoothDevice btDevice) throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
//        requestConnect(btDevice, UUID.nameUUIDFromBytes(btDevice.getAddress().getBytes()));
        return returnValue.booleanValue();
    }
    private class PairingThread extends Thread {
        BluetoothDevice mDevice;
        int count = 0;
        boolean mState;
        PairingThread ( BluetoothDevice device, boolean state ) {
            mDevice = device;
            count = 0;
            mState = state;
        }
        @Override
        public void run() {
            while ( count < 20 ) {
                Log.e(TAG, String.format("bound : %d", mDevice.getBondState()));
                if ( mState && mDevice.getBondState() != BluetoothDevice.BOND_BONDING ) {
                    if ( mDevice.getBondState() == BluetoothDevice.BOND_BONDED ) {
                        BluetoothFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getPairList();
                                for ( int i = 0; i < items.size(); i++ ) {
                                    if ( items.get(i).getAddress().equals(mDevice.getAddress()) ) {
                                        items.get(i).setStateStr("");
                                        items.remove(i);
                                        btListAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        });
                        break;
                    }
                } else if ( !mState && mDevice.getBondState() != BluetoothDevice.BOND_BONDED ) {
                    BluetoothFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            getPairList();
                            for ( int i = 0; i < pairList.size(); i++ ) {
                                if ( pairList.get(i).getAddress().equals(mDevice.getAddress()) ) {
                                    pairList.get(i).setStateStr("");
                                    items.add(pairList.get(i));
                                    pairList.remove(i);
                                    btListAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    });
                    break;
                }
//                else if ( !mState && mDevice.getBondState() == BluetoothDevice.BOND_NONE ) {

//                }
                try {
                    Thread.sleep(1000);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            BluetoothFragment.this.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    List<BluetoothItem> target = mState ? pairList : items;
//                    for ( int i = 0; i < target.size(); i++ ) {
//                        if ( target.get(i).getAddress().equals(mDevice.getAddress()) ) {
//                            target.get(i).setStateStr("");
//                            btListAdapter.notifyDataSetChanged();
//                            break;
//                        }
//                    }
//                }
//            });
            super.run();
        }
    }
    public void removePairing( String currentMac ) {
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        try {
            Class<?> btDeviceInstance =  Class.forName(BluetoothDevice.class.getCanonicalName());
            Method removeBondMethod = btDeviceInstance.getMethod("removeBond");
            boolean cleared = false;
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                String mac = bluetoothDevice.getAddress();
                if(mac.equals(currentMac)) {
                    removeBondMethod.invoke(bluetoothDevice);
                    Log.i(TAG,"Cleared Pairing");
                    cleared = true;
                    break;
                }
            }
            if(!cleared) {
                Log.i(TAG,"Not Paired");
            }
        } catch (Throwable th) {
            Log.e(TAG, "Error pairing", th);
        }
    }
}
