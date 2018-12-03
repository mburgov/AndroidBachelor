package dk.bachelor.via.holobachelor;

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
import android.widget.Button;


public class NavigationFragment extends Fragment implements RotationGestureDetector.onRotationGestureListener, View.OnClickListener {

    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RotationGestureDetector mRotationDetector;
    private Button bN, bS, bE, bW;
    public float angle = 0.0f;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.navigation_fragment, container, false);
        // this is the view we will add the gesture detector to
        View myView = view.findViewById(R.id.gesture_view);
        setOnClickListeners();
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
                if (angle > 25) {
                    ((MainActivity) getActivity()).passUserInput((byte) 3, new byte[]{1});
                    Log.d("RotationGestureDetector", "Positive Rotation");
                } else if (angle < -25) {
                    ((MainActivity) getActivity()).passUserInput((byte) 3, new byte[]{2});
                    Log.d("RotationGestureDetector", "Negative Rotation");
                }
            }
            return mDetector.onTouchEvent(motionEvent);

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNorth:
                panMap((byte) 1);
                break;
            case R.id.buttonSouth:
                panMap((byte) 2);
                break;
            case R.id.buttonEast:
                panMap((byte) 3);
                break;
            case R.id.buttonWest:
                panMap((byte) 4);
                break;
            default:
                break;
        }
    }

    private void setOnClickListeners() {
        bN = view.findViewById(R.id.buttonNorth);
        bS = view.findViewById(R.id.buttonSouth);
        bW = view.findViewById(R.id.buttonWest);
        bE = view.findViewById(R.id.buttonEast);
        bN.setOnClickListener(this);
        bS.setOnClickListener(this);
        bW.setOnClickListener(this);
        bE.setOnClickListener(this);
    }

    public void panMap(Byte direction) {
        /* first argument is the movement type
        second is the direction of panning, going CSS style
        1 - North
        2 - South
        3 - East
        4 - West
         */
        byte[] data = new byte[]{direction};
        ((MainActivity) getActivity()).passUserInput((byte) 1, data);
    }

    @Override
    public void onRotation(RotationGestureDetector rotationDetector) {
        angle = rotationDetector.getAngle();
        Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG", "onDown: ");
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            ((MainActivity) getActivity()).passUserInput((byte) 6, new byte[]{1});
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
