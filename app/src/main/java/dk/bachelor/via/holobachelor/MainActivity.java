package dk.bachelor.via.holobachelor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;


import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import Broadcaster.Broadcaster;


public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    AdvertiseData data;
    public static Broadcaster broadcaster;
    public static Handler mainThreadHandler;
    private Editor editor;
    String status;
    SharedPreferences pref;

    // executed only on startup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("Pref", 0); // 0 - for private mode
        editor = pref.edit();
        executeLogic();
    }

    @Override
    protected void onStop() {
        editor.putString("selectedFragment", "Navigation");
        editor.commit();
        super.onStop();
    }

    // executed on orientation change
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        executeLogic();
    }

    public void executeLogic() {
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_navigation:
                        selectedFragment = new NavigationFragment();
                        editor.putString("selectedFragment", "Navigation");
                        editor.commit();
                        break;

                    case R.id.navigation_settings:
                        selectedFragment = new SettingsFragment();
                        editor.putString("selectedFragment", "Settings");
                        editor.commit();
                        break;

                    case R.id.navigation_input:
                        selectedFragment = new InputFragment();
                        editor.putString("selectedFragment", "Input");
                        editor.commit();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, selectedFragment).commit();

                return true;
            }
        });
        if (pref.getString("selectedFragment", "defaultValue").equals("Navigation") || pref.getString("selectedFragment", "defaultValue").equals("defaultValue")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new NavigationFragment()).commit();

            navigation.getMenu().findItem(R.id.navigation_navigation).setChecked(true);

            Menu menu = navigation.getMenu();
            MenuItem menuItem = menu.getItem(1);
            menuItem.setChecked(true);
        } else if (pref.getString("selectedFragment", "defaultValue").equals("Settings")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SettingsFragment()).commit();

            navigation.getMenu().findItem(R.id.navigation_settings).setChecked(true);

            Menu menu = navigation.getMenu();
            MenuItem menuItem = menu.getItem(2);
            menuItem.setChecked(true);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new InputFragment()).commit();

            navigation.getMenu().findItem(R.id.navigation_input).setChecked(true);

            Menu menu = navigation.getMenu();
            MenuItem menuItem = menu.getItem(0);
            menuItem.setChecked(true);
        }
        mainThreadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    status = "Broadcasting";
                    Log.d("BLEStatus", status);
                } else if (msg.what == 0) {
                    status = "Not Broadcasting";
                    Log.d("BLEStatus", status);
                }
            }

        };

        mBManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (mBManager != null) {
            mBAdapter = mBManager.getAdapter();
            if (mBAdapter != null) {
                mBLEAdvertiser = mBAdapter.getBluetoothLeAdvertiser();
                broadcaster = Broadcaster.getInstance(mBManager, mBAdapter, mBLEAdvertiser, mainThreadHandler);
            }
        }
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
        if (mBAdapter != null && !mBAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "No advertising support on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcaster != null)
            broadcaster.stopAdvertising();
    }

    public void panMap(View view) {
        /* first argument is the movement type
        second is the direction of panning, going CSS style
        1 - North
        2 - South
        3 - East
        4 - West
         */
        byte[] data = {Byte.parseByte(view.getTag().toString())};
        if (broadcaster != null)
            passUserInput((byte) 1, data);
    }

    public void passUserInput(byte type, byte[] info) {
        if (broadcaster != null)
            broadcaster.createPacketWithData(type, info);
    }

    public String getStatus() {
        return status;
    }
}