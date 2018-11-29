package dk.bachelor.via.holobachelor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class InputFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.input_fragment, container, false);
        Button button = view.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendTextInput();
            }
        });
        return view;
    }

    private void sendTextInput(){
        EditText input = getActivity().findViewById(R.id.editText);
        ((MainActivity)getActivity()).passUserInput((byte) 4, input.getText().toString().getBytes());
        input.setText("");
    }
}
