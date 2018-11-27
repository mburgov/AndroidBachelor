package dk.bachelor.via.holobachelor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Activity activity;
    private CheckBox checkBox;
    private View view;
    private Editor editor;
    private Button minusButton;
    private Button plusButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("settingsFragment","called");
        int orientation = getResources().getConfiguration().orientation;
        Log.d("orientationSettings", Integer.toString(orientation));
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            view = inflater.inflate(R.layout.settings_fragment_land, container, false);
        } else {
            // In portrait
            view = inflater.inflate(R.layout.settings_fragment, container, false);
        }
        activity = getActivity();
        Log.d("activityMain",activity.toString());
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        checkBox= view.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(this);
        minusButton = view.findViewById(R.id.minus);
        minusButton.setOnClickListener(this);
        plusButton = view.findViewById(R.id.plus);
        plusButton.setOnClickListener(this);
        return view;
    }

    private void MapSize(byte data){
        Log.d("MapSize", "Rescaled");
        ((MainActivity)getActivity()).passUserInput((byte) 5, new byte[]{data});
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("MyPref", 0);
        Log.d("preferences checkbox",String.valueOf(pref.getBoolean("checkbox", false)));
        if (pref.getBoolean("checkbox", false) == true){ //false is default value
            checkBox.setChecked(true); //it was checked
        } else{
            checkBox.setChecked(false); //it was NOT checked
        }
        checkBoxClicked();
    }
    private void checkBoxClicked(){
        Log.d("checkbox state",String.valueOf(checkBox.isChecked()));
        if(checkBox.isChecked()) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
    @Override
    public void onClick(View view) {
    switch(view.getId()){
        case R.id.minus:
            MapSize((byte) 1);
            break;
        case R.id.plus:
            MapSize((byte) 2);
            break;
        case R.id.checkBox:
            if(checkBox.isChecked()) {
                Log.d("checkbox checked","checked");
                editor.putBoolean("checkbox", true);
                editor.commit(); // commit changes
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                editor.putBoolean("checkbox", false);
                editor.commit(); // commit changes
            }
        default:
            break;
    }
    }
}
