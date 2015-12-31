package com.example.primael.robotcompanionapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Home extends AppCompatActivity
{
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    private ListView devicesListView;

    private Button rightButton;
    private Button leftButton;
    private Button forwardButton;
    private Button backwardButton;

    private BluetoothAdapter bluetoothAdapter;

    public BluetoothDevice selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        devicesListView = (ListView) findViewById(R.id.devicesListview);

        rightButton = (Button) findViewById(R.id.rightButton);
        leftButton = (Button) findViewById(R.id.leftButton);
        forwardButton = (Button) findViewById(R.id.forwardButton);
        backwardButton = (Button) findViewById(R.id.backwardButton);

        rightButton.setVisibility(View.GONE);
        leftButton.setVisibility(View.GONE);
        forwardButton.setVisibility(View.GONE);
        backwardButton.setVisibility(View.GONE);
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

            devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Iterator <BluetoothDevice> it = pairedDevices.iterator();

                    selectedDevice = devicesArray.get(position);

                    if (pairedDevices.contains(selectedDevice)) {
                        ConnectThread connect = new ConnectThread(selectedDevice);
                        connect.start();
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

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device)
        {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try
            {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e)
            {
                Toast.makeText(Home.this, "Unable to get UUID", Toast.LENGTH_SHORT).show();
            }
            mmSocket = tmp;
        }

        public void run()
        {
            try
            {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            }
            catch (IOException connectException)
            {
                //Toast.makeText(Home.this, "Something went wrong, Unable to connect", Toast.LENGTH_SHORT).show();
                // Unable to connect; close the socket and get out
                try
                {
                    mmSocket.close();
                }
                catch (IOException closeException)
                {
                    Toast.makeText(Home.this, "Unable to close Socket", Toast.LENGTH_SHORT).show();
                }
            }
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Toast.makeText(Home.this, "Unable to close Socket", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_CONNECT:

                    break;

                case MESSAGE_READ:

                    break;
            }
        }
    }
}