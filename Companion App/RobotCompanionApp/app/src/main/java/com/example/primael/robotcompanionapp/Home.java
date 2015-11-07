package com.example.primael.robotcompanionapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Home extends AppCompatActivity
{
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private Set<BluetoothDevice> pairedDevices = new HashSet<>();
    private Set<BluetoothDevice> newDevices = new HashSet<>();

    private BluetoothAdapter bluetoothAdapter;

    private TextView pairedText;

    private ListView devicesListView;
    private ArrayList<String> devicesArray;
    private ArrayAdapter<String> devicesAdapter;

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice devices = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                newDevices.add(devices);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                if (!newDevices.isEmpty())
                {
                    Toast.makeText(Home.this, "New Devices found", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Home.this, "No New Device found", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(Home.this, "Process Ended", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        devicesListView = (ListView) findViewById(R.id.pairedDevicesList);
        pairedText = (TextView) findViewById(R.id.pairedTxt);
        pairedText.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
            unregisterReceiver(bluetoothReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;

        if (resultCode == RESULT_OK)
        {
            BluetoothGetDevices();
        }
        else
        {
            Toast.makeText(Home.this, "You won't be able to use this app without bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    public void Bluetooth (View view)
    {
        newDevices.clear();

        pairedText.setVisibility(View.VISIBLE);

        CheckBluetoothAdapter();
        CheckBluetoothState();
        //MakeDeviceVisible();
    }

    private void BluetoothGetDevices () {
        GetPairedDevices();
        LookForNewDevices();

        //Toast.makeText(Home.this, "Process ended", Toast.LENGTH_SHORT).show();
    }

    private void CheckBluetoothAdapter ()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null)
        {
            Toast.makeText(Home.this, "No Bluetooth Adapter found", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(Home.this, "Bluetooth Adapter found", Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckBluetoothState ()
    {
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
        else
        {
            BluetoothGetDevices();
        }
    }

    private void GetPairedDevices ()
    {
        Toast.makeText(Home.this, "Retrieving Paired Devices list", Toast.LENGTH_SHORT).show();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        Toast.makeText(Home.this, "Done", Toast.LENGTH_SHORT).show();

        if (pairedDevices.size() > 0)
        {
            devicesArray = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices)
            {
                devicesArray.add(device.getName());
            }

            devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devicesArray);
            devicesListView.setAdapter(devicesAdapter);
        }
        else
        {
            Toast.makeText(Home.this, "No Paired Devices found", Toast.LENGTH_SHORT).show();
        }
    }

    private void LookForNewDevices ()
    {
        Toast.makeText(Home.this, "Looking for new devices", Toast.LENGTH_SHORT).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(bluetoothReceiver, filter);

        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }

    private void MakeDeviceVisible ()
    {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivity(discoverableIntent);
    }

}
