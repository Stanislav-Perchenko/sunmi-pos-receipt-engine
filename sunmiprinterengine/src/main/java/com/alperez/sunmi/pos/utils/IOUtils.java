package com.alperez.sunmi.pos.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public final class IOUtils {

    @SuppressWarnings("CatchMayIgnoreException")
    public static void cloaeSilently(Closeable toClose) {
        try {
            toClose.close();
        } catch (IOException e) { }
    }

    @Nullable
    public static BluetoothDevice getBtDeviceByAddress(BluetoothAdapter btAdapter, String address) {
        for (BluetoothDevice device : btAdapter.getBondedDevices()) {
            if (TextUtils.equals(device.getAddress(), address)) return device;
        }
        return null;
    }

    public static BluetoothSocket connectBtDeviceFrcomm(BluetoothDevice device, UUID serviceChannelId) throws IOException {
        BluetoothSocket socket;
        socket = device.createRfcommSocketToServiceRecord(serviceChannelId);
        socket.connect();
        return  socket;
    }


    private IOUtils() {}
}
