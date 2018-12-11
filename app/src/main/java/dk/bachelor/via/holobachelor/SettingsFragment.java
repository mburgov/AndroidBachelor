package dk.bachelor.via.holobachelor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Activity activity;
    private CheckBox checkBox;
    private Editor editor;
    private Button minusButton;
    private Button plusButton;
    private TextView status;
    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        activity = getActivity();
        pref = activity.getApplicationContext().getSharedPreferences("CheckBoxPref", 0); // 0 - for private mode
        editor = pref.edit();
        checkBox = view.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(this);
        minusButton = view.findViewById(R.id.minus);
        minusButton.setOnClickListener(this);
        plusButton = view.findViewById(R.id.plus);
        plusButton.setOnClickListener(this);
        status = view.findViewById(R.id.textView5);
        status.setText(((MainActivity)getActivity()).getStatus());
        Log.d("UIStatusCreate",((MainActivity)getActivity()).getStatus());
        if(status.getText().toString().contains("Not")){
            status.setTextColor(Color.parseColor("#FF0000"));
        } else {
            status.setTextColor(Color.parseColor("#00FF00"));
        }
        return view;
    }

    private void changeMapSize(byte data) {
        Log.d("MapSize", "Rescaled");
        ((MainActivity) getActivity()).passUserInput((byte) 5, new byte[]{data});
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pref.getBoolean("checkbox", false) == true) { //false is default value
            checkBox.setChecked(true); //it was checked
        } else {
            checkBox.setChecked(false); //it was NOT checked
        }
        status = ((MainActivity)getActivity()).findViewById(R.id.textView5);
        status.setText(((MainActivity)getActivity()).getStatus());
        Log.d("UIStatusResume",((MainActivity)getActivity()).getStatus());
        if(status.getText().toString().contains("Not")){
            status.setTextColor(Color.parseColor("#FF0000"));
        } else {
            status.setTextColor(Color.parseColor("#00FF00"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.minus:
                changeMapSize((byte) 1);
                break;
            case R.id.plus:
                changeMapSize((byte) 2);
                break;
            case R.id.checkBox:
                if (checkBox.isChecked()) {
                    editor.putBoolean("checkbox", true);
                    editor.commit(); // commit changes
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    editor.putBoolean("checkbox", false);
                    editor.commit(); // commit changes
                }
            default:
                break;
        }
    }
}
