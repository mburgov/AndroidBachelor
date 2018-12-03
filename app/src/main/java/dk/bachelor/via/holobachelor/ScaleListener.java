package dk.bachelor.via.holobachelor;

import android.util.Log;
import android.view.ScaleGestureDetector;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    private float originalValue = 128;
    public float mScaleFactor;
    private NavigationFragment navigationFragment;

    public ScaleListener(NavigationFragment navigationFragment){
        this.navigationFragment=navigationFragment;
    }

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

        if(mScaleFactor > originalValue && Math.abs(navigationFragment.angle) < 25) {
            // 1 for zoom in
            ((MainActivity)navigationFragment.getActivity()).passUserInput((byte) 2, new byte[]{1});
            Log.d("Scale", "Scale factor: " + Float.toString(mScaleFactor));
            Log.d("Scale", "Zoomed in");
        } else if (mScaleFactor < originalValue && Math.abs(navigationFragment.angle) < 25){
            // 0 for zoom out
            ((MainActivity)navigationFragment.getActivity()).passUserInput((byte) 2, new byte[]{2});
            Log.d("Scale", "Scale factor: " + Float.toString(mScaleFactor));
            Log.d("Scale", "Zoomed out");
        }
        Log.d("Scale", "It ended");
    }
}
