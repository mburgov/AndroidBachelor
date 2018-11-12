package dk.bachelor.via.holobachelor;

import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import Broadcaster.Broadcaster;

public class NavigationFragment extends Fragment implements RotationGestureDetector.OnRotationGestureListener  {

    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RotationGestureDetector mRotationDetector;
    private float mScaleFactor = 1.0f;
    private boolean screenIsTouched = false;
    private float angle = 0.0f;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private Broadcaster broadcaster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.navigation_fragment, container, false);
        // this is the view we will add the gesture detector to
        View myView = view.findViewById(R.id.gesture_view);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mRotationDetector = new RotationGestureDetector(this);
        broadcaster = MainActivity.broadcaster;
        // get the gesture detector
        mDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                Log.d("TAG","onDown: ");

                // don't return false here or else none of the other
                // gestures will work
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.i("TAG", "onLongPress: ");
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
        });

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        myView.setOnTouchListener(touchListener);

        return view;
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
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
            return mDetector.onTouchEvent(motionEvent);

        }
    };

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


    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        angle = rotationDetector.getAngle();
        Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
/*
        */
    }
}
