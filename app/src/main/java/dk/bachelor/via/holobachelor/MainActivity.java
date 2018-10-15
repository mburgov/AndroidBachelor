package dk.bachelor.via.holobachelor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    static final int BEACON_ID = 1775;
    AdvertiseData data;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this is the view we will add the gesture detector to
        View myView = findViewById(R.id.gesture_view);

        // get the gesture detector
        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        myView.setOnTouchListener(touchListener);
        mBManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (mBManager != null) {
            mBAdapter = mBManager.getAdapter();
        }
        mBLEAdvertiser = mBAdapter.getBluetoothLeAdvertiser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBAdapter == null || !mBAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE support on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!mBAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "No advertising support on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAdvertising();
    }

    // BLE Code
    private void startAdvertising() {
        if (mBLEAdvertiser == null) return;
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(false)
                .setTimeout(800)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();
        mBLEAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    private void stopAdvertising() {
        if (mBLEAdvertiser == null) return;
        mBLEAdvertiser.stopAdvertising(mAdvertiseCallback);
        String msg = "Service Stopped";
        TextView tv1 = (TextView)findViewById(R.id.textView);
        tv1.setText(msg);
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
            TextView tv1 = (TextView)findViewById(R.id.textView);
            tv1.setText(msg);
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

    private byte[] buildGPSPacket(byte payload) {
        byte[] packet = new byte[1];
        packet[0] = payload;
        return packet;
    }

    private void createPacketWithData(byte payload) {
        data = new AdvertiseData.Builder()
                .addManufacturerData(BEACON_ID, buildGPSPacket(payload))
                .build();
        startAdvertising();
    }

    public void buttonPress(View view){
        createPacketWithData((byte) 1);
    }

    public void buttonPress2(View view){
        createPacketWithData((byte) 2);
    }

    // Gesture Control
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            return mDetector.onTouchEvent(event);

        }
    };

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            return true;
        }
    }

}