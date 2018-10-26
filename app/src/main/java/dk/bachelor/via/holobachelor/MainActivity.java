package dk.bachelor.via.holobachelor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {
    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    static final int BEACON_ID = 1775;
    AdvertiseData data;
    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RotationGestureDetector mRotationDetector;
    private float mScaleFactor = 1.0f;
    private boolean screenIsTouched = false;
    private float angle = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this is the view we will add the gesture detector to
        View myView = findViewById(R.id.gesture_view);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        mRotationDetector = new RotationGestureDetector(this);

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
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private byte[] buildGPSPacket(byte id, byte payload) {
        byte[] packet = new byte[2];
        packet[0] = id;
        packet[1] = payload;
        return packet;
    }

    // IDs:
    // 1 - Panning
    // 2 - Zooming
    // 3 - Rotating
    private void createPacketWithData(byte id, byte payload) {
        data = new AdvertiseData.Builder()
                .addManufacturerData(BEACON_ID, buildGPSPacket(id, payload))
                .build();
        startAdvertising();
    }

    public void buttonPress(View view){
        createPacketWithData((byte) 1, (byte)1);
    }

    public void buttonPress2(View view){
        createPacketWithData((byte) 1, (byte) 2);
    }

    // Gesture Control
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        mRotationDetector.onTouchEvent(motionEvent);
        // pass the events to the gesture detector
        // a return value of true means the detector is handling it
        // a return value of false means the detector didn't
        // recognize the event
        if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            Log.d("TouchTest", "Touch down");
            screenIsTouched = true;
        } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
            Log.d("TouchTest", "Touch up");
            // Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
            // payload: 1 for positive, 0 for negative rotation
            // positive rotation is counter clockwise
            if(angle > 25) {
                createPacketWithData((byte) 3, (byte) 1);
                Log.d("RotationGestureDetector", "Positive Rotation");
            }
            else if(angle < -25) {
                createPacketWithData((byte) 3, (byte) 0);
                Log.d("RotationGestureDetector", "Negative Rotation");
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float originalValue = 128;

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f,
                    Math.min(mScaleFactor, 256.0f));
            Log.d("TAG", "Scale factor: " + Float.toString(mScaleFactor));
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector){
            Log.d("Scale", "It started");
            mScaleFactor = originalValue;
            return true;
        }

        @Override
        public void onScaleEnd (ScaleGestureDetector detector){

            if(mScaleFactor > originalValue && Math.abs(angle) < 25) {
                // 1 for zoom in
                createPacketWithData((byte) 2, (byte) 1);
                Log.d("Scale", "Scale factor: " + Float.toString(mScaleFactor));
                Log.d("Scale", "Zoomed in");
            } else if (mScaleFactor < originalValue && Math.abs(angle) < 25){
                // 0 for zoom out
                createPacketWithData((byte) 2, (byte) 0);
                Log.d("Scale", "Scale factor: " + Float.toString(mScaleFactor));
                Log.d("Scale", "Zoomed out");
            }
            Log.d("Scale", "It ended");
        }
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mDetector.onTouchEvent(event);

        }
    };

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        angle = rotationDetector.getAngle();
         Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
        // payload: 1 for positive, 0 for negative rotation
        // positive rotation is counter clockwise
        /*
        if(angle > 0 && !screenIsTouched) {
            createPacketWithData((byte) 3, (byte) 1);
            Log.d("RotationGestureDetector", "Positive Rotation");
        }
        else if(angle < 0 && !screenIsTouched) {
            createPacketWithData((byte) 3, (byte) 0);
            Log.d("RotationGestureDetector", "Negative Rotation");
        }*/


    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
/*
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
        }*/
    }
}