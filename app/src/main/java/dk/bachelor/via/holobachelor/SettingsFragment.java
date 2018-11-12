package dk.bachelor.via.holobachelor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Broadcaster.Broadcaster;

public class SettingsFragment extends Fragment {


    public static Broadcaster broadcaster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        broadcaster = MainActivity.broadcaster;

        return view;
    }
}
