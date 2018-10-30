package com.animal.harness.hodoo.hodooharness.domain;

import android.bluetooth.BluetoothDevice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BluetoothItem {
    private String name;
    private String address;
    @Builder.Default
    private boolean state = false;
    private String stateStr;
    private BluetoothDevice device;
}
