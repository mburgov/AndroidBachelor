package Broadcaster;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import dk.bachelor.via.holobachelor.R;
import dk.bachelor.via.holobachelor.SettingsFragment;

import static java.lang.Thread.sleep;

public class Broadcaster {

    private static Broadcaster INSTANCE = null;
    static final int BEACON_ID = 1775;
    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    static AdvertiseData data;
    Handler mainHandler;


    private Broadcaster(BluetoothManager manager, BluetoothAdapter mBAdapter, BluetoothLeAdvertiser mBLEAdvertiser, Handler mainHandler){
        this.mBManager = manager;
        this.mBAdapter = mBAdapter;
        this.mBLEAdvertiser = mBLEAdvertiser;
        this.mainHandler = mainHandler;
    }

    public static Broadcaster getInstance(BluetoothManager manager, BluetoothAdapter mBAdapter, BluetoothLeAdvertiser mBLEAdvertiser, Handler mainHandler) {

        if(INSTANCE == null){
            INSTANCE = new Broadcaster(manager, mBAdapter, mBLEAdvertiser, mainHandler);
        }
        return INSTANCE;

    }

    private void startAdvertising() {
        if (mBLEAdvertiser == null) return;
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(false)
                .setTimeout(800)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
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
        mainHandler.sendMessage(Message.obtain(null, 0, new Message()));
    }

    private void restartAdvertising() {
        stopAdvertising();
        startAdvertising();
    }

    public static AdvertiseData getData() {
        return data;
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            String msg = "Service Running";
            Log.d("Stopped", msg);
            mainHandler.sendMessage(Message.obtain(null, 1, msg));
        }

        @Override
        public void onStartFailure(int errorCode) {
            if (errorCode != ADVERTISE_FAILED_ALREADY_STARTED) {
                String msg = "Service failed to start: " + errorCode;
                mainHandler.sendMessage(Message.obtain(null, 0, msg));
            } else {
                restartAdvertising();
            }
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
    // 5 - Scaling
    // 6 - Tap
    public void createPacketWithData(byte id, byte[] payload) {
        data = new AdvertiseData.Builder()
                .addManufacturerData(BEACON_ID, buildBLEPacket(id, payload))
                .build();
        startAdvertising();
    }
}
