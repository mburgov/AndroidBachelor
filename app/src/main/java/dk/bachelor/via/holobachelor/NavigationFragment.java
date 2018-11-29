package dk.bachelor.via.holobachelor;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;


public class NavigationFragment extends Fragment implements RotationGestureDetector.onRotationGestureListener  {

    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RotationGestureDetector mRotationDetector;
    public float mScaleFactor = 1.0f;
    public float angle = 0.0f;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.navigation_fragment, container, false);
        // this is the view we will add the gesture detector to
        View myView = view.findViewById(R.id.gesture_view);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener(this));
        mRotationDetector = new RotationGestureDetector(this);
        // get the gesture detector
        mDetector = new GestureDetector(getActivity(), new MyGestureListener());

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
            } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                Log.d("TouchTest", "Touch up");
                // payload: 1 for positive, 2 for negative rotation
                // positive rotation is counter clockwise
                if(angle > 25) {
                    ((MainActivity)getActivity()).passUserInput((byte) 3, new byte[]{1});
                    Log.d("RotationGestureDetector", "Positive Rotation");
                }
                else if(angle < -25) {
                    ((MainActivity)getActivity()).passUserInput((byte) 3, new byte[]{2});
                    Log.d("RotationGestureDetector", "Negative Rotation");
                }
            }
            return mDetector.onTouchEvent(motionEvent);

        }
    };




    @Override
    public void onRotation(RotationGestureDetector rotationDetector) {
        angle = rotationDetector.getAngle();
        Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
    }


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
            ((MainActivity)getActivity()).passUserInput((byte) 6, new byte[]{1});
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
