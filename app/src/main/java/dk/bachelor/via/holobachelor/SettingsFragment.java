package dk.bachelor.via.holobachelor;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        int orientation = getResources().getConfiguration().orientation;
        Log.d("orientationSettings", Integer.toString(orientation));
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            view = inflater.inflate(R.layout.settings_fragment_land, container, false);
        } else {
            // In portrait
            view = inflater.inflate(R.layout.settings_fragment, container, false);
        }

        ImageButton minusButton = view.findViewById(R.id.minus);
        minusButton.setOnClickListener(this);
        ImageButton plusButton = view.findViewById(R.id.plus);
        plusButton.setOnClickListener(this);
        return view;
    }

    private void MapSize(byte data){
        ((MainActivity)getActivity()).passUserInput((byte) 5, new byte[]{data});
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
        default:
            break;
    }
    }
}
