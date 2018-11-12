package dk.bachelor.via.holobachelor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import Broadcaster.Broadcaster;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity{
    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    static final int BEACON_ID = 1775;
    AdvertiseData data;
    public static Broadcaster broadcaster;

    public Broadcaster getBroadcaster() {
        return broadcaster;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                                                           @Override
                                                           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                                               Fragment selectedFragment = null;
                                                               switch (item.getItemId()){
                                                                   case R.id.navigation_navigation:
                                                                       selectedFragment = new NavigationFragment();
                                                                       break;

                                                                   case R.id.navigation_settings:
                                                                       break;
                                                               }
                                                               getSupportFragmentManager().beginTransaction().replace(R.id.frame, selectedFragment).commit();

                                                               return true;
                                                           }
        });



                navigation.getMenu().findItem(R.id.navigation_navigation).setChecked(true);

        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        // this is the view we will add the gesture detector to
        View myView = findViewById(R.id.gesture_view);


        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector

        mBManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (mBManager != null) {
            mBAdapter = mBManager.getAdapter();
        }
        mBLEAdvertiser = mBAdapter.getBluetoothLeAdvertiser();
        broadcaster = new Broadcaster(mBManager, mBAdapter, mBLEAdvertiser);

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
        if (!mBAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "No advertising support on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcaster.stopAdvertising();
    }

    public void panMap(View view){
        /* first argument is the movement type
        second is the direction of panning, going CSS style
        1 - North
        2 - East
        3 - South
        4 - West
         */
        broadcaster.createPacketWithData((byte) 1, Byte.parseByte(view.getTag().toString()));
    }

    // Gesture Control

}