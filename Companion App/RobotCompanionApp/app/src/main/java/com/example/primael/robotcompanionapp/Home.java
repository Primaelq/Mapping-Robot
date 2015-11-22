package com.example.primael.robotcompanionapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Home extends AppCompatActivity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private Set<BluetoothDevice> pairedDevices = new HashSet<>();

    private BluetoothAdapter bluetoothAdapter;

    private ListView devicesListView;
    private ArrayList<String> devicesArray;
    private ArrayAdapter<String> devicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        devicesListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            devicesArray = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices)
            {
                devicesArray.add(device.getName());
            }

            devicesAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, devicesArray);
            devicesListView.setAdapter(devicesAdapter);


        }
        else
        {
            Toast.makeText(Home.this, "No Paired Devices found", Toast.LENGTH_SHORT).show();
        }
    }

}