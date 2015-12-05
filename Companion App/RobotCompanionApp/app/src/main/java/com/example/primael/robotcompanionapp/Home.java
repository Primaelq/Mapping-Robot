package com.example.primael.robotcompanionapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Home extends AppCompatActivity
{
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;

    private ListView devicesListView;
    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        devicesListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;

        if (resultCode == RESULT_OK) {
            BluetoothGetDevices();
        }
        else
        {
            Toast.makeText(Home.this, "You won't be able to use this app without bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    public void Bluetooth(View view)
    {
        CheckBluetoothAdapter();
        CheckBluetoothState();
    }

    private void BluetoothGetDevices ()
    {
        GetPairedDevices();
    }

    private void CheckBluetoothAdapter ()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null)
        {
            Toast.makeText(Home.this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
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
        ArrayAdapter<String> devicesAdapter;
        final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        final ArrayList<String> devicesArrayName;
        final ArrayList<BluetoothDevice> devicesArray;

        if (pairedDevices.size() > 0)
        {
            devicesArrayName = new ArrayList<>();
            devicesArray = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices)
            {
                devicesArrayName.add(device.getName());
                devicesArray.add(device);
            }

            devicesAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, devicesArrayName);
            devicesListView.setAdapter(devicesAdapter);

            devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick (AdapterView <?> parent, View view, int position, long id)
                {
                    //Iterator <BluetoothDevice> it = pairedDevices.iterator();

                    BluetoothDevice selectedDevice = devicesArray.get(position);

                    if (pairedDevices.contains(selectedDevice)){
                        Toast.makeText(Home.this, "Connecting to  " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                    }

                    /*while (it.hasNext())
                    {
                        selectedDevice = it.next();
                        if (selectedDevice.getName().equals(selectedDeviceName))
                        {
                            //Toast.makeText(Home.this, "Found " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }*/
                }
            });

        }
        else
        {
            Toast.makeText(Home.this, "No Paired Devices found", Toast.LENGTH_SHORT).show();
        }
    }

    //private ConnectThread extends

}