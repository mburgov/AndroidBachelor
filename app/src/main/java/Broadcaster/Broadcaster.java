package Broadcaster;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;

import static java.lang.Thread.sleep;

public class Broadcaster {

    private static Broadcaster INSTANCE = null;
    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    static final int BEACON_ID = 1775;
    AdvertiseData data;

    private Broadcaster(BluetoothManager manager, BluetoothAdapter mBAdapter, BluetoothLeAdvertiser mBLEAdvertiser){
        this.mBManager = manager;
        this.mBAdapter = mBAdapter;
        this.mBLEAdvertiser = mBLEAdvertiser;
    }

    public static Broadcaster getInstance(BluetoothManager manager, BluetoothAdapter mBAdapter, BluetoothLeAdvertiser mBLEAdvertiser) {

        if(INSTANCE == null){
            INSTANCE = new Broadcaster(manager, mBAdapter, mBLEAdvertiser);
        }
        return INSTANCE;

    }

    private void startAdvertising() {
        if (mBLEAdvertiser == null) return;
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(false)
                .setTimeout(800)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();
        mBLEAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopAdvertising() {
        if (mBLEAdvertiser == null) return;
        mBLEAdvertiser.stopAdvertising(mAdvertiseCallback);
        String msg = "Service Stopped";
    }

    private void restartAdvertising() {
        stopAdvertising();
        startAdvertising();
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            String msg = "Service Running";
            mHandler.sendMessage(Message.obtain(null, 0, msg));
        }

        @Override
        public void onStartFailure(int errorCode) {
            if (errorCode != ADVERTISE_FAILED_ALREADY_STARTED) {
                String msg = "Service failed to start: " + errorCode;
                mHandler.sendMessage(Message.obtain(null, 0, msg));
            } else {
                restartAdvertising();
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
/*
UI feedback to the user would go here.
*/
        }
    };

    private byte[] buildBLEPacket(byte id, byte[] payload) {
        byte[] packet = new byte[payload.length + 1];
        packet[0] = id;
        System.arraycopy(payload, 0, packet, 1, payload.length);
        Log.d("Packet to be sent", Arrays.toString(packet));
        return packet;
    }

    // IDs:
    // 1 - Panning
    // 2 - Zooming
    // 3 - Rotating
    // 4 - Text input
    public void createPacketWithData(byte id, byte[] payload) {
        data = new AdvertiseData.Builder()
                .addManufacturerData(BEACON_ID, buildBLEPacket(id, payload))
                .build();
        startAdvertising();
    }
}
