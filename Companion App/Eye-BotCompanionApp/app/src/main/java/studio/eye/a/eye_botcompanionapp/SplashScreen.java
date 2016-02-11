package studio.eye.a.eye_botcompanionapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SplashScreen extends Activity implements AdapterView.OnItemClickListener
{
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    ListView devicesListView;
    ArrayAdapter <String> devicesAdapter;

    static TextView stateTextView;

    Button btScan;
    Button doneButton;

    static BluetoothAdapter btAdapter;

    static BluetoothDevice selectedDevice;

    ArrayList<BluetoothDevice> availableDevicesArray;

    IntentFilter filter;

    BroadcastReceiver receiver;

    static boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
        buttonInit();

        if (btAdapter == null)
        {
            Toast.makeText(SplashScreen.this, "No Bluetooth detected", Toast.LENGTH_SHORT).show();
            finish ();
        }
        else
        {
            if (!btAdapter.isEnabled())
            {
                Intent intent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            }
            startDiscovery();
        }
    }

    public void startDiscovery()
    {
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    private void init ()
    {
        // ---------- Activity ----------
        devicesListView = (ListView) findViewById(R.id.devicesLV);
        devicesAdapter = new ArrayAdapter<>(this, R.layout.custom_textview);
        devicesListView.setAdapter(devicesAdapter);
        devicesListView.setOnItemClickListener(this);
        stateTextView = (TextView) findViewById(R.id.pleaseConnectTV);

        // ---------- Class ---------
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        availableDevicesArray = new ArrayList<>();

        receiver = new BroadcastReceiver ()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals (action))
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devicesAdapter.add(device.getName());
                    availableDevicesArray.add(device);
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals (action))
                {
                    btScan.setBackground (ContextCompat.getDrawable(context, R.drawable.ic_bluetooth_white_searching));
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals (action))
                {
                    btScan.setBackground (ContextCompat.getDrawable(context, R.drawable.ic_bluetooth_white));
                }
            }
        };

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        filter = new IntentFilter (BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver (receiver, filter);
        filter = new IntentFilter (BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver (receiver, filter);
    }

    private void buttonInit ()
    {
        btScan = (Button) findViewById(R.id.btScanBT);
        doneButton = (Button) findViewById(R.id.doneBT);

        btScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickAnimation(btScan);
                availableDevicesArray.clear();
                devicesAdapter.clear();
                startDiscovery();
                Toast.makeText(SplashScreen.this, "Scanning ...", Toast.LENGTH_SHORT).show();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                onClickAnimation(doneButton);
                Intent intent = new Intent(SplashScreen.this, MainMenu.class);

                if (isConnected) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(SplashScreen.this, "Please connect to your robot first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onDestroy ()
    {
        super.onDestroy();
        btAdapter.cancelDiscovery();

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
        {
            Toast.makeText (SplashScreen.this, "You won't be able to use this app without Bluetooth", Toast.LENGTH_SHORT).show ();
            finish();
        }
        else
        {
            startDiscovery();
        }
    }

    public void onItemClick (AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        if (btAdapter.isDiscovering())
        {
            btAdapter.cancelDiscovery();
        }

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);

        selectedDevice  = availableDevicesArray.get(arg2);
    }

    public void onClickAnimation(View v)
    {
        v.startAnimation(buttonClick);
    }
}
