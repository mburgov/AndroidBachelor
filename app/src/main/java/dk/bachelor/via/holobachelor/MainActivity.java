package dk.bachelor.via.holobachelor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import Broadcaster.Broadcaster;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {
    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    static final int BEACON_ID = 1775;
    AdvertiseData data;
    Broadcaster broadcaster;
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
        broadcaster = new Broadcaster(mBManager, mBAdapter, mBLEAdvertiser);

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
        broadcaster.stopAdvertising();
    }

    public void buttonPress(View view){
        broadcaster.createPacketWithData((byte) 1, (byte)1);
    }

    public void buttonPress2(View view){
        broadcaster.createPacketWithData((byte) 1, (byte) 2);
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
                broadcaster.createPacketWithData((byte) 3, (byte) 1);
                Log.d("RotationGestureDetector", "Positive Rotation");
            }
            else if(angle < -25) {
                broadcaster.createPacketWithData((byte) 3, (byte) 0);
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
                broadcaster.createPacketWithData((byte) 2, (byte) 1);
                Log.d("Scale", "Scale factor: " + Float.toString(mScaleFactor));
                Log.d("Scale", "Zoomed in");
            } else if (mScaleFactor < originalValue && Math.abs(angle) < 25){
                // 0 for zoom out
                broadcaster.createPacketWithData((byte) 2, (byte) 0);
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